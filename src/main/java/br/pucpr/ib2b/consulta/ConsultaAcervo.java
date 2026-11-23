package br.pucpr.ib2b.consulta;

import java.util.List;

/** Padrao Decorator - papel Component: contrato unico de consulta. */
public interface ConsultaAcervo {
    List<MetadadosLivro> buscarMetadados(Criterio criterio);
}
