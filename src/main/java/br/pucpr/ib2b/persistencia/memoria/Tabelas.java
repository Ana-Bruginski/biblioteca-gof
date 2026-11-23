package br.pucpr.ib2b.persistencia.memoria;

import br.pucpr.ib2b.dominio.Atendente;
import br.pucpr.ib2b.dominio.Devolucao;
import br.pucpr.ib2b.dominio.Emprestimo;
import br.pucpr.ib2b.dominio.Estudante;
import br.pucpr.ib2b.dominio.Pessoa;
import br.pucpr.ib2b.dominio.Reserva;

import java.util.LinkedHashMap;
import java.util.Map;

/** Conjunto de "tabelas" da familia em memoria. Os valores guardados sao copias imutaveis. */
class Tabelas {
    final Map<String, LinhaLivro> livros = new LinkedHashMap<>();
    final Map<String, Reserva> reservasPorLivro = new LinkedHashMap<>();
    final Map<String, Emprestimo> emprestimosPorLivro = new LinkedHashMap<>();
    final Map<String, Devolucao> devolucoes = new LinkedHashMap<>();
    final Map<String, Pessoa> pessoas = new LinkedHashMap<>();
    final Map<String, Estudante> estudantes = new LinkedHashMap<>();
    final Map<String, Atendente> atendentes = new LinkedHashMap<>();

    Tabelas copia() {
        Tabelas t = new Tabelas();
        t.livros.putAll(livros);
        t.reservasPorLivro.putAll(reservasPorLivro);
        t.emprestimosPorLivro.putAll(emprestimosPorLivro);
        t.devolucoes.putAll(devolucoes);
        t.pessoas.putAll(pessoas);
        t.estudantes.putAll(estudantes);
        t.atendentes.putAll(atendentes);
        return t;
    }
}
