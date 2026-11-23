package br.pucpr.ib2b.dominio;

/** Pre-requisito do cadastro de Estudante e Atendente (gancho do Template Method). */
public class PessoaNaoCadastradaException extends ExcecaoDeDominio {
    public PessoaNaoCadastradaException(String cpf) {
        super("pessoa com CPF " + cpf + " nao esta cadastrada");
    }
}
