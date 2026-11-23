package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.app.Configuracao;
import br.pucpr.ib2b.app.Sistema;
import br.pucpr.ib2b.dominio.Situacao;
import br.pucpr.ib2b.infra.RelogioAjustavel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Teste opcional da familia relacional contra um MySQL de verdade.
 * So roda com: mvn test -Dmysql.url=jdbc:mysql://localhost:3306/biblioteca_ib2b?createDatabaseIfNotExist=true
 * (usuario e senha: -Dmysql.usuario=... -Dmysql.senha=...). Sem a propriedade, e ignorado.
 */
class MySQLIntegracaoTest {

    @Test
    void perfilDeProducaoGravaELeNoMySQL() {
        assumeTrue(System.getProperty("mysql.url") != null, "MySQL nao configurado: teste ignorado");
        Sistema s = new Sistema(Configuracao.carregar().com("perfil", "PRODUCAO"), new RelogioAjustavel());
        String sufixo = UUID.randomUUID().toString().substring(0, 6);
        String cpf = String.valueOf(10_000_000_000L + Math.abs(UUID.randomUUID().getMostSignificantBits() % 89_999_999_999L));
        String livro = "IT-" + sufixo;

        assertTrue(s.pessoas().inserirPessoa(cpf, "Teste", "MySQL", "teste@exemplo.com").entidade() != null);
        assertNotNull(s.estudantes().inserirEstudante(cpf, "E-" + sufixo, "M-" + sufixo).entidade());
        assertNotNull(s.acervo().inserirLivro(livro, "Livro de integracao " + sufixo).entidade());
        assertTrue(s.emprestimos().reservarLivro(livro, cpf).sucesso());
        assertTrue(s.emprestimos().emprestarLivro(livro, cpf).sucesso());
        assertEquals(Situacao.EMPRESTADO, s.circulacao().consultar(livro).situacao());
        assertTrue(s.emprestimos().renovarEmprestimo(livro).sucesso());
        assertTrue(s.devolucoes().registrarDevolucao(livro).sucesso());
        assertEquals(Situacao.DEVOLVIDO, s.circulacao().consultar(livro).situacao());
    }
}
