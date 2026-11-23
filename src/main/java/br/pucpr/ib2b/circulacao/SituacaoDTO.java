package br.pucpr.ib2b.circulacao;

import br.pucpr.ib2b.dominio.Situacao;

import java.time.LocalDate;

public record SituacaoDTO(String idLivro, String titulo, Situacao situacao, LocalDate vencimento, int numRenovacoes) {
    @Override
    public String toString() {
        return idLivro + " - " + titulo + " | " + situacao
                + (vencimento == null ? "" : " | vence em " + vencimento)
                + (numRenovacoes > 0 ? " | renovacoes: " + numRenovacoes : "");
    }
}
