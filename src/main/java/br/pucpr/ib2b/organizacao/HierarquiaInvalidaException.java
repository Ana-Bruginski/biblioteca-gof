package br.pucpr.ib2b.organizacao;

import br.pucpr.ib2b.dominio.ExcecaoDeDominio;

public class HierarquiaInvalidaException extends ExcecaoDeDominio {
    public HierarquiaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
