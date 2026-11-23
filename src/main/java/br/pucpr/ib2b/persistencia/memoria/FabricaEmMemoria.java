package br.pucpr.ib2b.persistencia.memoria;

import br.pucpr.ib2b.persistencia.FabricaDePersistencia;
import br.pucpr.ib2b.persistencia.FamiliaIncompativelException;
import br.pucpr.ib2b.persistencia.RepositorioLivro;
import br.pucpr.ib2b.persistencia.RepositorioMovimentacao;
import br.pucpr.ib2b.persistencia.RepositorioRelacionamento;
import br.pucpr.ib2b.persistencia.UnidadeDeTrabalho;

/** Abstract Factory - ConcreteFactory da familia em memoria (perfil TESTE). */
public class FabricaEmMemoria implements FabricaDePersistencia {
    private final BaseEmMemoria base;

    public FabricaEmMemoria(BaseEmMemoria base) {
        this.base = base;
    }

    @Override
    public UnidadeDeTrabalho criarUnidadeDeTrabalho() {
        return new UnidadeDeTrabalhoEmMemoria(base);
    }

    @Override
    public RepositorioLivro criarRepositorioLivro(UnidadeDeTrabalho uow) {
        return new RepositorioLivroEmMemoria(daFamilia(uow));
    }

    @Override
    public RepositorioMovimentacao criarRepositorioMovimentacao(UnidadeDeTrabalho uow) {
        return new RepositorioMovimentacaoEmMemoria(daFamilia(uow));
    }

    @Override
    public RepositorioRelacionamento criarRepositorioRelacionamento(UnidadeDeTrabalho uow) {
        return new RepositorioRelacionamentoEmMemoria(daFamilia(uow));
    }

    @Override
    public String familia() { return "memoria"; }

    /** Impede misturar familias: so aceita unidade de trabalho criada por esta fabrica. */
    private UnidadeDeTrabalhoEmMemoria daFamilia(UnidadeDeTrabalho uow) {
        if (uow instanceof UnidadeDeTrabalhoEmMemoria m && m.pertenceA(base)) {
            return m;
        }
        throw new FamiliaIncompativelException("FabricaEmMemoria", uow);
    }
}
