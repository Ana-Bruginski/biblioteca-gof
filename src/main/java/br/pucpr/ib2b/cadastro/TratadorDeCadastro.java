package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.biblioteca.SessaoDeDados;
import br.pucpr.ib2b.dominio.ExcecaoDeDominio;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Padrao Template Method - papel AbstractClass.
 * incluir() e o metodo-modelo: e FINAL, entao nenhuma subclasse muda a ordem dos passos.
 * Ordem fixa: buscar existente -> validar -> verificar pre-requisitos -> criar -> armazenar -> apresentar.
 * Como a verificacao do pre-requisito vem antes de criar/armazenar, uma recusa nao deixa gravacao parcial.
 *
 * @param <T> entidade cadastrada (Livro, Pessoa, Estudante, Atendente)
 * @param <D> dados de entrada do cadastro
 */
public abstract class TratadorDeCadastro<T, D> {
    protected final FabricaDePersistencia fabrica;

    protected TratadorDeCadastro(FabricaDePersistencia fabrica) {
        this.fabrica = fabrica;
    }

    public final ResultadoCadastro<T> incluir(D dados) {
        try {
            T existente = buscarExistente(dados);
            if (existente != null) {
                return apresentar(existente, false);
            }
            validar(dados);
            verificarPreRequisitos(dados);      // gancho
            T novo = criar(dados);
            armazenar(novo);
            return apresentar(novo, true);
        } catch (ExcecaoDeDominio e) {
            return ResultadoCadastro.recusado(e.getMessage());
        }
    }

    // -------- operacoes primitivas: cada cadastro implementa as suas --------

    protected abstract T buscarExistente(D dados);

    protected abstract void validar(D dados);

    protected abstract T criar(D dados);

    protected abstract void armazenar(T entidade);

    // -------- gancho (hook): vazio por padrao --------

    protected void verificarPreRequisitos(D dados) {
        // nada a verificar por padrao; Estudante e Atendente exigem Pessoa cadastrada
    }

    // -------- passo comum --------

    protected ResultadoCadastro<T> apresentar(T entidade, boolean novo) {
        return novo ? ResultadoCadastro.criado(entidade) : ResultadoCadastro.existente(entidade);
    }

    // -------- apoio: leitura e gravacao pela fabrica (delegacao a Acervo/Relacionamento) --------

    protected <R> R ler(Function<SessaoDeDados, R> consulta) {
        SessaoDeDados s = SessaoDeDados.abrir(fabrica);
        try {
            return consulta.apply(s);
        } finally {
            s.desfazer();                       // leitura: nada a confirmar
        }
    }

    protected void gravar(Consumer<SessaoDeDados> escrita) {
        SessaoDeDados s = SessaoDeDados.abrir(fabrica);
        try {
            escrita.accept(s);
            s.confirmar();
        } catch (RuntimeException e) {
            s.desfazer();
            throw e;
        }
    }

    protected static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new br.pucpr.ib2b.dominio.ValidacaoException(mensagem);
        }
    }

    protected static boolean vazio(String s) { return s == null || s.isBlank(); }
}
