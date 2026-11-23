package br.pucpr.ib2b.consulta;

import java.util.List;

/** Padrao Decorator - papel Decorator: guarda o componente interno e delega por padrao. */
public abstract class DecoradorDeConsulta implements ConsultaAcervo {
    protected final ConsultaAcervo interno;

    protected DecoradorDeConsulta(ConsultaAcervo interno) {
        this.interno = interno;
    }

    @Override
    public List<MetadadosLivro> buscarMetadados(Criterio criterio) {
        return interno.buscarMetadados(criterio);
    }
}
