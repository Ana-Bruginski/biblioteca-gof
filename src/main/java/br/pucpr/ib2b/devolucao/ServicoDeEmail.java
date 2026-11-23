package br.pucpr.ib2b.devolucao;

/** Servico de e-mail (classe ServicoDeEmail do Trabalho 02). */
public interface ServicoDeEmail {
    void enviar(String destinatario, String assunto, String corpo);
}
