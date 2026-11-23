package br.pucpr.ib2b.dominio;

public class Atendente extends Papel {
    private String setor;

    public Atendente(String cpf, String idAtendente, String setor) {
        super(cpf, idAtendente);
        this.setor = setor;
    }

    public Atendente copiar() { return new Atendente(getCpf(), getId(), setor); }

    public String getSetor() { return setor; }
    public void atualizarDados(String setor) { this.setor = setor; }

    @Override
    public String toString() { return "Atendente " + getId() + " (CPF " + getCpf() + ", setor " + setor + ")"; }
}
