package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.dominio.Livro;
import br.pucpr.ib2b.persistencia.ConfiguradorDePersistencia;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;
import br.pucpr.ib2b.persistencia.FamiliaIncompativelException;
import br.pucpr.ib2b.persistencia.Perfil;
import br.pucpr.ib2b.persistencia.RepositorioLivro;
import br.pucpr.ib2b.persistencia.UnidadeDeTrabalho;
import br.pucpr.ib2b.persistencia.memoria.FabricaEmMemoria;
import br.pucpr.ib2b.persistencia.memoria.RepositorioLivroEmMemoria;
import br.pucpr.ib2b.persistencia.memoria.RepositorioMovimentacaoEmMemoria;
import br.pucpr.ib2b.persistencia.memoria.RepositorioRelacionamentoEmMemoria;
import br.pucpr.ib2b.persistencia.memoria.UnidadeDeTrabalhoEmMemoria;
import br.pucpr.ib2b.persistencia.mysql.FabricaMySQL;
import br.pucpr.ib2b.persistencia.mysql.ParametrosMySQL;
import br.pucpr.ib2b.persistencia.mysql.RepositorioLivroMySQL;
import br.pucpr.ib2b.persistencia.mysql.RepositorioMovimentacaoMySQL;
import br.pucpr.ib2b.persistencia.mysql.RepositorioRelacionamentoMySQL;
import br.pucpr.ib2b.persistencia.mysql.UnidadeDeTrabalhoMySQL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 8 - Familias compativeis de acesso aos dados - Abstract Factory. */
class R08_FamiliasDePersistenciaAbstractFactoryTest {

    private static final ParametrosMySQL MYSQL =
            new ParametrosMySQL("jdbc:mysql://localhost:3306/biblioteca_ib2b", "root", "root");

    @Test
    void R08_configuracaoDeTesteForneceApenasObjetosEmMemoria() {
        FabricaDePersistencia f = ConfiguradorDePersistencia.fabricaPara(Perfil.TESTE, MYSQL);
        UnidadeDeTrabalho uow = f.criarUnidadeDeTrabalho();

        RepositorioLivroEmMemoria livros = assertInstanceOf(RepositorioLivroEmMemoria.class, f.criarRepositorioLivro(uow));
        RepositorioMovimentacaoEmMemoria mov =
                assertInstanceOf(RepositorioMovimentacaoEmMemoria.class, f.criarRepositorioMovimentacao(uow));
        RepositorioRelacionamentoEmMemoria rel =
                assertInstanceOf(RepositorioRelacionamentoEmMemoria.class, f.criarRepositorioRelacionamento(uow));

        assertInstanceOf(FabricaEmMemoria.class, f);
        assertInstanceOf(UnidadeDeTrabalhoEmMemoria.class, uow);
        assertSame(uow, livros.unidade());
        assertSame(uow, mov.unidade());
        assertSame(uow, rel.unidade());
    }

    @Test
    void R08_configuracaoDeProducaoForneceRepositoriosMySQLQueCompartilhamAMesmaUnidade() {
        FabricaDePersistencia f = ConfiguradorDePersistencia.fabricaPara(Perfil.PRODUCAO, MYSQL);
        UnidadeDeTrabalho uow = f.criarUnidadeDeTrabalho();       // a conexao so abre no primeiro uso

        RepositorioLivroMySQL livros = assertInstanceOf(RepositorioLivroMySQL.class, f.criarRepositorioLivro(uow));
        RepositorioMovimentacaoMySQL mov =
                assertInstanceOf(RepositorioMovimentacaoMySQL.class, f.criarRepositorioMovimentacao(uow));
        RepositorioRelacionamentoMySQL rel =
                assertInstanceOf(RepositorioRelacionamentoMySQL.class, f.criarRepositorioRelacionamento(uow));

        assertInstanceOf(FabricaMySQL.class, f);
        assertInstanceOf(UnidadeDeTrabalhoMySQL.class, uow);
        assertSame(uow, livros.unidade());
        assertSame(uow, mov.unidade());
        assertSame(uow, rel.unidade());
    }

    @Test
    void R08_naoEPossivelMisturarRepositorioDeUmaFamiliaComUnidadeDeOutra() {
        FabricaDePersistencia memoria = ConfiguradorDePersistencia.fabricaPara(Perfil.TESTE, MYSQL);
        FabricaDePersistencia mysql = ConfiguradorDePersistencia.fabricaPara(Perfil.PRODUCAO, MYSQL);

        assertThrows(FamiliaIncompativelException.class,
                () -> mysql.criarRepositorioLivro(memoria.criarUnidadeDeTrabalho()));
        assertThrows(FamiliaIncompativelException.class,
                () -> memoria.criarRepositorioMovimentacao(mysql.criarUnidadeDeTrabalho()));
    }

    @Test
    void R08_perfilAusenteInterrompeEmVezDeEscolherPorOmissao() {
        assertThrows(IllegalStateException.class, () -> ConfiguradorDePersistencia.fabricaPara(null, MYSQL));
    }

    @Test
    void R08_desfazerDescartaTodasAsEscritasDaUnidade() {
        FabricaDePersistencia f = ConfiguradorDePersistencia.fabricaPara(Perfil.TESTE, MYSQL);
        UnidadeDeTrabalho uow = f.criarUnidadeDeTrabalho();
        f.criarRepositorioLivro(uow).salvar(new Livro("X1", "Rascunho"));
        uow.desfazer();

        UnidadeDeTrabalho nova = f.criarUnidadeDeTrabalho();
        RepositorioLivro livros = f.criarRepositorioLivro(nova);
        assertTrue(livros.buscar("X1").isEmpty());
    }
}
