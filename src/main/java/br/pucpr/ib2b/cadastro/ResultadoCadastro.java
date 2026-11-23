package br.pucpr.ib2b.cadastro;

/** Resultado do metodo-modelo incluir(): novo cadastro, cadastro ja existente ou recusa. */
public record ResultadoCadastro<T>(StatusCadastro status, T entidade, String motivo) {
    public static <T> ResultadoCadastro<T> criado(T t) { return new ResultadoCadastro<>(StatusCadastro.CRIADO, t, null); }

    public static <T> ResultadoCadastro<T> existente(T t) {
        return new ResultadoCadastro<>(StatusCadastro.EXISTENTE, t, "ja cadastrado");
    }

    public static <T> ResultadoCadastro<T> recusado(String motivo) {
        return new ResultadoCadastro<>(StatusCadastro.RECUSADO, null, motivo);
    }

    @Override
    public String toString() {
        return switch (status) {
            case CRIADO -> "cadastrado: " + entidade;
            case EXISTENTE -> "ja estava cadastrado: " + entidade;
            case RECUSADO -> "recusado: " + motivo;
        };
    }
}
