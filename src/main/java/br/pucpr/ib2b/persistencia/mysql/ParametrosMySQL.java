package br.pucpr.ib2b.persistencia.mysql;

/** Dados de conexao com o MySQL principal (lidos de app.properties). */
public record ParametrosMySQL(String url, String usuario, String senha) { }
