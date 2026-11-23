package br.pucpr.ib2b.financeiro.banco;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Padrao Adapter (de objeto) - papel Adapter para o banco B.
 * Saida: SolicitacaoCobranca -> abrirCobranca(documentoFormatado, valorDecimal).
 * Entrada: notificacao XML do banco B -> ConfirmacaoPagamento.
 */
public class AdaptadorBancoB implements GatewayDeCobranca, BancoSimulavel {
    private final ApiBancoB api;
    private final ReceptorDePagamento receptor;

    public AdaptadorBancoB(ApiBancoB api, ReceptorDePagamento receptor) {
        this.api = api;
        this.receptor = receptor;
    }

    @Override
    public Cobranca emitirBoleto(SolicitacaoCobranca s) {
        ApiBancoB.CobrancaB c = api.abrirCobranca(paraDocumento(s.cpf()), s.valor().setScale(2)); // 7,00 -> 7.00
        return new Cobranca(s.idCobranca(), CodigoBanco.B, c.txid(), s.valor(), c.copiaECola());
    }

    @Override
    public SituacaoCobranca consultarSituacao(Cobranca c) {
        return traduzir(api.statusCobranca(c.getIdExterno()));
    }

    /** Ponto de entrada da notificacao do banco B. */
    public void receberNotificacao(String xml) {
        if (!"CONCLUIDA".equals(tag(xml, "situacao"))) {
            return;
        }
        receptor.confirmar(new ConfirmacaoPagamento(CodigoBanco.B, tag(xml, "txid"), tag(xml, "e2eid"),
                new BigDecimal(tag(xml, "valor"))));
    }

    @Override
    public void simularPagamento(Cobranca cobranca, String idPagamento) {
        receberNotificacao(api.concluir(cobranca.getIdExterno(), idPagamento));
    }

    /** O banco B exige o CPF formatado: 12345678901 -> 123.456.789-01. */
    private String paraDocumento(String cpf) {
        String d = cpf.replaceAll("\\D", "");
        if (d.length() != 11) {
            return d;
        }
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    private SituacaoCobranca traduzir(String situacao) {
        if (situacao == null) {
            return SituacaoCobranca.DESCONHECIDA;
        }
        return switch (situacao) {
            case "ATIVA" -> SituacaoCobranca.ABERTA;
            case "CONCLUIDA" -> SituacaoCobranca.QUITADA;
            case "REMOVIDA" -> SituacaoCobranca.CANCELADA;
            default -> SituacaoCobranca.DESCONHECIDA;
        };
    }

    private static String tag(String xml, String nome) {
        Matcher m = Pattern.compile("<" + nome + ">(.*?)</" + nome + ">").matcher(xml);
        return m.find() ? m.group(1) : null;
    }
}
