package br.pucpr.ib2b.tratadores;

import br.pucpr.ib2b.circulacao.ComprovanteDevolucao;
import br.pucpr.ib2b.circulacao.Laudo;
import br.pucpr.ib2b.circulacao.SituacaoDTO;
import br.pucpr.ib2b.circulacao.iCirculacao;
import br.pucpr.ib2b.devolucao.AgendaDeInspecao;
import br.pucpr.ib2b.devolucao.EventoDevolucao;
import br.pucpr.ib2b.devolucao.PublicadorDeDevolucao;
import br.pucpr.ib2b.dominio.ExcecaoDeDominio;
import br.pucpr.ib2b.financeiro.iFinanceiro;

import java.util.function.Supplier;

/**
 * Tratador dos servicos do Atendente (f5 e f6 do Trabalho 01) e da inspecao/reparo.
 * Observer: depois que a devolucao e CONFIRMADA, so publica o evento; nao conhece multa, e-mail
 * nem indicadores. Uma nova reacao e um novo ouvinte registrado na montagem, sem mudar esta classe.
 */
public class TratadorDeDevolucao {
    private final iCirculacao circulacao;
    private final PublicadorDeDevolucao publicador;
    private final AgendaDeInspecao agenda;
    private final iFinanceiro financeiro;

    public TratadorDeDevolucao(iCirculacao circulacao, PublicadorDeDevolucao publicador, AgendaDeInspecao agenda,
                               iFinanceiro financeiro) {
        this.circulacao = circulacao;
        this.publicador = publicador;
        this.agenda = agenda;
        this.financeiro = financeiro;
    }

    /** f5 - registrar devolucao de livro. */
    public Resultado registrarDevolucao(String idLivro) {
        ComprovanteDevolucao c;
        try {
            c = circulacao.devolver(idLivro);          // transacao confirmada aqui dentro
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
        EventoDevolucao ev = EventoDevolucao.de(c);
        publicador.publicar(ev);                       // so depois da confirmacao
        return Resultado.ok("devolucao " + c.idDevolucao() + " registrada; livro DEVOLVIDO aguardando inspecao"
                + (ev.diasAtraso() > 0 ? "; atraso de " + ev.diasAtraso() + " dia(s)" : "; no prazo"));
    }

    /** f6 - emitir (segunda via do) boleto de multa, caso a devolucao tenha sido em atraso. */
    public Resultado emitirBoletoDeMulta(String idLivro, String cpf) {
        return financeiro.segundaVia(idLivro, cpf)
                .map(c -> Resultado.ok("boleto: " + c))
                .orElse(Resultado.recusado("nao ha multa para o livro " + idLivro + " e o CPF " + cpf));
    }

    /** Decisao da inspecao: BOM_ESTADO -> LIVRE; DANIFICADO -> REPARANDO. */
    public Resultado registrarInspecao(String idLivro, Laudo laudo) {
        Resultado r = executar(() -> circulacao.registrarInspecao(idLivro, laudo));
        if (r.sucesso()) {
            agenda.concluir(idLivro);
        }
        return r;
    }

    public Resultado enviarParaReparo(String idLivro) { return executar(() -> circulacao.enviarParaReparo(idLivro)); }

    public Resultado concluirReparo(String idLivro) { return executar(() -> circulacao.concluirReparo(idLivro)); }

    public Resultado registrarLaudoIrrecuperavel(String idLivro) {
        return executar(() -> circulacao.registrarLaudoIrrecuperavel(idLivro));
    }

    private Resultado executar(Supplier<SituacaoDTO> operacao) {
        try {
            return Resultado.ok(operacao.get().toString());
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }
}
