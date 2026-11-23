package br.pucpr.ib2b.devolucao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Simula o envio (guarda as mensagens). Pode ser configurado para falhar, para testar o isolamento. */
public class ServicoDeEmailSimulado implements ServicoDeEmail {
    private final List<String> enviados = new CopyOnWriteArrayList<>();
    private volatile boolean falhar;

    @Override
    public void enviar(String destinatario, String assunto, String corpo) {
        if (falhar) {
            throw new FalhaDeEnvioException("servidor de e-mail indisponivel");
        }
        enviados.add(destinatario + " | " + assunto);
    }

    public void simularFalha(boolean falhar) { this.falhar = falhar; }

    public List<String> enviados() { return List.copyOf(enviados); }
}
