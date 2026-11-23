package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.app.Configuracao;
import br.pucpr.ib2b.app.Sistema;
import br.pucpr.ib2b.circulacao.SituacaoDTO;
import br.pucpr.ib2b.infra.RelogioAjustavel;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Monta um sistema no perfil TESTE (familia em memoria) com dados basicos:
 * Ana e Bia sao estudantes; L1 a L5 sao livros LIVRES. A data simulada comeca em 02/11/2026.
 */
abstract class CenarioBase {
    static final String ANA = "11111111111";
    static final String BIA = "33333333333";

    protected RelogioAjustavel relogio;
    protected Sistema sistema;

    @BeforeEach
    void montarSistema() {
        relogio = new RelogioAjustavel(LocalDateTime.of(2026, 11, 2, 10, 0));
        sistema = new Sistema(configuracao(), relogio);
        sistema.pessoas().inserirPessoa(ANA, "Ana", "Silva", "ana@exemplo.com");
        sistema.estudantes().inserirEstudante(ANA, "E-ANA", "2026001");
        sistema.pessoas().inserirPessoa(BIA, "Bia", "Souza", "bia@exemplo.com");
        sistema.estudantes().inserirEstudante(BIA, "E-BIA", "2026002");
        sistema.acervo().inserirLivro("L1", "Dom Casmurro");
        sistema.acervo().inserirLivro("L2", "Memorias Postumas de Bras Cubas");
        sistema.acervo().inserirLivro("L3", "Grande Sertao: Veredas");
        sistema.acervo().inserirLivro("L4", "Vidas Secas");
        sistema.acervo().inserirLivro("L5", "O Cortico");
    }

    /** Subclasses podem trocar a configuracao (ex.: banco B). */
    protected Configuracao configuracao() {
        return Configuracao.paraTeste();
    }

    /** Reserva e efetiva o emprestimo de um livro para o estudante. */
    protected void emprestar(String idLivro, String cpf) {
        assertTrue(sistema.emprestimos().reservarLivro(idLivro, cpf).sucesso());
        assertTrue(sistema.emprestimos().emprestarLivro(idLivro, cpf).sucesso());
    }

    protected SituacaoDTO situacao(String idLivro) {
        return sistema.circulacao().consultar(idLivro);
    }
}
