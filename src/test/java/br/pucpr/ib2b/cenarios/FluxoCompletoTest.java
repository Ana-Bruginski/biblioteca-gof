package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.circulacao.Laudo;
import br.pucpr.ib2b.dominio.Situacao;
import br.pucpr.ib2b.financeiro.Multa;
import br.pucpr.ib2b.financeiro.banco.Cobranca;
import br.pucpr.ib2b.financeiro.banco.SituacaoCobranca;
import br.pucpr.ib2b.solicitacao.SolicitacaoDeRenovacao;
import br.pucpr.ib2b.solicitacao.SolicitacaoDeReserva;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Fluxo da demonstracao: reserva, emprestimo, renovacao, devolucao atrasada, multa e pagamento. */
class FluxoCompletoTest extends CenarioBase {

    @Test
    void fluxoCompletoComDevolucaoAtrasadaMultaEPagamento() {
        sistema.emprestimos().submeter(SolicitacaoDeReserva.nova("balcao", "L3", ANA));
        assertTrue(sistema.emprestimos().emprestarLivro("L3", ANA).sucesso());
        sistema.emprestimos().submeter(SolicitacaoDeRenovacao.nova("app", "L3"));
        assertEquals(Situacao.RENOVADO, situacao("L3").situacao());

        relogio.avancarDias(7 + 30 + 4);            // 4 dias depois do vencimento renovado
        assertTrue(sistema.devolucoes().registrarDevolucao("L3").sucesso());

        Multa multa = sistema.multas().consultarMultas(ANA).get(0);
        assertEquals(new BigDecimal("4.00"), multa.valor());
        assertTrue(sistema.devolucoes().emitirBoletoDeMulta("L3", ANA).sucesso(), "f6: segunda via");

        assertTrue(sistema.administracao().simularPagamento(multa.idMulta()).sucesso());
        Cobranca cobranca = sistema.cobrancas().buscarPorId(multa.idMulta()).orElseThrow();
        assertEquals(SituacaoCobranca.QUITADA, cobranca.getSituacao());

        sistema.devolucoes().registrarInspecao("L3", Laudo.BOM_ESTADO);
        assertEquals(Situacao.LIVRE, situacao("L3").situacao());
        assertEquals(1, sistema.email().enviados().size());
    }
}
