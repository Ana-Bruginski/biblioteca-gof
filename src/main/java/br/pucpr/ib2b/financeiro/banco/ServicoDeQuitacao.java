package br.pucpr.ib2b.financeiro.banco;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recebe as confirmacoes ja traduzidas pelos adaptadores e garante UMA unica quitacao:
 * notificacoes repetidas encontram a cobranca QUITADA e nao geram segunda quitacao.
 */
public class ServicoDeQuitacao implements ReceptorDePagamento {
    private final RegistroDeCobrancas registro;
    private final AtomicInteger quitacoes = new AtomicInteger();
    private final List<ConfirmacaoPagamento> pendentesDeConciliacao = new CopyOnWriteArrayList<>();

    public ServicoDeQuitacao(RegistroDeCobrancas registro) {
        this.registro = registro;
    }

    @Override
    public void confirmar(ConfirmacaoPagamento conf) {
        registro.buscar(conf.banco(), conf.idExterno()).ifPresentOrElse(
                cobranca -> {
                    if (registro.quitarSeAberta(cobranca.getIdCobranca(), conf.idPagamento())) {
                        quitacoes.incrementAndGet();
                    }
                },
                () -> pendentesDeConciliacao.add(conf));         // cobranca desconhecida: nada e quitado
    }

    public int quantidadeDeQuitacoes() { return quitacoes.get(); }

    public List<ConfirmacaoPagamento> pendentesDeConciliacao() { return List.copyOf(pendentesDeConciliacao); }
}
