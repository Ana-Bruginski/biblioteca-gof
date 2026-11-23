package br.pucpr.ib2b.financeiro;

import br.pucpr.ib2b.financeiro.banco.Cobranca;

import java.util.List;
import java.util.Optional;

/** Interface do componente Financeiro (iMulta / iQuitacao no diagrama de componentes). */
public interface iFinanceiro {
    void solicitarMulta(String cpf, String idDevolucao, String idLivro, int diasAtraso);

    List<Multa> consultarMultas(String cpf);

    Optional<Cobranca> segundaVia(String idLivro, String cpf);

    List<Cobranca> cobrancas();
}
