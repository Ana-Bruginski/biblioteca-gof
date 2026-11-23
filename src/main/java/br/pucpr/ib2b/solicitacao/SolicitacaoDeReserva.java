package br.pucpr.ib2b.solicitacao;

import br.pucpr.ib2b.circulacao.ComprovanteReserva;
import br.pucpr.ib2b.circulacao.iCirculacao;

import java.time.Instant;
import java.util.UUID;

/** Padrao Command - ConcreteCommand de reserva. */
public class SolicitacaoDeReserva extends SolicitacaoDeCirculacao {
    private final String idLivro;
    private final String cpf;

    public SolicitacaoDeReserva(UUID id, String autor, Instant criadaEm, String idLivro, String cpf) {
        super(id, autor, criadaEm);
        this.idLivro = idLivro;
        this.cpf = cpf;
    }

    /** A tela cria a solicitacao com um id novo; num reenvio, reaproveita o MESMO objeto/id. */
    public static SolicitacaoDeReserva nova(String autor, String idLivro, String cpf) {
        return new SolicitacaoDeReserva(UUID.randomUUID(), autor, Instant.now(), idLivro, cpf);
    }

    @Override
    public ResultadoSolicitacao executar(iCirculacao circulacao) {
        ComprovanteReserva c = circulacao.reservar(idLivro, cpf);    // regras ficam no receptor/Livro
        return ResultadoSolicitacao.executada(getId(), "reserva " + c.idReserva() + " do livro " + idLivro);
    }
}
