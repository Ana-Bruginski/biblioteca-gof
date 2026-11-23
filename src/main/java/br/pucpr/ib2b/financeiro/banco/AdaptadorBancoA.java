package br.pucpr.ib2b.financeiro.banco;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Padrao Adapter (de objeto) - papel Adapter para o banco A.
 * Saida: SolicitacaoCobranca (reais) -> gerarTitulo(cpf, centavos).
 * Entrada: webhook JSON do banco A -> ConfirmacaoPagamento entregue ao ReceptorDePagamento.
 */
public class AdaptadorBancoA implements GatewayDeCobranca, BancoSimulavel {
    private final ApiBancoA api;
    private final ReceptorDePagamento receptor;

    public AdaptadorBancoA(ApiBancoA api, ReceptorDePagamento receptor) {
        this.api = api;
        this.receptor = receptor;
    }

    @Override
    public Cobranca emitirBoleto(SolicitacaoCobranca s) {
        long centavos = paraCentavos(s.valor());                 // R$ 7,00 -> 700
        ApiBancoA.TituloA titulo = api.gerarTitulo(s.cpf(), centavos);
        return new Cobranca(s.idCobranca(), CodigoBanco.A, titulo.nossoNumero(), s.valor(), titulo.linhaDigitavel());
    }

    @Override
    public SituacaoCobranca consultarSituacao(Cobranca c) {
        return traduzir(api.consultarTitulo(c.getIdExterno()));
    }

    /** Ponto de entrada do webhook do banco A. */
    public void receberWebhook(String json) {
        if (!"LIQUIDADO".equals(campo(json, "status"))) {
            return;
        }
        long centavos = Long.parseLong(campo(json, "valorCentavos"));
        receptor.confirmar(new ConfirmacaoPagamento(CodigoBanco.A, campo(json, "nossoNumero"),
                campo(json, "idPagamento"), BigDecimal.valueOf(centavos, 2)));
    }

    @Override
    public void simularPagamento(Cobranca cobranca, String idPagamento) {
        receberWebhook(api.liquidar(cobranca.getIdExterno(), idPagamento));
    }

    private long paraCentavos(BigDecimal valor) {
        try {
            return valor.movePointRight(2).longValueExact();
        } catch (ArithmeticException e) {
            throw new ValorInvalidoException("valor com mais de duas casas decimais: " + valor);
        }
    }

    private SituacaoCobranca traduzir(ApiBancoA.StatusA status) {
        if (status == null) {
            return SituacaoCobranca.DESCONHECIDA;
        }
        return switch (status) {
            case EM_ABERTO -> SituacaoCobranca.ABERTA;
            case LIQUIDADO -> SituacaoCobranca.QUITADA;
            case BAIXADO -> SituacaoCobranca.CANCELADA;
        };
    }

    private static String campo(String json, String nome) {
        Matcher m = Pattern.compile("\"" + nome + "\"\\s*:\\s*\"?([^\",}]+)\"?").matcher(json);
        return m.find() ? m.group(1) : null;
    }
}
