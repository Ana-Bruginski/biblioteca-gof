package br.pucpr.ib2b.financeiro;

import br.pucpr.ib2b.financeiro.banco.Cobranca;
import br.pucpr.ib2b.financeiro.banco.GatewayDeCobranca;
import br.pucpr.ib2b.financeiro.banco.RegistroDeCobrancas;
import br.pucpr.ib2b.financeiro.banco.SolicitacaoCobranca;
import br.pucpr.ib2b.infra.Ids;
import br.pucpr.ib2b.infra.Relogio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Strategy - Context: delega o valor a politica vigente.
 * Adapter - Client: emite o boleto pelo contrato GatewayDeCobranca, sem saber qual banco esta por tras.
 * Nao ha nenhum if por politica nem por banco nesta classe.
 */
public class ServicoDeMulta {
    private final SeletorDePolitica seletor;
    private final GatewayDeCobranca gateway;
    private final RegistroDeCobrancas cobrancas;
    private final Relogio relogio;
    private final List<Multa> multas = new CopyOnWriteArrayList<>();

    public ServicoDeMulta(SeletorDePolitica seletor, GatewayDeCobranca gateway, RegistroDeCobrancas cobrancas,
                          Relogio relogio) {
        this.seletor = seletor;
        this.gateway = gateway;
        this.cobrancas = cobrancas;
        this.relogio = relogio;
    }

    /** Calcula com a politica vigente e, se o valor for positivo, cria a Multa e emite o boleto. */
    public Optional<Multa> gerarMulta(String cpf, String idDevolucao, String idLivro, int diasAtraso) {
        PoliticaDeMulta politica = seletor.vigente();
        BigDecimal valor = politica.calcular(diasAtraso);
        if (valor.signum() <= 0) {
            return Optional.empty();
        }
        Multa multa = new Multa(Ids.novo("MUL"), idDevolucao, idLivro, cpf, diasAtraso, valor, politica.id(),
                relogio.agora());
        multas.add(multa);
        cobrar(multa);
        return Optional.of(multa);
    }

    /** Emissao do boleto: igual para qualquer politica e qualquer banco. */
    public Cobranca cobrar(Multa multa) {
        Cobranca c = gateway.emitirBoleto(new SolicitacaoCobranca(multa.idMulta(), multa.cpfEstudante(), multa.valor()));
        cobrancas.registrar(c);
        return c;
    }

    public List<Multa> multas() { return List.copyOf(multas); }
}
