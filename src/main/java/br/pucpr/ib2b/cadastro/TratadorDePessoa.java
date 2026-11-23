package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.dominio.Pessoa;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;

import java.util.Optional;

/** Template Method - ConcreteClass do cadastro de pessoas; realiza iPessoa. */
public class TratadorDePessoa extends TratadorDeCadastro<Pessoa, DadosPessoa> implements iPessoa {

    public TratadorDePessoa(FabricaDePersistencia fabrica) {
        super(fabrica);
    }

    @Override
    public ResultadoCadastro<Pessoa> inserirPessoa(String cpf, String nome, String sobrenome, String email) {
        return incluir(new DadosPessoa(cpf, nome, sobrenome, email));
    }

    @Override
    protected Pessoa buscarExistente(DadosPessoa d) {
        return ler(s -> s.relacionamento().buscarPessoa(d.cpf()).orElse(null));
    }

    @Override
    protected void validar(DadosPessoa d) {
        exigir(d.cpf() != null && d.cpf().matches("\\d{11}"), "o CPF deve ter 11 digitos");
        exigir(!vazio(d.nome()) && !vazio(d.sobrenome()), "nome e sobrenome sao obrigatorios");
        exigir(d.email() != null && d.email().contains("@"), "e-mail invalido");
    }

    @Override
    protected Pessoa criar(DadosPessoa d) { return new Pessoa(d.cpf(), d.nome(), d.sobrenome(), d.email()); }

    @Override
    protected void armazenar(Pessoa p) { gravar(s -> s.relacionamento().gravarPessoa(p)); }

    @Override
    public Optional<Pessoa> consultarPessoa(String cpf) {
        return ler(s -> s.relacionamento().buscarPessoa(cpf));
    }

    @Override
    public boolean atualizarPessoa(String cpf, String nome, String sobrenome, String email) {
        Optional<Pessoa> p = consultarPessoa(cpf);
        if (p.isEmpty()) {
            return false;
        }
        p.get().atualizarDados(nome, sobrenome, email);
        gravar(s -> s.relacionamento().gravarPessoa(p.get()));
        return true;
    }

    @Override
    public boolean excluirPessoa(String cpf) {
        if (consultarPessoa(cpf).isEmpty()) {
            return false;
        }
        gravar(s -> s.relacionamento().excluirPessoa(cpf));
        return true;
    }
}
