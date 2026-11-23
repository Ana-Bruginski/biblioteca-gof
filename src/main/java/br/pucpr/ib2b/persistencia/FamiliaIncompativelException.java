package br.pucpr.ib2b.persistencia;

/** Lancada quando alguem tenta misturar produtos de familias diferentes. */
public class FamiliaIncompativelException extends RuntimeException {
    public FamiliaIncompativelException(String fabrica, Object uow) {
        super("a fabrica " + fabrica + " nao aceita a unidade de trabalho " + uow.getClass().getSimpleName());
    }
}
