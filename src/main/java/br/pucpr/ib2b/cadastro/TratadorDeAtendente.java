package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.dominio.Atendente;
import br.pucpr.ib2b.dominio.PessoaNaoCadastradaException;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;

import java.util.Optional;

/** Template Method - ConcreteClass do cadastro de atendentes; realiza iAtendente. */
public class TratadorDeAtendente extends TratadorDeCadastro<Atendente, DadosAtendente> implements iAtendente {
    private final iPessoa pessoas;

    public TratadorDeAtendente(FabricaDePersistencia fabrica, iPessoa pessoas) {
        super(fabrica);
        this.pessoas = pessoas;
    }

    @Override
    public ResultadoCadastro<Atendente> inserirAtendente(String cpf, String idAtendente, String setor) {
        return incluir(new DadosAtendente(cpf, idAtendente, setor));
    }

    @Override
    protected Atendente buscarExistente(DadosAtendente d) {
        return ler(s -> s.relacionamento().buscarAtendente(d.cpf()).orElse(null));
    }

    @Override
    protected void validar(DadosAtendente d) {
        exigir(d.cpf() != null && d.cpf().matches("\\d{11}"), "o CPF deve ter 11 digitos");
        exigir(!vazio(d.idAtendente()), "o id do atendente e obrigatorio");
        exigir(!vazio(d.setor()), "o setor e obrigatorio");
    }

    /** Gancho sobrescrito: todo atendente precisa de uma pessoa ja cadastrada (regra 4.1). */
    @Override
    protected void verificarPreRequisitos(DadosAtendente d) {
        if (pessoas.consultarPessoa(d.cpf()).isEmpty()) {
            throw new PessoaNaoCadastradaException(d.cpf());
        }
    }

    @Override
    protected Atendente criar(DadosAtendente d) { return new Atendente(d.cpf(), d.idAtendente(), d.setor()); }

    @Override
    protected void armazenar(Atendente a) { gravar(s -> s.relacionamento().gravarAtendente(a)); }

    @Override
    public Optional<Atendente> consultarAtendente(String cpf) {
        return ler(s -> s.relacionamento().buscarAtendente(cpf));
    }

    @Override
    public boolean atualizarAtendente(String cpf, String setor) {
        Optional<Atendente> a = consultarAtendente(cpf);
        if (a.isEmpty() || vazio(setor)) {
            return false;
        }
        a.get().atualizarDados(setor);
        gravar(s -> s.relacionamento().gravarAtendente(a.get()));
        return true;
    }

    @Override
    public boolean excluirAtendente(String cpf) {
        if (consultarAtendente(cpf).isEmpty()) {
            return false;
        }
        gravar(s -> s.relacionamento().excluirAtendente(cpf));
        return true;
    }
}
