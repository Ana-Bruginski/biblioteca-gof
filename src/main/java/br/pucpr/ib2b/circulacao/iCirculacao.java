package br.pucpr.ib2b.circulacao;

import java.util.List;

/**
 * Padrao Facade - contrato da fachada de circulacao.
 * Cada operacao e um caso de uso completo e transacional que devolve comprovantes (DTOs).
 */
public interface iCirculacao {
    ComprovanteReserva reservar(String idLivro, String cpf);

    List<ComprovanteReserva> consultarReservas(String cpf);

    ComprovanteEmprestimo efetivarEmprestimo(String idLivro, String cpf);

    ComprovanteRenovacao renovar(String idLivro);

    SituacaoDTO consultar(String idLivro);

    ComprovanteDevolucao devolver(String idLivro);

    SituacaoDTO registrarInspecao(String idLivro, Laudo laudo);

    SituacaoDTO enviarParaReparo(String idLivro);

    SituacaoDTO concluirReparo(String idLivro);

    SituacaoDTO registrarLaudoIrrecuperavel(String idLivro);
}
