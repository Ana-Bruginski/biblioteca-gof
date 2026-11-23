package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.circulacao.Laudo;
import br.pucpr.ib2b.devolucao.EventoDevolucao;
import br.pucpr.ib2b.dominio.Situacao;
import br.pucpr.ib2b.financeiro.Multa;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 5 - Reacoes independentes a devolucao - Observer. */
class R05_ReacoesADevolucaoObserverTest extends CenarioBase {

    @Test
    void R05_devolucaoAtrasadaAlimentaInspecaoMultaEIndicadores() {
        emprestar("L1", ANA);                       // vencimento = hoje + 7
        relogio.avancarDias(10);                    // 3 dias de atraso

        assertTrue(sistema.devolucoes().registrarDevolucao("L1").sucesso());

        assertTrue(sistema.agenda().estaPendente("L1"), "inspecao agendada");
        List<Multa> multas = sistema.multas().consultarMultas(ANA);
        assertEquals(1, multas.size(), "multa solicitada");
        assertEquals(3, multas.get(0).diasAtraso());
        assertEquals(new BigDecimal("3.00"), multas.get(0).valor());
        assertEquals(1, sistema.painel().devolucoesAtrasadas(), "indicadores atualizados");
        assertEquals(3, sistema.painel().totalDiasAtraso());
        assertEquals(Situacao.DEVOLVIDO, situacao("L1").situacao(), "continua DEVOLVIDO ate a inspecao");
    }

    @Test
    void R05_devolucaoNoPrazoNaoGeraMulta() {
        emprestar("L1", ANA);
        relogio.avancarDias(3);

        sistema.devolucoes().registrarDevolucao("L1");

        assertTrue(sistema.multas().consultarMultas(ANA).isEmpty());
        assertEquals(1, sistema.painel().devolucoes());
        assertEquals(0, sistema.painel().devolucoesAtrasadas());
        assertTrue(sistema.agenda().estaPendente("L1"));
    }

    @Test
    void R05_falhaDoEmailNaoAfetaOsDemaisNemDesfazADevolucao() {
        emprestar("L1", ANA);
        relogio.avancarDias(9);
        sistema.administracao().simularFalhaDeEmail(true);

        assertTrue(sistema.devolucoes().registrarDevolucao("L1").sucesso());

        assertEquals(Situacao.DEVOLVIDO, situacao("L1").situacao());
        assertEquals(1, sistema.multas().consultarMultas(ANA).size(), "multa gerada mesmo com o e-mail falhando");
        assertEquals(1, sistema.publicador().falhas().size());
        assertEquals("OuvinteEmail", sistema.publicador().falhas().get(0).ouvinte());
    }

    @Test
    void R05_novoOuvinteEIncluidoSemAlterarOTratadorEEventoLevaOsDados() {
        List<EventoDevolucao> recebidos = new ArrayList<>();
        sistema.publicador().registrar(recebidos::add);           // nova reacao: so registrar
        emprestar("L1", ANA);
        relogio.avancarDias(8);

        sistema.devolucoes().registrarDevolucao("L1");

        EventoDevolucao ev = recebidos.get(0);
        assertEquals(ANA, ev.cpfEstudante());
        assertEquals(relogio.hoje(), ev.devolvidoEm());
        assertEquals(relogio.hoje().minusDays(1), ev.vencimento());
        assertEquals(1, ev.diasAtraso());
    }

    @Test
    void R05_decisaoDaInspecaoTiraOLivroDeDevolvido() {
        emprestar("L1", ANA);
        sistema.devolucoes().registrarDevolucao("L1");

        sistema.devolucoes().registrarInspecao("L1", Laudo.BOM_ESTADO);

        assertEquals(Situacao.LIVRE, situacao("L1").situacao());
        assertFalse(sistema.agenda().estaPendente("L1"));
    }
}
