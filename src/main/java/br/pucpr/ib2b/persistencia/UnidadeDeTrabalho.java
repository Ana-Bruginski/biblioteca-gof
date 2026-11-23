package br.pucpr.ib2b.persistencia;

/**
 * Abstract Factory - AbstractProduct.
 * Agrupa as escritas de uma operacao: ou todas sao confirmadas, ou todas sao desfeitas.
 */
public interface UnidadeDeTrabalho {
    void confirmar();

    void desfazer();
}
