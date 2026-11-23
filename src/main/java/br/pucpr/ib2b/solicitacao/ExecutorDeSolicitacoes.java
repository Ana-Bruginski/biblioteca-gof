package br.pucpr.ib2b.solicitacao;

import br.pucpr.ib2b.circulacao.iCirculacao;
import br.pucpr.ib2b.dominio.ExcecaoDeDominio;

/**
 * Padrao Command - papel Invoker.
 * Dispara a execucao e garante que o mesmo identificador nao seja executado duas vezes.
 */
public class ExecutorDeSolicitacoes {
    private final iCirculacao receptor;
    private final RegistroDeSolicitacoes registro;

    public ExecutorDeSolicitacoes(iCirculacao receptor, RegistroDeSolicitacoes registro) {
        this.receptor = receptor;
        this.registro = registro;
    }

    public ResultadoSolicitacao submeter(SolicitacaoDeCirculacao s) {
        if (!registro.reservarExecucao(s.getId())) {                 // id repetido: nao executa de novo
            return registro.resultadoDe(s.getId()).orElse(ResultadoSolicitacao.emProcessamento(s.getId()));
        }
        ResultadoSolicitacao r;
        try {
            r = s.executar(receptor);                                // regras ficam no receptor
        } catch (ExcecaoDeDominio e) {
            r = ResultadoSolicitacao.recusada(s.getId(), e.getMessage());   // recusa tambem e resultado final
        } catch (RuntimeException falhaTecnica) {
            registro.liberar(s.getId());
            throw falhaTecnica;
        }
        registro.concluir(s.getId(), r);
        return r;
    }
}
