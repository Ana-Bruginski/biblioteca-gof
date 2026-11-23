package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.app.Configuracao;
import br.pucpr.ib2b.app.Sistema;
import br.pucpr.ib2b.financeiro.Multa;
import br.pucpr.ib2b.financeiro.ServicoDeMulta;
import br.pucpr.ib2b.financeiro.banco.AdaptadorBancoA;
import br.pucpr.ib2b.financeiro.banco.Cobranca;
import br.pucpr.ib2b.financeiro.banco.CodigoBanco;
import br.pucpr.ib2b.financeiro.banco.ConfirmacaoPagamento;
import br.pucpr.ib2b.financeiro.banco.GatewayDeCobranca;
import br.pucpr.ib2b.financeiro.banco.SituacaoCobranca;
import br.pucpr.ib2b.financeiro.banco.SolicitacaoCobranca;
import br.pucpr.ib2b.financeiro.banco.ValorInvalidoException;
import br.pucpr.ib2b.infra.RelogioAjustavel;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 3 - Integracao com bancos incompativeis - Adapter. */
class R03_BancosAdapterTest {

    private static Multa multaDeSeteReais() {
        return new Multa("MUL-7", "DEV-7", "L1", "11111111111", 7, new BigDecimal("7.00"), "ORIGINAL",
                LocalDateTime.of(2026, 11, 2, 10, 0));
    }

    private static Sistema sistemaComBanco(String banco) {
        return new Sistema(Configuracao.paraTeste().com("banco", banco), new RelogioAjustavel());
    }

    @Test
    void R03_seteReaisViramSetecentosCentavosNoBancoA() {
        Sistema s = sistemaComBanco("A");
        Cobranca c = s.servicoDeMulta().cobrar(multaDeSeteReais());

        assertEquals(700L, s.apiBancoA().ultimoValorEmCentavos());
        assertEquals(CodigoBanco.A, c.getBanco());
        assertEquals(new BigDecimal("7.00"), c.getValor());
    }

    @Test
    void R03_seteReaisViramSeteDecimalNoBancoB() {
        Sistema s = sistemaComBanco("B");
        Cobranca c = s.servicoDeMulta().cobrar(multaDeSeteReais());

        assertEquals("7.00", s.apiBancoB().ultimoValorDecimal().toPlainString());
        assertEquals("111.111.111-11", s.apiBancoB().ultimoDocumento(), "o adaptador B formata o documento");
        assertEquals(CodigoBanco.B, c.getBanco());
    }

    @Test
    void R03_servicoDeMultasDependeSoDoContratoSemCondicionalPorBanco() {
        for (Field f : ServicoDeMulta.class.getDeclaredFields()) {
            assertFalse(f.getType().getSimpleName().startsWith("Adaptador"), "campo " + f.getName());
            assertFalse(f.getType().getSimpleName().startsWith("ApiBanco"), "campo " + f.getName());
        }
        assertTrue(GatewayDeCobranca.class.isAssignableFrom(sistemaComBanco("A").gateway().getClass()));
        assertTrue(GatewayDeCobranca.class.isAssignableFrom(sistemaComBanco("B").gateway().getClass()));
    }

    @Test
    void R03_notificacaoRepetidaNaoGeraSegundaQuitacao() {
        Sistema s = sistemaComBanco("A");
        Cobranca c = s.servicoDeMulta().cobrar(multaDeSeteReais());
        String webhook = s.apiBancoA().liquidar(c.getIdExterno(), "PAG-1");
        AdaptadorBancoA adaptador = (AdaptadorBancoA) s.gateway();

        adaptador.receberWebhook(webhook);
        adaptador.receberWebhook(webhook);                     // reenvio da mesma notificacao

        assertEquals(1, s.quitacao().quantidadeDeQuitacoes());
        assertEquals(SituacaoCobranca.QUITADA, s.cobrancas().buscarPorId("MUL-7").orElseThrow().getSituacao());
        assertEquals(SituacaoCobranca.QUITADA, s.gateway().consultarSituacao(c), "situacao do banco traduzida");
    }

    @Test
    void R03_notificacaoDeCobrancaDesconhecidaNaoQuitaNada() {
        Sistema s = sistemaComBanco("A");
        s.quitacao().confirmar(new ConfirmacaoPagamento(CodigoBanco.A, "A9999", "PAG-X", new BigDecimal("1.00")));
        assertEquals(0, s.quitacao().quantidadeDeQuitacoes());
        assertEquals(1, s.quitacao().pendentesDeConciliacao().size());
    }

    @Test
    void R03_valorComMaisDeDuasCasasERecusadoAntesDeChamarOBanco() {
        Sistema s = sistemaComBanco("A");
        assertThrows(ValorInvalidoException.class,
                () -> s.gateway().emitirBoleto(new SolicitacaoCobranca("X", "11111111111", new BigDecimal("7.005"))));
        assertEquals(-1L, s.apiBancoA().ultimoValorEmCentavos());
    }
}
