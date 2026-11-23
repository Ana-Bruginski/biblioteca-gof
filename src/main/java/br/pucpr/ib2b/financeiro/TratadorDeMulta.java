package br.pucpr.ib2b.financeiro;

import br.pucpr.ib2b.financeiro.banco.Cobranca;
import br.pucpr.ib2b.financeiro.banco.RegistroDeCobrancas;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Tratador do componente Financeiro (Trabalho 02). Delega ao ServicoDeMulta.
 * Usa o idDevolucao como chave de idempotencia: a mesma devolucao nunca gera duas multas.
 */
public class TratadorDeMulta implements iFinanceiro {
    private final ServicoDeMulta servico;
    private final RegistroDeCobrancas cobrancas;

    public TratadorDeMulta(ServicoDeMulta servico, RegistroDeCobrancas cobrancas) {
        this.servico = servico;
        this.cobrancas = cobrancas;
    }

    @Override
    public synchronized void solicitarMulta(String cpf, String idDevolucao, String idLivro, int diasAtraso) {
        boolean jaExiste = servico.multas().stream().anyMatch(m -> m.idDevolucao().equals(idDevolucao));
        if (!jaExiste) {
            servico.gerarMulta(cpf, idDevolucao, idLivro, diasAtraso);
        }
    }

    @Override
    public List<Multa> consultarMultas(String cpf) {
        return servico.multas().stream().filter(m -> m.cpfEstudante().equals(cpf)).toList();
    }

    /** f6: emite a segunda via do boleto da multa mais recente do livro para o estudante. */
    @Override
    public Optional<Cobranca> segundaVia(String idLivro, String cpf) {
        return servico.multas().stream()
                .filter(m -> m.idLivro().equals(idLivro) && m.cpfEstudante().equals(cpf))
                .max(Comparator.comparing(Multa::emitidaEm))
                .flatMap(m -> cobrancas.buscarPorId(m.idMulta()));
    }

    @Override
    public List<Cobranca> cobrancas() { return cobrancas.todas(); }
}
