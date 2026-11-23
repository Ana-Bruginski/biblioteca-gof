package br.pucpr.ib2b.dominio;

import java.time.LocalDate;

/**
 * Padrao State - papel Context.
 * Guarda o estado corrente e os dados do ciclo (vencimento, numRenovacoes, reservante)
 * e delega cada evento ao objeto de estado. Nao ha if/switch sobre a situacao aqui.
 */
public class Livro {

    /** Prazo do primeiro emprestimo (premissa da equipe; o enunciado nao fixa o valor). */
    public static final int PRAZO_EMPRESTIMO_DIAS = 7;
    /** Cada renovacao acrescenta 30 dias ao vencimento atual (regra 8 do Trabalho 01). */
    public static final int DIAS_POR_RENOVACAO = 30;

    private final String id;
    private String titulo;
    private EstadoLivro estado;
    private LocalDate vencimento;
    private int numRenovacoes;
    private String cpfReservante;

    public Livro(String id, String titulo) {
        this.id = id;
        this.titulo = titulo;
        this.estado = Livre.INSTANCIA;
    }

    /** Usado pelos repositorios: recria o Livro a partir do que foi gravado. */
    public static Livro reconstituir(String id, String titulo, Situacao situacao, LocalDate vencimento,
                                     int numRenovacoes, String cpfReservante) {
        Livro l = new Livro(id, titulo);
        l.estado = situacao.estado();
        l.vencimento = vencimento;
        l.numRenovacoes = numRenovacoes;
        l.cpfReservante = cpfReservante;
        return l;
    }

    // ---------- eventos da maquina de estados: todos delegados ao estado corrente ----------

    public void reservar(Estudante e) { estado.reservar(this, e); }

    public void emprestar(Estudante e, LocalDate hoje) { estado.emprestar(this, e, hoje); }

    public void renovar() { estado.renovar(this); }

    public void devolver() { estado.devolver(this); }

    public void aprovarInspecao() { estado.aprovarInspecao(this); }

    public void enviarParaReparo() { estado.enviarParaReparo(this); }

    public void concluirReparo() { estado.concluirReparo(this); }

    public void registrarLaudoIrrecuperavel() { estado.registrarLaudoIrrecuperavel(this); }

    public void excluir() { estado.excluir(this); }

    // ---------- operacoes usadas somente pelos estados (visibilidade de pacote, ~ na UML) ----------

    void mudarPara(EstadoLivro novo) { this.estado = novo; }

    void prorrogar(int dias) {
        this.vencimento = vencimento.plusDays(dias);
        this.numRenovacoes++;
    }

    void definirReservante(String cpf) { this.cpfReservante = cpf; }

    void definirVencimento(LocalDate data) { this.vencimento = data; }

    void limparCiclo() {
        this.numRenovacoes = 0;
        this.cpfReservante = null;
        this.vencimento = null;
    }

    // ---------- consultas ----------

    public Situacao getSituacao() { return estado.situacao(); }

    public String getId() { return id; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getVencimento() { return vencimento; }

    public int getNumRenovacoes() { return numRenovacoes; }

    public String getCpfReservante() { return cpfReservante; }

    @Override
    public String toString() {
        return id + " - " + titulo + " [" + getSituacao() + "]";
    }
}
