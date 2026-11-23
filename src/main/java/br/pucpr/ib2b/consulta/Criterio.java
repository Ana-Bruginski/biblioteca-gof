package br.pucpr.ib2b.consulta;

/** Chave da consulta, normalizada (sem espacos nas pontas, minusculas). */
public record Criterio(String termo) {
    public Criterio {
        termo = termo == null ? "" : termo.trim().toLowerCase();
    }
}
