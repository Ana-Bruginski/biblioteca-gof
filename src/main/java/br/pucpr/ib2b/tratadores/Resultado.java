package br.pucpr.ib2b.tratadores;

/** O que a interface recebe dos tratadores: sucesso ou recusa, com mensagem. */
public record Resultado(boolean sucesso, String mensagem) {
    public static Resultado ok(String mensagem) { return new Resultado(true, mensagem); }

    public static Resultado recusado(String motivo) { return new Resultado(false, motivo); }

    @Override
    public String toString() { return (sucesso ? "OK: " : "RECUSADO: ") + mensagem; }
}
