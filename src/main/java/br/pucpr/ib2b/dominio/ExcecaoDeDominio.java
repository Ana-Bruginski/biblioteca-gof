package br.pucpr.ib2b.dominio;

/** Base das recusas de regra de negocio. O tratador converte em Resultado de recusa. */
public class ExcecaoDeDominio extends RuntimeException {
    public ExcecaoDeDominio(String mensagem) {
        super(mensagem);
    }
}
