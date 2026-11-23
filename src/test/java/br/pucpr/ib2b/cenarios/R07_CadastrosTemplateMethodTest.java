package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.cadastro.DadosEstudante;
import br.pucpr.ib2b.cadastro.ResultadoCadastro;
import br.pucpr.ib2b.cadastro.StatusCadastro;
import br.pucpr.ib2b.cadastro.TratadorDeCadastro;
import br.pucpr.ib2b.cadastro.TratadorDeEstudante;
import br.pucpr.ib2b.dominio.Estudante;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 7 - Rotina comum para cadastros - Template Method. */
class R07_CadastrosTemplateMethodTest extends CenarioBase {

    @Test
    void R07_estudanteDuplicadoRetornaOCadastroExistente() {
        ResultadoCadastro<Estudante> r = sistema.estudantes().inserirEstudante(ANA, "OUTRO-ID", "9999");

        assertEquals(StatusCadastro.EXISTENTE, r.status());
        assertEquals("E-ANA", r.entidade().getId(), "devolve o cadastro original, sem alterar");
        assertEquals("2026001", r.entidade().getMatricula());
    }

    @Test
    void R07_estudanteComCpfNaoCadastradoFalhaAntesDeQualquerGravacao() {
        int[] chamadas = new int[2];     // [0] = criar, [1] = armazenar
        TratadorDeEstudante espiao = new TratadorDeEstudante(sistema.fabrica(), sistema.pessoas()) {
            @Override
            protected Estudante criar(DadosEstudante d) {
                chamadas[0]++;
                return super.criar(d);
            }

            @Override
            protected void armazenar(Estudante e) {
                chamadas[1]++;
                super.armazenar(e);
            }
        };

        ResultadoCadastro<Estudante> r = espiao.inserirEstudante("99999999999", "E-X", "0001");

        assertEquals(StatusCadastro.RECUSADO, r.status());
        assertTrue(r.motivo().contains("nao esta cadastrada"));
        assertArrayEquals(new int[]{0, 0}, chamadas, "criar e armazenar nao podem rodar");
        assertTrue(sistema.estudantes().consultarEstudante("99999999999").isEmpty());
    }

    @Test
    void R07_atendenteTambemExigePessoaCadastrada() {
        assertEquals(StatusCadastro.RECUSADO, sistema.atendentes().inserirAtendente("88888888888", "A9", "X").status());
        assertEquals(StatusCadastro.CRIADO, sistema.atendentes().inserirAtendente(BIA, "A1", "Circulacao").status());
    }

    @Test
    void R07_metodoModeloEFinalEOsPassosVariaveisSaoProtegidos() throws Exception {
        assertTrue(Modifier.isFinal(TratadorDeCadastro.class.getMethod("incluir", Object.class).getModifiers()));
        for (String passo : new String[]{"buscarExistente", "validar", "criar"}) {
            assertTrue(Modifier.isAbstract(TratadorDeCadastro.class.getDeclaredMethod(passo, Object.class)
                    .getModifiers()), passo);
        }
    }

    @Test
    void R07_validacoesPropriasDeCadaCadastro() {
        assertEquals(StatusCadastro.RECUSADO, sistema.pessoas().inserirPessoa("123", "X", "Y", "x@y").status());
        assertEquals(StatusCadastro.RECUSADO, sistema.acervo().inserirLivro("L9", " ").status());
        assertEquals(StatusCadastro.EXISTENTE, sistema.acervo().inserirLivro("L1", "Outro titulo").status());
    }
}
