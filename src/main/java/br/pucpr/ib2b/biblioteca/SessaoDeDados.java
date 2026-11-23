package br.pucpr.ib2b.biblioteca;

import br.pucpr.ib2b.persistencia.FabricaDePersistencia;
import br.pucpr.ib2b.persistencia.UnidadeDeTrabalho;

/**
 * Cliente da Abstract Factory: pede a UMA fabrica a unidade de trabalho e os tres repositorios,
 * e entrega os pontos internos Acervo, Relacionamento e Movimentacao ja ligados a eles.
 * Como tudo vem da mesma fabrica, nao ha como misturar memoria com MySQL na mesma operacao.
 */
public class SessaoDeDados {
    private final UnidadeDeTrabalho uow;
    private final AcervoDeLivros acervo;
    private final Relacionamento relacionamento;
    private final Movimentacao movimentacao;

    private SessaoDeDados(FabricaDePersistencia fabrica) {
        this.uow = fabrica.criarUnidadeDeTrabalho();
        this.acervo = new AcervoDeLivros(fabrica.criarRepositorioLivro(uow));
        this.relacionamento = new Relacionamento(fabrica.criarRepositorioRelacionamento(uow));
        this.movimentacao = new Movimentacao(fabrica.criarRepositorioMovimentacao(uow));
    }

    public static SessaoDeDados abrir(FabricaDePersistencia fabrica) {
        return new SessaoDeDados(fabrica);
    }

    public AcervoDeLivros acervo() { return acervo; }

    public Relacionamento relacionamento() { return relacionamento; }

    public Movimentacao movimentacao() { return movimentacao; }

    public void confirmar() { uow.confirmar(); }

    public void desfazer() { uow.desfazer(); }
}
