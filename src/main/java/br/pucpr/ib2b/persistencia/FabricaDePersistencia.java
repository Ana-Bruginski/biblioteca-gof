package br.pucpr.ib2b.persistencia;

/**
 * Padrao Abstract Factory - papel AbstractFactory.
 * Cria a unidade de trabalho e os tres repositorios de UMA mesma familia.
 * Os repositorios recebem a unidade de trabalho na criacao e escrevem por ela.
 */
public interface FabricaDePersistencia {
    UnidadeDeTrabalho criarUnidadeDeTrabalho();

    RepositorioLivro criarRepositorioLivro(UnidadeDeTrabalho uow);

    RepositorioMovimentacao criarRepositorioMovimentacao(UnidadeDeTrabalho uow);

    RepositorioRelacionamento criarRepositorioRelacionamento(UnidadeDeTrabalho uow);

    /** Nome da familia, usado em mensagens e testes. */
    String familia();

    /** Preparacao da familia na inicializacao (a relacional cria as tabelas). */
    default void inicializar() { }
}
