package br.pucpr.ib2b.app;

import br.pucpr.ib2b.cadastro.TratadorDeAcervo;
import br.pucpr.ib2b.cadastro.TratadorDeAtendente;
import br.pucpr.ib2b.cadastro.TratadorDeEstudante;
import br.pucpr.ib2b.cadastro.TratadorDePessoa;
import br.pucpr.ib2b.circulacao.ServicoDeCirculacao;
import br.pucpr.ib2b.circulacao.iCirculacao;
import br.pucpr.ib2b.consulta.ConsultaAcervo;
import br.pucpr.ib2b.consulta.ConsultaAcervoBasica;
import br.pucpr.ib2b.consulta.MontadorDeConsulta;
import br.pucpr.ib2b.consulta.RegistroDeMetricas;
import br.pucpr.ib2b.devolucao.AgendaDeInspecao;
import br.pucpr.ib2b.devolucao.OuvinteEmail;
import br.pucpr.ib2b.devolucao.OuvinteIndicadores;
import br.pucpr.ib2b.devolucao.OuvinteInspecao;
import br.pucpr.ib2b.devolucao.OuvinteMulta;
import br.pucpr.ib2b.devolucao.PainelDeIndicadores;
import br.pucpr.ib2b.devolucao.PublicadorDeDevolucao;
import br.pucpr.ib2b.devolucao.ServicoDeEmailSimulado;
import br.pucpr.ib2b.financeiro.ConfiguracaoAdministrativa;
import br.pucpr.ib2b.financeiro.MultaComTeto;
import br.pucpr.ib2b.financeiro.MultaComTolerancia;
import br.pucpr.ib2b.financeiro.MultaDiaria;
import br.pucpr.ib2b.financeiro.SeletorDePolitica;
import br.pucpr.ib2b.financeiro.ServicoDeMulta;
import br.pucpr.ib2b.financeiro.ServicoDeSimulacao;
import br.pucpr.ib2b.financeiro.TratadorDeMulta;
import br.pucpr.ib2b.financeiro.banco.AdaptadorBancoA;
import br.pucpr.ib2b.financeiro.banco.AdaptadorBancoB;
import br.pucpr.ib2b.financeiro.banco.ApiBancoA;
import br.pucpr.ib2b.financeiro.banco.ApiBancoB;
import br.pucpr.ib2b.financeiro.banco.BancoSimulavel;
import br.pucpr.ib2b.financeiro.banco.GatewayDeCobranca;
import br.pucpr.ib2b.financeiro.banco.RegistroDeCobrancas;
import br.pucpr.ib2b.financeiro.banco.ServicoDeQuitacao;
import br.pucpr.ib2b.infra.RelogioAjustavel;
import br.pucpr.ib2b.organizacao.OrganizadorDoAcervo;
import br.pucpr.ib2b.persistencia.ConfiguradorDePersistencia;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;
import br.pucpr.ib2b.solicitacao.ExecutorDeSolicitacoes;
import br.pucpr.ib2b.solicitacao.RegistroDeSolicitacoes;
import br.pucpr.ib2b.tratadores.TratadorDeAdministracao;
import br.pucpr.ib2b.tratadores.TratadorDeDevolucao;
import br.pucpr.ib2b.tratadores.TratadorDeEmprestimo;
import br.pucpr.ib2b.tratadores.TratadorDeOrganizacao;

/**
 * Montagem da aplicacao (composition root). E o UNICO lugar que conhece as classes concretas:
 * - escolhe a familia de persistencia pelo perfil (Abstract Factory);
 * - escolhe o adaptador do banco da instalacao (Adapter);
 * - registra as politicas de multa (Strategy) e os ouvintes da devolucao (Observer);
 * - monta a cadeia de decoradores da consulta (Decorator).
 */
public class Sistema {
    // infraestrutura e componentes internos
    private final RelogioAjustavel relogio;
    private final FabricaDePersistencia fabrica;
    private final iCirculacao circulacao;
    private final ApiBancoA apiBancoA = new ApiBancoA();
    private final ApiBancoB apiBancoB = new ApiBancoB();
    private final RegistroDeCobrancas cobrancas = new RegistroDeCobrancas();
    private final ServicoDeQuitacao quitacao = new ServicoDeQuitacao(cobrancas);
    private final GatewayDeCobranca gateway;
    private final ConfiguracaoAdministrativa configuracaoAdministrativa = new ConfiguracaoAdministrativa();
    private final SeletorDePolitica seletor = new SeletorDePolitica(configuracaoAdministrativa);
    private final ServicoDeMulta servicoDeMulta;
    private final PublicadorDeDevolucao publicador = new PublicadorDeDevolucao();
    private final AgendaDeInspecao agenda = new AgendaDeInspecao();
    private final PainelDeIndicadores painel = new PainelDeIndicadores();
    private final ServicoDeEmailSimulado email = new ServicoDeEmailSimulado();
    private final RegistroDeMetricas metricas = new RegistroDeMetricas();
    private final OrganizadorDoAcervo organizador = new OrganizadorDoAcervo();

    // tratadores: a interface so conhece estes
    private final TratadorDePessoa tratadorDePessoa;
    private final TratadorDeEstudante tratadorDeEstudante;
    private final TratadorDeAtendente tratadorDeAtendente;
    private final TratadorDeAcervo tratadorDeAcervo;
    private final TratadorDeEmprestimo tratadorDeEmprestimo;
    private final TratadorDeDevolucao tratadorDeDevolucao;
    private final TratadorDeMulta tratadorDeMulta;
    private final TratadorDeOrganizacao tratadorDeOrganizacao;
    private final TratadorDeAdministracao tratadorDeAdministracao;

    public Sistema(Configuracao cfg, RelogioAjustavel relogio) {
        this.relogio = relogio;

        // Abstract Factory: familia escolhida uma unica vez, pelo perfil
        this.fabrica = ConfiguradorDePersistencia.fabricaPara(cfg.perfil(), cfg.mysql());
        fabrica.inicializar();

        // Facade
        this.circulacao = new ServicoDeCirculacao(fabrica, relogio);

        // Adapter: o banco da instalacao e decidido aqui, por configuracao
        this.gateway = switch (cfg.banco()) {
            case A -> new AdaptadorBancoA(apiBancoA, quitacao);
            case B -> new AdaptadorBancoB(apiBancoB, quitacao);
        };

        // Strategy: politicas disponiveis e a vigente
        seletor.registrar(new MultaDiaria());
        seletor.registrar(new MultaComTeto());
        seletor.registrar(new MultaComTolerancia());
        seletor.definirVigente(cfg.politicaInicial());
        this.servicoDeMulta = new ServicoDeMulta(seletor, gateway, cobrancas, relogio);
        this.tratadorDeMulta = new TratadorDeMulta(servicoDeMulta, cobrancas);

        // Observer: reacoes a devolucao
        publicador.registrar(new OuvinteInspecao(agenda));
        publicador.registrar(new OuvinteMulta(tratadorDeMulta));
        publicador.registrar(new OuvinteIndicadores(painel));
        publicador.registrar(new OuvinteEmail(email));

        // Decorator: cadeia de consulta conforme a instalacao
        ConsultaAcervo consulta = MontadorDeConsulta.montar(new ConsultaAcervoBasica(fabrica), cfg.consulta(),
                metricas, relogio);

        // Template Method: tratadores de cadastro
        this.tratadorDePessoa = new TratadorDePessoa(fabrica);
        this.tratadorDeEstudante = new TratadorDeEstudante(fabrica, tratadorDePessoa);
        this.tratadorDeAtendente = new TratadorDeAtendente(fabrica, tratadorDePessoa);
        this.tratadorDeAcervo = new TratadorDeAcervo(fabrica, consulta);

        // Command + tratadores de circulacao
        ExecutorDeSolicitacoes executor = new ExecutorDeSolicitacoes(circulacao, new RegistroDeSolicitacoes());
        this.tratadorDeEmprestimo = new TratadorDeEmprestimo(circulacao, executor);
        this.tratadorDeDevolucao = new TratadorDeDevolucao(circulacao, publicador, agenda, tratadorDeMulta);

        // Composite
        this.tratadorDeOrganizacao = new TratadorDeOrganizacao(organizador, tratadorDeAcervo);

        this.tratadorDeAdministracao = new TratadorDeAdministracao(seletor, new ServicoDeSimulacao(seletor), relogio,
                (BancoSimulavel) gateway, cobrancas, painel, agenda, email, publicador, metricas);
    }

    public TratadorDePessoa pessoas() { return tratadorDePessoa; }
    public TratadorDeEstudante estudantes() { return tratadorDeEstudante; }
    public TratadorDeAtendente atendentes() { return tratadorDeAtendente; }
    public TratadorDeAcervo acervo() { return tratadorDeAcervo; }
    public TratadorDeEmprestimo emprestimos() { return tratadorDeEmprestimo; }
    public TratadorDeDevolucao devolucoes() { return tratadorDeDevolucao; }
    public TratadorDeMulta multas() { return tratadorDeMulta; }
    public TratadorDeOrganizacao organizacao() { return tratadorDeOrganizacao; }
    public TratadorDeAdministracao administracao() { return tratadorDeAdministracao; }

    // acesso a componentes internos, usado pelos testes de verificacao
    public FabricaDePersistencia fabrica() { return fabrica; }
    public iCirculacao circulacao() { return circulacao; }
    public RelogioAjustavel relogio() { return relogio; }
    public AgendaDeInspecao agenda() { return agenda; }
    public PainelDeIndicadores painel() { return painel; }
    public ServicoDeEmailSimulado email() { return email; }
    public PublicadorDeDevolucao publicador() { return publicador; }
    public ServicoDeMulta servicoDeMulta() { return servicoDeMulta; }
    public ServicoDeQuitacao quitacao() { return quitacao; }
    public RegistroDeCobrancas cobrancas() { return cobrancas; }
    public ApiBancoA apiBancoA() { return apiBancoA; }
    public ApiBancoB apiBancoB() { return apiBancoB; }
    public GatewayDeCobranca gateway() { return gateway; }
    public RegistroDeMetricas metricas() { return metricas; }
    public OrganizadorDoAcervo organizador() { return organizador; }
}
