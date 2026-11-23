package br.pucpr.ib2b.persistencia;

/** Falha tecnica de armazenamento (nao e recusa de regra de negocio). */
public class ErroDePersistencia extends RuntimeException {
    public ErroDePersistencia(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
