package br.pucpr.ib2b.dominio;

/**
 * Valor gravado na persistencia para representar o estado do Livro.
 * estado() reconstitui o objeto de estado (State) ao carregar o Livro.
 */
public enum Situacao {
    LIVRE, RESERVADO, EMPRESTADO, RENOVADO, DEVOLVIDO, REPARANDO, INCINERADO;

    public EstadoLivro estado() {
        return switch (this) {
            case LIVRE -> Livre.INSTANCIA;
            case RESERVADO -> Reservado.INSTANCIA;
            case EMPRESTADO -> Emprestado.INSTANCIA;
            case RENOVADO -> Renovado.INSTANCIA;
            case DEVOLVIDO -> Devolvido.INSTANCIA;
            case REPARANDO -> Reparando.INSTANCIA;
            case INCINERADO -> Incinerado.INSTANCIA;
        };
    }
}
