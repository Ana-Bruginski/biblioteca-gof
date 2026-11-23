package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.dominio.Estudante;
import br.pucpr.ib2b.dominio.PessoaNaoCadastradaException;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;

import java.util.Optional;

/** Template Method - ConcreteClass do cadastro de estudantes; realiza iEstudante. */
public class TratadorDeEstudante extends TratadorDeCadastro<Estudante, DadosEstudante> implements iEstudante {
    private final iPessoa pessoas;

    public TratadorDeEstudante(FabricaDePersistencia fabrica, iPessoa pessoas) {
        super(fabrica);
        this.pessoas = pessoas;
    }

    @Override
    public ResultadoCadastro<Estudante> inserirEstudante(String cpf, String idEstudante, String matricula) {
        return incluir(new DadosEstudante(cpf, idEstudante, matricula));
    }

    @Override
    protected Estudante buscarExistente(DadosEstudante d) {
        return ler(s -> s.relacionamento().buscarEstudante(d.cpf()));
    }

    @Override
    protected void validar(DadosEstudante d) {
        exigir(d.cpf() != null && d.cpf().matches("\\d{11}"), "o CPF deve ter 11 digitos");
        exigir(!vazio(d.idEstudante()), "o id do estudante e obrigatorio");
        exigir(!vazio(d.matricula()), "a matricula e obrigatoria");
    }

    /** Gancho sobrescrito: todo estudante precisa de uma pessoa ja cadastrada (regra 3.1). */
    @Override
    protected void verificarPreRequisitos(DadosEstudante d) {
        if (pessoas.consultarPessoa(d.cpf()).isEmpty()) {
            throw new PessoaNaoCadastradaException(d.cpf());
        }
    }

    @Override
    protected Estudante criar(DadosEstudante d) { return new Estudante(d.cpf(), d.idEstudante(), d.matricula()); }

    @Override
    protected void armazenar(Estudante e) { gravar(s -> s.relacionamento().gravarEstudante(e)); }

    @Override
    public Optional<Estudante> consultarEstudante(String cpf) {
        return ler(s -> Optional.ofNullable(s.relacionamento().buscarEstudante(cpf)));
    }

    @Override
    public boolean atualizarEstudante(String cpf, String matricula) {
        Optional<Estudante> e = consultarEstudante(cpf);
        if (e.isEmpty() || vazio(matricula)) {
            return false;
        }
        e.get().atualizarDados(matricula);
        gravar(s -> s.relacionamento().gravarEstudante(e.get()));
        return true;
    }

    @Override
    public boolean excluirEstudante(String cpf) {
        if (consultarEstudante(cpf).isEmpty()) {
            return false;
        }
        gravar(s -> s.relacionamento().excluirEstudante(cpf));
        return true;
    }
}
