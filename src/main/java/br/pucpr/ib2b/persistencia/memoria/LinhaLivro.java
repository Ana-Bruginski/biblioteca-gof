package br.pucpr.ib2b.persistencia.memoria;

import br.pucpr.ib2b.dominio.Livro;
import br.pucpr.ib2b.dominio.Situacao;

import java.time.LocalDate;

/** "Linha" gravada para um Livro: o estado e guardado como Situacao e reconstituido na leitura. */
record LinhaLivro(String id, String titulo, Situacao situacao, LocalDate vencimento, int numRenovacoes,
                  String cpfReservante) {

    static LinhaLivro de(Livro l) {
        return new LinhaLivro(l.getId(), l.getTitulo(), l.getSituacao(), l.getVencimento(),
                l.getNumRenovacoes(), l.getCpfReservante());
    }

    Livro paraLivro() {
        return Livro.reconstituir(id, titulo, situacao, vencimento, numRenovacoes, cpfReservante);
    }
}
