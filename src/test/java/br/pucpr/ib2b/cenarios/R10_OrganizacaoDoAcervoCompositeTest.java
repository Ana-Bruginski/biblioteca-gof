package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.biblioteca.SessaoDeDados;
import br.pucpr.ib2b.circulacao.SituacaoDTO;
import br.pucpr.ib2b.organizacao.GrupoDoAcervo;
import br.pucpr.ib2b.organizacao.HierarquiaInvalidaException;
import br.pucpr.ib2b.organizacao.ItemDoAcervo;
import br.pucpr.ib2b.organizacao.OrganizadorDoAcervo;
import br.pucpr.ib2b.organizacao.TipoDeGrupo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 10 - Organizacao hierarquica do acervo - Composite. */
class R10_OrganizacaoDoAcervoCompositeTest extends CenarioBase {

    private OrganizadorDoAcervo organizar() {
        OrganizadorDoAcervo o = new OrganizadorDoAcervo();
        o.criarGrupo("Literatura", TipoDeGrupo.UNIDADE, "Acervo iB2B");
        o.criarGrupo("Romance", TipoDeGrupo.SECAO, "Literatura");
        o.criarGrupo("Regionalismo", TipoDeGrupo.SECAO, "Literatura");
        o.adicionarLivro("L1", "Romance");
        o.adicionarLivro("L2", "Romance");
        o.adicionarLivro("L5", "Romance");
        o.adicionarLivro("L3", "Regionalismo");
        o.adicionarLivro("L4", "Regionalismo");
        return o;
    }

    @Test
    void R10_unidadeComDuasSecoesDeTresEDoisLivrosInformaCinco() {
        OrganizadorDoAcervo o = organizar();

        assertEquals(5, o.contarLivros("Literatura"));
        assertEquals(3, o.contarLivros("Romance"));
        assertEquals(2, o.contarLivros("Regionalismo"));
        assertEquals(1, new ItemDoAcervo("L9").contarLivros(), "folha responde a mesma operacao");
        assertEquals(5, o.grupo("Literatura").listarIds().size());
    }

    @Test
    void R10_moverLivroEntreSecoesNaoAlteraIdEstadoNemEmprestimo() {
        emprestar("L1", ANA);
        SituacaoDTO antes = situacao("L1");
        OrganizadorDoAcervo o = organizar();

        o.mover("L1", "Regionalismo");

        assertEquals(2, o.contarLivros("Romance"));
        assertEquals(3, o.contarLivros("Regionalismo"));
        assertEquals(5, o.contarLivros("Literatura"));
        assertEquals(antes, situacao("L1"), "id, estado e vencimento iguais");
        SessaoDeDados s = SessaoDeDados.abrir(sistema.fabrica());
        assertTrue(s.movimentacao().buscarEmprestimoAtivo("L1").isPresent(), "emprestimo intacto");
        s.desfazer();
    }

    @Test
    void R10_grupoRecusaCiclos() {
        OrganizadorDoAcervo o = organizar();
        GrupoDoAcervo literatura = o.grupo("Literatura");
        GrupoDoAcervo romance = o.grupo("Romance");
        literatura.getPai().remover(literatura);       // solta Literatura para tentar pendura-la em baixo

        assertThrows(HierarquiaInvalidaException.class, () -> romance.adicionar(literatura));
        assertThrows(HierarquiaInvalidaException.class, () -> literatura.adicionar(literatura));
    }

    @Test
    void R10_grupoRecusaLivroQueJaPertenceAOutroGrupo() {
        OrganizadorDoAcervo o = organizar();
        assertThrows(HierarquiaInvalidaException.class, () -> o.adicionarLivro("L1", "Regionalismo"));
        assertEquals(5, o.contarLivros("Literatura"));
    }

    @Test
    void R10_moverParaGrupoInexistenteMantemOItemNaOrigem() {
        OrganizadorDoAcervo o = organizar();
        assertThrows(HierarquiaInvalidaException.class, () -> o.mover("L1", "Poesia"));
        assertEquals(3, o.contarLivros("Romance"));
    }
}
