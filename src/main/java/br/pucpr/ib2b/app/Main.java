package br.pucpr.ib2b.app;

import br.pucpr.ib2b.circulacao.ComprovanteReserva;
import br.pucpr.ib2b.circulacao.Laudo;
import br.pucpr.ib2b.consulta.MetadadosLivro;
import br.pucpr.ib2b.financeiro.Multa;
import br.pucpr.ib2b.infra.RelogioAjustavel;
import br.pucpr.ib2b.organizacao.TipoDeGrupo;
import br.pucpr.ib2b.solicitacao.SolicitacaoDeCirculacao;
import br.pucpr.ib2b.solicitacao.SolicitacaoDeRenovacao;
import br.pucpr.ib2b.solicitacao.SolicitacaoDeReserva;

import java.util.List;
import java.util.Scanner;

/**
 * Interface de linha de comando ("telas"). Chama APENAS os tratadores e nao contem regras do dominio.
 * Nas opcoes de reserva e renovacao, a tela cria a solicitacao (Command) e a entrega ao tratador.
 */
public class Main {
    private final Sistema sistema;
    private final Scanner in = new Scanner(System.in);
    private SolicitacaoDeCirculacao ultimaSolicitacao;

    public Main(Sistema sistema) {
        this.sistema = sistema;
    }

    public static void main(String[] args) {
        Configuracao cfg = Configuracao.carregar();
        Sistema sistema;
        try {
            sistema = new Sistema(cfg, new RelogioAjustavel());
        } catch (RuntimeException e) {
            System.out.println("Nao foi possivel iniciar no perfil " + cfg.perfil() + ": " + e.getMessage());
            System.out.println("Dica: confira o MySQL em app.properties ou rode em memoria com -Dperfil=TESTE");
            return;
        }
        System.out.println("=== Biblioteca iB2B | perfil " + cfg.perfil() + " | persistencia "
                + sistema.fabrica().familia() + " | banco " + cfg.banco() + " ===");
        new Main(sistema).executar();
    }

    private void executar() {
        while (true) {
            System.out.println("""

                    ---------------- MENU (data simulada: %s) ----------------
                     1 Cadastrar pessoa            2 Cadastrar estudante        3 Cadastrar atendente
                     4 Cadastrar livro             5 Consultar catalogo         6 Situacao de um livro
                     7 Reservar livro              8 Consultar minhas reservas  9 Efetivar emprestimo
                    10 Renovar emprestimo         11 Reenviar ultima solicitacao (mesmo id)
                    12 Registrar devolucao        13 Registrar inspecao         14 Reparo do livro
                    15 Multas e boletos           16 Simular pagamento no banco 17 Politica de multa
                    18 Organizacao do acervo      19 Avancar o relogio          20 Painel e indicadores
                    21 Carregar dados de exemplo   0 Sair""".formatted(sistema.administracao().hoje()));
            String op = ler("Opcao");
            try {
                if (!tratar(op)) {
                    return;
                }
            } catch (RuntimeException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private boolean tratar(String op) {
        switch (op) {
            case "1" -> System.out.println(sistema.pessoas().inserirPessoa(ler("CPF (11 digitos)"), ler("Nome"),
                    ler("Sobrenome"), ler("E-mail")));
            case "2" -> System.out.println(sistema.estudantes().inserirEstudante(ler("CPF da pessoa"),
                    ler("Id do estudante"), ler("Matricula")));
            case "3" -> System.out.println(sistema.atendentes().inserirAtendente(ler("CPF da pessoa"),
                    ler("Id do atendente"), ler("Setor")));
            case "4" -> System.out.println(sistema.acervo().inserirLivro(ler("Id do livro"), ler("Titulo")));
            case "5" -> {
                List<MetadadosLivro> lista = sistema.acervo().buscarNoCatalogo(ler("Termo do titulo"));
                lista.forEach(m -> System.out.println("  " + m.id() + " - " + m.titulo()));
                System.out.println(lista.size() + " resultado(s)");
            }
            case "6" -> System.out.println(sistema.emprestimos().consultarSituacao(ler("Id do livro")));
            case "7" -> submeter(SolicitacaoDeReserva.nova(ler("Seu nome (autor)"), ler("Id do livro"),
                    ler("CPF do estudante")));
            case "8" -> {
                List<ComprovanteReserva> reservas = sistema.emprestimos().consultarReservas(ler("CPF do estudante"));
                reservas.forEach(r -> System.out.println("  " + r.idReserva() + " | livro " + r.idLivro()
                        + " | " + r.reservadaEm()));
                System.out.println(reservas.size() + " reserva(s)");
            }
            case "9" -> System.out.println(sistema.emprestimos().emprestarLivro(ler("Id do livro"),
                    ler("CPF do estudante")));
            case "10" -> submeter(SolicitacaoDeRenovacao.nova(ler("Seu nome (autor)"), ler("Id do livro")));
            case "11" -> {
                if (ultimaSolicitacao == null) {
                    System.out.println("Nenhuma solicitacao enviada ainda.");
                } else {
                    System.out.println("Reenviando a solicitacao " + ultimaSolicitacao.getId());
                    submeter(ultimaSolicitacao);
                }
            }
            case "12" -> System.out.println(sistema.devolucoes().registrarDevolucao(ler("Id do livro")));
            case "13" -> {
                String id = ler("Id do livro");
                String laudo = ler("Laudo (1 = bom estado, 2 = danificado)");
                System.out.println(sistema.devolucoes().registrarInspecao(id,
                        "2".equals(laudo) ? Laudo.DANIFICADO : Laudo.BOM_ESTADO));
            }
            case "14" -> reparo();
            case "15" -> multas();
            case "16" -> {
                sistema.multas().cobrancas().forEach(c -> System.out.println("  " + c));
                System.out.println(sistema.administracao().simularPagamento(ler("Id da cobranca (MUL-...)")));
            }
            case "17" -> politica();
            case "18" -> organizacao();
            case "19" -> System.out.println(sistema.administracao().avancarDias(Integer.parseInt(ler("Quantos dias"))));
            case "20" -> System.out.println(sistema.administracao().painel());
            case "21" -> carregarExemplo();
            case "0" -> {
                return false;
            }
            default -> System.out.println("Opcao invalida.");
        }
        return true;
    }

    private void submeter(SolicitacaoDeCirculacao s) {
        ultimaSolicitacao = s;
        System.out.println(sistema.emprestimos().submeter(s));
    }

    private void reparo() {
        String id = ler("Id do livro");
        String op = ler("1 = enviar livro LIVRE para reparo, 2 = concluir reparo, 3 = laudo irrecuperavel");
        System.out.println(switch (op) {
            case "1" -> sistema.devolucoes().enviarParaReparo(id);
            case "2" -> sistema.devolucoes().concluirReparo(id);
            default -> sistema.devolucoes().registrarLaudoIrrecuperavel(id);
        });
    }

    private void multas() {
        String cpf = ler("CPF do estudante");
        List<Multa> multas = sistema.multas().consultarMultas(cpf);
        multas.forEach(m -> System.out.println("  " + m.idMulta() + " | livro " + m.idLivro() + " | "
                + m.diasAtraso() + " dia(s) | R$ " + m.valor() + " | politica " + m.politicaAplicada()));
        if (!multas.isEmpty()) {
            System.out.println(sistema.devolucoes().emitirBoletoDeMulta(ler("Id do livro para a 2a via"), cpf));
        } else {
            System.out.println("Nenhuma multa para este CPF.");
        }
    }

    private void politica() {
        System.out.println("Politica vigente: " + sistema.administracao().politicaVigente());
        String op = ler("1 = simular valores, 2 = trocar politica");
        if ("1".equals(op)) {
            int dias = Integer.parseInt(ler("Dias de atraso"));
            sistema.administracao().simularMultas(dias).forEach((id, v) -> System.out.println("  " + id + ": R$ " + v));
        } else {
            System.out.println(sistema.administracao().definirPoliticaDeMulta(ler("ORIGINAL, TETO ou TOLERANCIA")
                    .trim().toUpperCase()));
        }
    }

    private void organizacao() {
        System.out.print(sistema.organizacao().arvore());
        String op = ler("1 = criar grupo, 2 = adicionar livro, 3 = mover livro, 4 = contar livros");
        System.out.println(switch (op) {
            case "1" -> sistema.organizacao().criarGrupo(ler("Nome do grupo"),
                    TipoDeGrupo.valueOf(ler("Tipo (UNIDADE, SECAO, SUBSECAO)").trim().toUpperCase()),
                    ler("Grupo pai"));
            case "2" -> sistema.organizacao().adicionarLivro(ler("Id do livro"), ler("Grupo"));
            case "3" -> sistema.organizacao().moverLivro(ler("Id do livro"), ler("Grupo de destino"));
            default -> sistema.organizacao().contarLivros(ler("Grupo"));
        });
    }

    /** Dados para a demonstracao: 1 estudante, 1 atendente, 5 livros e uma organizacao com duas secoes. */
    private void carregarExemplo() {
        System.out.println(sistema.pessoas().inserirPessoa("11111111111", "Ana", "Silva", "ana@exemplo.com"));
        System.out.println(sistema.estudantes().inserirEstudante("11111111111", "E1", "2026001"));
        System.out.println(sistema.pessoas().inserirPessoa("22222222222", "Joao", "Souza", "joao@exemplo.com"));
        System.out.println(sistema.atendentes().inserirAtendente("22222222222", "A1", "Circulacao"));
        String[][] livros = {{"L1", "Dom Casmurro"}, {"L2", "Memorias Postumas de Bras Cubas"},
                {"L3", "Grande Sertao: Veredas"}, {"L4", "Vidas Secas"}, {"L5", "O Cortico"}};
        for (String[] l : livros) {
            System.out.println(sistema.acervo().inserirLivro(l[0], l[1]));
        }
        System.out.println(sistema.organizacao().criarGrupo("Literatura", TipoDeGrupo.UNIDADE, "Acervo iB2B"));
        System.out.println(sistema.organizacao().criarGrupo("Romance", TipoDeGrupo.SECAO, "Literatura"));
        System.out.println(sistema.organizacao().criarGrupo("Regionalismo", TipoDeGrupo.SECAO, "Literatura"));
        for (String id : new String[]{"L1", "L2", "L5"}) {
            System.out.println(sistema.organizacao().adicionarLivro(id, "Romance"));
        }
        for (String id : new String[]{"L3", "L4"}) {
            System.out.println(sistema.organizacao().adicionarLivro(id, "Regionalismo"));
        }
    }

    private String ler(String rotulo) {
        System.out.print(rotulo + ": ");
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }
}
