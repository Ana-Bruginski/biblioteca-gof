package br.pucpr.ib2b.consulta;

import br.pucpr.ib2b.biblioteca.SessaoDeDados;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;

import java.util.List;

/** Padrao Decorator - ConcreteComponent: a implementacao original, que le o Acervo. Nao e alterada. */
public class ConsultaAcervoBasica implements ConsultaAcervo {
    private final FabricaDePersistencia fabrica;

    public ConsultaAcervoBasica(FabricaDePersistencia fabrica) {
        this.fabrica = fabrica;
    }

    @Override
    public List<MetadadosLivro> buscarMetadados(Criterio c) {
        SessaoDeDados s = SessaoDeDados.abrir(fabrica);
        try {
            return s.acervo().listar().stream()
                    .filter(l -> l.getTitulo().toLowerCase().contains(c.termo()) || l.getId().equalsIgnoreCase(c.termo()))
                    .map(l -> new MetadadosLivro(l.getId(), l.getTitulo()))
                    .toList();
        } finally {
            s.desfazer();
        }
    }
}
