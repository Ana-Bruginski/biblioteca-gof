package br.pucpr.ib2b.dominio;

public class Estudante extends Papel {
    private String matricula;

    public Estudante(String cpf, String idEstudante, String matricula) {
        super(cpf, idEstudante);
        this.matricula = matricula;
    }

    public Estudante copiar() { return new Estudante(getCpf(), getId(), matricula); }

    public String getMatricula() { return matricula; }
    public void atualizarDados(String matricula) { this.matricula = matricula; }

    @Override
    public String toString() { return "Estudante " + getId() + " (CPF " + getCpf() + ", matricula " + matricula + ")"; }
}
