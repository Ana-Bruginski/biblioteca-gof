package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.dominio.Situacao;
import br.pucpr.ib2b.solicitacao.ResultadoSolicitacao;
import br.pucpr.ib2b.solicitacao.SolicitacaoDeRenovacao;
import br.pucpr.ib2b.solicitacao.SolicitacaoDeReserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 6 - Solicitacoes de circulacao como objetos - Command. */
class R06_SolicitacoesCommandTest extends CenarioBase {

    @Test
    void R06_renovacaoExecutadaDuasVezesComOMesmoIdAcrescentaApenas30Dias() {
        emprestar("L1", ANA);
        LocalDate vencimentoInicial = situacao("L1").vencimento();
        SolicitacaoDeRenovacao s42 = SolicitacaoDeRenovacao.nova("app do estudante", "L1");

        ResultadoSolicitacao primeira = sistema.emprestimos().submeter(s42);
        ResultadoSolicitacao reenvio = sistema.emprestimos().submeter(s42);     // mesmo id

        assertEquals(ResultadoSolicitacao.Status.EXECUTADA, primeira.status());
        assertEquals(primeira, reenvio, "o reenvio recebe o resultado gravado");
        assertEquals(vencimentoInicial.plusDays(30), situacao("L1").vencimento());
        assertEquals(1, situacao("L1").numRenovacoes());
    }

    @Test
    void R06_solicitacoesDiferentesContinuamSujeitasAoLimiteDeDuas() {
        emprestar("L1", ANA);
        LocalDate vencimentoInicial = situacao("L1").vencimento();

        ResultadoSolicitacao r1 = sistema.emprestimos().submeter(SolicitacaoDeRenovacao.nova("balcao", "L1"));
        ResultadoSolicitacao r2 = sistema.emprestimos().submeter(SolicitacaoDeRenovacao.nova("app", "L1"));
        ResultadoSolicitacao r3 = sistema.emprestimos().submeter(SolicitacaoDeRenovacao.nova("app", "L1"));

        assertEquals(ResultadoSolicitacao.Status.EXECUTADA, r1.status());
        assertEquals(ResultadoSolicitacao.Status.EXECUTADA, r2.status());
        assertEquals(ResultadoSolicitacao.Status.RECUSADA, r3.status());
        assertEquals(vencimentoInicial.plusDays(60), situacao("L1").vencimento());
    }

    @Test
    void R06_submissoesSimultaneasDoMesmoIdExecutamUmaUnicaVez() throws Exception {
        emprestar("L1", ANA);
        SolicitacaoDeRenovacao mesma = SolicitacaoDeRenovacao.nova("app", "L1");
        ExecutorService threads = Executors.newFixedThreadPool(8);
        List<Callable<ResultadoSolicitacao>> tarefas = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            tarefas.add(() -> sistema.emprestimos().submeter(mesma));
        }

        threads.invokeAll(tarefas);
        threads.shutdown();

        assertEquals(1, situacao("L1").numRenovacoes());
    }

    @Test
    void R06_reservaTambemESolicitacaoComIdAutorEInstante() {
        SolicitacaoDeReserva s = SolicitacaoDeReserva.nova("tela de balcao", "L2", BIA);
        assertNotNull(s.getId());
        assertEquals("tela de balcao", s.getAutor());
        assertNotNull(s.getCriadaEm());

        ResultadoSolicitacao r = sistema.emprestimos().submeter(s);

        assertEquals(ResultadoSolicitacao.Status.EXECUTADA, r.status());
        assertEquals(Situacao.RESERVADO, situacao("L2").situacao());
        assertEquals(r, sistema.emprestimos().submeter(s));
    }
}
