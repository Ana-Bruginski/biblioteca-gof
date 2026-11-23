package br.pucpr.ib2b.dominio;

/** Papel assumido por uma Pessoa (Trabalho 01): todo papel tem um id e pertence a um CPF. */
public abstract class Papel {
    private final String cpf;
    private final String id;

    protected Papel(String cpf, String id) {
        this.cpf = cpf;
        this.id = id;
    }

    public String getCpf() { return cpf; }
    public String getId() { return id; }
}
