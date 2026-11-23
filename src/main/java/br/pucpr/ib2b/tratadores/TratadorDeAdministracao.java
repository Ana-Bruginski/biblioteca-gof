package br.pucpr.ib2b.tratadores;

import br.pucpr.ib2b.consulta.RegistroDeMetricas;
import br.pucpr.ib2b.devolucao.AgendaDeInspecao;
import br.pucpr.ib2b.devolucao.PainelDeIndicadores;
import br.pucpr.ib2b.devolucao.PublicadorDeDevolucao;
import br.pucpr.ib2b.devolucao.ServicoDeEmailSimulado;
import br.pucpr.ib2b.financeiro.SeletorDePolitica;
import br.pucpr.ib2b.financeiro.ServicoDeSimulacao;
import br.pucpr.ib2b.financeiro.banco.BancoSimulavel;
import br.pucpr.ib2b.financeiro.banco.RegistroDeCobrancas;
import br.pucpr.ib2b.infra.RelogioAjustavel;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Tratador do Administrador para configuracao, simulacoes e acompanhamento.
 * Inclui apoios de demonstracao: adiantar o relogio e simular o pagamento no banco.
 */
public class TratadorDeAdministracao {
    private final SeletorDePolitica seletor;
    private final ServicoDeSimulacao simulacao;
    private final RelogioAjustavel relogio;
    private final BancoSimulavel banco;
    private final RegistroDeCobrancas cobrancas;
    private final PainelDeIndicadores painel;
    private final AgendaDeInspecao agenda;
    private final ServicoDeEmailSimulado email;
    private final PublicadorDeDevolucao publicador;
    private final RegistroDeMetricas metricas;

    public TratadorDeAdministracao(SeletorDePolitica seletor, ServicoDeSimulacao simulacao, RelogioAjustavel relogio,
                                   BancoSimulavel banco, RegistroDeCobrancas cobrancas, PainelDeIndicadores painel,
                                   AgendaDeInspecao agenda, ServicoDeEmailSimulado email,
                                   PublicadorDeDevolucao publicador, RegistroDeMetricas metricas) {
        this.seletor = seletor;
        this.simulacao = simulacao;
        this.relogio = relogio;
        this.banco = banco;
        this.cobrancas = cobrancas;
        this.painel = painel;
        this.agenda = agenda;
        this.email = email;
        this.publicador = publicador;
        this.metricas = metricas;
    }

    public Resultado definirPoliticaDeMulta(String id) {
        try {
            seletor.definirVigente(id);
            return Resultado.ok("politica vigente: " + id);
        } catch (IllegalArgumentException e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    public String politicaVigente() { return seletor.vigente().id(); }

    public Map<String, BigDecimal> simularMultas(int diasAtraso) { return simulacao.simular(diasAtraso); }

    public Resultado avancarDias(int dias) {
        relogio.avancarDias(dias);
        return Resultado.ok("data simulada: " + relogio.hoje());
    }

    public String hoje() { return relogio.hoje().toString(); }

    public Resultado simularPagamento(String idCobranca) {
        return cobrancas.buscarPorId(idCobranca).map(c -> {
            banco.simularPagamento(c, "PAG-" + idCobranca);
            return Resultado.ok("notificacao do banco recebida: " + cobrancas.buscarPorId(idCobranca).orElseThrow());
        }).orElse(Resultado.recusado("cobranca inexistente: " + idCobranca));
    }

    public void simularFalhaDeEmail(boolean falhar) { email.simularFalha(falhar); }

    public String painel() {
        return "Indicadores: " + painel
                + "\nInspecoes pendentes: " + agenda.pendentes()
                + "\nE-mails enviados: " + email.enviados().size()
                + "\nFalhas de ouvintes (fila de reenvio): " + publicador.falhas()
                + "\nConsultas medidas: " + metricas.quantidade();
    }
}
