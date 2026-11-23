package br.pucpr.ib2b.infra;

import java.util.UUID;

/** Gera identificadores curtos e legiveis, como R-1a2b3c4d. */
public final class Ids {
    private Ids() { }

    public static String novo(String prefixo) {
        return prefixo + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
