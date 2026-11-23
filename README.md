# Sistema de empréstimo de livros com padrões GoF (Trabalho 03)

PUCPR · Bacharelado em Ciência da Computação · Modelagem de Sistemas Computacionais
Prof. Edson Emílio Scalabrin · 2º semestre de 2026

**Equipe:** Ana Luiza Bruginski de Melo · Hugo Souza Jamielniak · João Pedro Macena Correa · Nathan Bueno Catenacci Okazaki

Versão executável do sistema da iB2B (cadastro, reserva, empréstimo, renovação e devolução de livros), continuação do Trabalho 02. O sistema aplica os dez padrões GoF pedidos, um em cada situação da lista "Problemas de aplicação dos padrões GoF". Cada padrão é comprovado por um teste automatizado.

---

## 1. Pré-requisitos

| Ferramenta | Versão | Uso |
|---|---|---|
| JDK | 17 ou superior | compilar e executar |
| Maven | 3.8 ou superior | build, testes e execução |
| MySQL | 8.x | só no perfil de produção |

## 2. Rodar os testes (um único comando)

```bash
mvn test
```

O comando roda os testes dos dez cenários de verificação (`R01` a `R10`) e o teste do fluxo completo. Todos usam a família de persistência **em memória**, por isso não é preciso configurar nada.

Resultado obtido pela equipe em 23/11/2026, com Maven: `Tests run: 51, Failures: 0, Errors: 0, Skipped: 1` e `BUILD SUCCESS`. O teste ignorado é o opcional da família MySQL, descrito abaixo.

No IntelliJ, o mesmo comando roda pelo painel Maven: **Lifecycle → test**.

Também existe um teste opcional da família **MySQL**. Ele só roda se o banco for informado na linha de comando; sem isso, aparece como ignorado:

```bash
mvn test -Dmysql.url="jdbc:mysql://localhost:3306/biblioteca_ib2b?createDatabaseIfNotExist=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false" -Dmysql.usuario=root -Dmysql.senha=SUA_SENHA
```

## 3. Executar a interface de linha de comando

**Perfil de produção (MySQL).** Confira usuário e senha em `src/main/resources/app.properties` ou passe-os na linha de comando. O banco e as tabelas são criados automaticamente na primeira execução (`schema.sql`).

```bash
mvn -q compile exec:java -Dmysql.usuario=root -Dmysql.senha=SUA_SENHA
```

**Perfil de teste (memória).** Não precisa de banco de dados.

```bash
mvn -q compile exec:java -Dperfil=TESTE
```

**No IntelliJ.** Abra `app/Main.java` e rode `Main.main()`. Para o perfil de teste, em *Run → Edit Configurations → Modify options → Add VM options*, informe `-Dperfil=TESTE`. Para o perfil de produção, informe `-Dmysql.usuario=root -Dmysql.senha=SUA_SENHA` (ou ajuste `app.properties`).

Outras opções que podem ser trocadas com `-Dchave=valor`:

| Chave | Valores | Padrão relacionado |
|---|---|---|
| `perfil` | `PRODUCAO` ou `TESTE` | Abstract Factory |
| `banco` | `A` ou `B` | Adapter |
| `multa.politica` | `ORIGINAL`, `TETO` ou `TOLERANCIA` | Strategy |
| `consulta.medicao`, `consulta.cache` | `true` ou `false` | Decorator |

### Roteiro da demonstração (fluxo completo pela interface)

| Passo | Opção do menu | O que mostrar |
|---|---|---|
| 1 | `21` Carregar dados de exemplo | Cria estudante, atendente, 5 livros e a organização em seções |
| 2 | `7` Reservar livro (`L1`, CPF `11111111111`) | A tela cria a solicitação (Command) |
| 3 | `9` Efetivar empréstimo | Fachada valida a reserva, remove e cria o empréstimo (Facade) |
| 4 | `10` Renovar e depois `11` Reenviar a mesma solicitação | O reenvio não renova de novo (Command) |
| 5 | `19` Avançar o relógio (ex.: `40` dias) | Simula o atraso |
| 6 | `12` Registrar devolução | Livro fica DEVOLVIDO; multa, inspeção, indicadores e e-mail reagem (Observer) |
| 7 | `15` Multas e boletos | Valor pela política vigente (Strategy) e segunda via do boleto (f6) |
| 8 | `16` Simular pagamento no banco | Notificação traduzida pelo adaptador e quitação única (Adapter) |
| 9 | `13` Registrar inspeção | DEVOLVIDO → LIVRE ou REPARANDO (State) |
| 10 | `18` Organização do acervo e `20` Painel | Contagem por seção (Composite) e indicadores |

---

## 4. Organização do código

```
src/main/java/br/pucpr/ib2b
├── app/            Main (interface CLI), Sistema (montagem), Configuracao
├── tratadores/     TratadorDeEmprestimo (f1–f4), TratadorDeDevolucao (f5, f6, inspeção),
│                   TratadorDeOrganizacao, TratadorDeAdministracao, Resultado
├── cadastro/       Template Method: TratadorDeCadastro, TratadorDeAcervo, TratadorDePessoa,
│                   TratadorDeEstudante, TratadorDeAtendente, iLivro, iPessoa, iEstudante, iAtendente
├── dominio/        State: Livro, EstadoLivro e os 7 estados; Pessoa, Estudante, Atendente,
│                   Reserva, Emprestimo, Devolucao e exceções de domínio
├── biblioteca/     Pontos internos: AcervoDeLivros, Relacionamento, Movimentacao, SessaoDeDados
├── circulacao/     Facade: iCirculacao, ServicoDeCirculacao e comprovantes (DTOs)
├── devolucao/      Observer: PublicadorDeDevolucao, EventoDevolucao e ouvintes
├── solicitacao/    Command: SolicitacaoDeCirculacao, ExecutorDeSolicitacoes, RegistroDeSolicitacoes
├── financeiro/     Strategy: PoliticaDeMulta e políticas; ServicoDeMulta; TratadorDeMulta
│   └── banco/      Adapter: GatewayDeCobranca, AdaptadorBancoA/B, ApiBancoA/B, ServicoDeQuitacao
├── consulta/       Decorator: ConsultaAcervo, decoradores de medição e de cache
├── organizacao/    Composite: ComponenteDoAcervo, GrupoDoAcervo, ItemDoAcervo
├── persistencia/   Abstract Factory: FabricaDePersistencia, famílias memoria/ e mysql/
└── infra/          Relogio, RelogioAjustavel, Ids
```

**Camadas.** A interface (`Main`) chama apenas os tratadores. Os tratadores delegam aos pontos internos `AcervoDeLivros`, `Relacionamento` e `Movimentacao`, diretamente ou pela fachada `ServicoDeCirculacao`. As regras do domínio ficam no `Livro` (State) e nas classes de domínio.

---

## 5. Tabela de rastreabilidade

| Req. | Situação | Padrão | Modelos (págs. do PDF do Astah) | Papéis do padrão → classes | Testes que comprovam |
|---|---|---|---|---|---|
| 1 | Ciclo de vida do livro | **State** (+ Singleton nos estados) | classes p. 2 · estados p. 3 · sequência p. 4 | Context: `Livro` · State: `EstadoLivro` · ConcreteState: `Livre`, `Reservado`, `Emprestado`, `Renovado`, `Devolvido`, `Reparando`, `Incinerado` · Clientes: `ServicoDeCirculacao`, tratadores | `R01_terceiraRenovacaoRecusadaSemAlterarVencimento`, `R01_livroDevolvidoAindaNaoInspecionadoNaoPodeSerReservado`, `R01_eventoNaoAceitoPeloEstadoERecusadoSemAlterarOLivro`, `R01_laudoIrrecuperavelLevaDeReparandoAIncinerado`, `R01_cicloCompletoVoltaALivreELimpaOsDados`, `R01_estadoEGravadoComoSituacaoEReconstituidoNaLeitura` |
| 2 | Políticas de multa | **Strategy** | classes p. 5 · sequência p. 6 | Strategy: `PoliticaDeMulta` · ConcreteStrategy: `MultaDiaria`, `MultaComTeto`, `MultaComTolerancia` · Context: `ServicoDeMulta`, `ServicoDeSimulacao` · Seleção: `SeletorDePolitica`, `ConfiguracaoAdministrativa` | `R02_cincoDiasDaoCincoReaisNaOriginalETresReaisNaTolerancia`, `R02_semAtrasoOValorEZeroEmTodasAsPoliticas`, `R02_tetoLimitaEmVinteReais`, `R02_trocaDePoliticaNaoRecalculaMultaJaEmitida`, `R02_politicaInexistenteERecusadaAoConfigurar` |
| 3 | Bancos incompatíveis | **Adapter** (de objeto) | classes p. 7 · sequência p. 8 | Target: `GatewayDeCobranca` (saída), `ReceptorDePagamento` (entrada) · Adapter: `AdaptadorBancoA`, `AdaptadorBancoB` · Adaptee: `ApiBancoA`, `ApiBancoB` · Client: `ServicoDeMulta` · Idempotência: `ServicoDeQuitacao`, `RegistroDeCobrancas` | `R03_seteReaisViramSetecentosCentavosNoBancoA`, `R03_seteReaisViramSeteDecimalNoBancoB`, `R03_servicoDeMultasDependeSoDoContratoSemCondicionalPorBanco`, `R03_notificacaoRepetidaNaoGeraSegundaQuitacao`, `R03_notificacaoDeCobrancaDesconhecidaNaoQuitaNada`, `R03_valorComMaisDeDuasCasasERecusadoAntesDeChamarOBanco` |
| 4 | Entrada única de circulação | **Facade** | classes p. 9 · sequência p. 10 | Facade: `iCirculacao` / `ServicoDeCirculacao` · Subsistema: `Relacionamento`, `Movimentacao`, `AcervoDeLivros`, `Livro` · Clientes: `TratadorDeEmprestimo`, `TratadorDeDevolucao` | `R04_efetivacaoValidaReservaRemoveReservaCriaEmprestimoEAtualizaLivro`, `R04_interfaceRecebeComprovantesENaoColecoesInternas`, `R04_reservaDeOutroEstudanteNaoAlteraNada`, `R04_falhaNoMeioDaOperacaoDesfazOsPassosAnteriores` |
| 5 | Reações à devolução | **Observer** (modelo push) | classes p. 11 · sequência p. 12 | Subject: `PublicadorDeDevolucao` · Observer: `OuvinteDeDevolucao` · ConcreteObserver: `OuvinteInspecao`, `OuvinteMulta`, `OuvinteIndicadores`, `OuvinteEmail` · Evento: `EventoDevolucao` · Quem dispara: `TratadorDeDevolucao` | `R05_devolucaoAtrasadaAlimentaInspecaoMultaEIndicadores`, `R05_devolucaoNoPrazoNaoGeraMulta`, `R05_falhaDoEmailNaoAfetaOsDemaisNemDesfazADevolucao`, `R05_novoOuvinteEIncluidoSemAlterarOTratadorEEventoLevaOsDados`, `R05_decisaoDaInspecaoTiraOLivroDeDevolvido` |
| 6 | Solicitações como objetos | **Command** | classes p. 13 · sequência p. 14 | Command: `SolicitacaoDeCirculacao` · ConcreteCommand: `SolicitacaoDeReserva`, `SolicitacaoDeRenovacao` · Invoker: `ExecutorDeSolicitacoes` (+ `RegistroDeSolicitacoes`) · Receiver: `iCirculacao` · Client: `Main` (telas) | `R06_renovacaoExecutadaDuasVezesComOMesmoIdAcrescentaApenas30Dias`, `R06_solicitacoesDiferentesContinuamSujeitasAoLimiteDeDuas`, `R06_submissoesSimultaneasDoMesmoIdExecutamUmaUnicaVez`, `R06_reservaTambemESolicitacaoComIdAutorEInstante` |
| 7 | Rotina comum de cadastros | **Template Method** | classes p. 15 · sequência p. 16 | AbstractClass: `TratadorDeCadastro<T, D>` (método-modelo final `incluir`) · Primitivas: `buscarExistente`, `validar`, `criar`, `armazenar` · Gancho: `verificarPreRequisitos` · ConcreteClass: `TratadorDeAcervo`, `TratadorDePessoa`, `TratadorDeEstudante`, `TratadorDeAtendente` | `R07_estudanteDuplicadoRetornaOCadastroExistente`, `R07_estudanteComCpfNaoCadastradoFalhaAntesDeQualquerGravacao`, `R07_atendenteTambemExigePessoaCadastrada`, `R07_metodoModeloEFinalEOsPassosVariaveisSaoProtegidos`, `R07_validacoesPropriasDeCadaCadastro` |
| 8 | Famílias de acesso a dados | **Abstract Factory** | classes p. 17 · sequência p. 18 | AbstractFactory: `FabricaDePersistencia` · ConcreteFactory: `FabricaEmMemoria`, `FabricaMySQL` · AbstractProduct: `UnidadeDeTrabalho`, `RepositorioLivro`, `RepositorioMovimentacao`, `RepositorioRelacionamento` · ConcreteProduct: `...EmMemoria`, `...MySQL` · Escolha: `ConfiguradorDePersistencia` · Cliente: `SessaoDeDados` | `R08_configuracaoDeTesteForneceApenasObjetosEmMemoria`, `R08_configuracaoDeProducaoForneceRepositoriosMySQLQueCompartilhamAMesmaUnidade`, `R08_naoEPossivelMisturarRepositorioDeUmaFamiliaComUnidadeDeOutra`, `R08_perfilAusenteInterrompeEmVezDeEscolherPorOmissao`, `R08_desfazerDescartaTodasAsEscritasDaUnidade`, `MySQLIntegracaoTest` (opcional) |
| 9 | Consulta com recursos opcionais | **Decorator** | classes p. 19 · sequência p. 20 | Component: `ConsultaAcervo` · ConcreteComponent: `ConsultaAcervoBasica` · Decorator: `DecoradorDeConsulta` · ConcreteDecorator: `DecoradorDeMedicao`, `DecoradorDeCache` · Montagem: `MontadorDeConsulta` | `R09_segundaConsultaUsaCacheEAindaAssimGeraMedicao`, `R09_resultadoAntigoDoCacheNaoAutorizaReservaDeLivroQueDeixouDeEstarLivre`, `R09_cadeiaEMontadaConformeAInstalacaoComMedicaoPorFora`, `R09_cacheExpiradoVoltaAConsultarOAcervo` |
| 10 | Hierarquia do acervo | **Composite** | classes p. 21 · sequência p. 22 | Component: `ComponenteDoAcervo` · Leaf: `ItemDoAcervo` · Composite: `GrupoDoAcervo` · Client: `OrganizadorDoAcervo` (via `TratadorDeOrganizacao`) | `R10_unidadeComDuasSecoesDeTresEDoisLivrosInformaCinco`, `R10_moverLivroEntreSecoesNaoAlteraIdEstadoNemEmprestimo`, `R10_grupoRecusaCiclos`, `R10_grupoRecusaLivroQueJaPertenceAOutroGrupo`, `R10_moverParaGrupoInexistenteMantemOItemNaOrigem` |
| — | Fluxo da demonstração | todos | sequências de cada requisito (p. 4 a 22) | reserva, empréstimo, renovação, devolução atrasada, multa, pagamento e inspeção | `FluxoCompletoTest` |

---

## 6. Premissas e decisões de projeto

- **Renovação.** Usa a guarda `numRenovacoes < 2`, como na premissa da lista (corrige o `<= 2` da tabela da seção 11). Cada renovação soma 30 dias ao vencimento atual.
- **Prazo do primeiro empréstimo.** O enunciado não fixa o valor; a equipe adotou 7 dias (`Livro.PRAZO_EMPRESTIMO_DIAS`).
- **Máquina de estados.** A devolução leva a DEVOLVIDO, e a inspeção decide entre LIVRE e REPARANDO. O evento `registrarLaudoIrrecuperavel` leva REPARANDO a INCINERADO. Também mantivemos duas transições do Trabalho 02: LIVRE → REPARANDO (`enviarParaReparo`, dano no acervo) e a exclusão de um livro LIVRE (`excluir`).
- **Serviços f1 a f6.** Seguem a numeração do Trabalho 01:
  - f1 `reservarLivro`, f2 `consultarReservas`, f3 `emprestarLivro` e f4 `renovarEmprestimo` estão em `TratadorDeEmprestimo`.
  - f5 `registrarDevolucao` e f6 `emitirBoletoDeMulta` estão em `TratadorDeDevolucao`.
  - A multa é gerada automaticamente pelo `OuvinteMulta`. f6 emite a segunda via do boleto.
- **Command.** O receptor (`iCirculacao`) é entregue pelo invocador em `executar(circulacao)`. Assim, a tela cria a solicitação sem conhecer o receptor e continua chamando apenas o tratador. Não há `desfazer()`.
- **Estudante e Atendente.** São identificados pelo CPF da pessoa, com um papel de cada tipo por pessoa.
- **Persistência.** O Oracle continua como réplica do MySQL, mantida pela infraestrutura, e nenhuma fábrica cria repositório que escreva nele. A organização hierárquica do acervo (Composite), as multas, as cobranças e o registro de solicitações ficam em memória nesta versão.
- **Datas.** O `RelogioAjustavel` permite adiantar a data na demonstração para simular atrasos.
- **Bancos A e B.** São simulados por `ApiBancoA` e `ApiBancoB`, que imitam APIs incompatíveis: centavos e JSON no banco A, decimal, documento formatado e XML no banco B.

## 7. Uso de LLMs

| Ferramenta / modelo | Partes do sistema |
|---|---|
| Claude (Anthropic), modelo `claude-opus-5-5`, no aplicativo Claude (Cowork) | Geração inicial do código de todos os pacotes, dos testes `R01` a `R10`, do `FluxoCompletoTest`, do `schema.sql` e deste README |
| Claude (Anthropic), modelo `claude-opus-5-5`, no aplicativo Claude (Cowork) | Diagramas UML do Trabalho 03 descritos em PlantUML a partir do código (`docs/uml/`), importados no Astah (plugin PlantUML) e exportados em PDF (`docs/Trabalho03-Modelos-Astah.pdf`). O diagrama de classes do R01 foi desenhado pela equipe no Astah |

O resumo dos pedidos e do que a equipe aceitou, corrigiu ou descartou está em [`REGISTRO_LLM.md`](REGISTRO_LLM.md).

## 8. Modelos UML

Os modelos estão na pasta [`docs/`](docs):

| Arquivo | Conteúdo |
|---|---|
| `docs/Trabalho03-AnaBruginski_HugoSouza_JoaoMacena_NathanBueno.asta` | Modelo do Astah: o modelo do Trabalho 02 atualizado, com a pasta **Padroes GoF** contendo o diagrama de classes de cada padrão (R01 a R10), a máquina de estados do `Livro` e os diagramas de sequência dos dez cenários (SEQ R01 a SEQ R10) |
| `docs/Trabalho03-Modelos-Astah.pdf` | Diagramas do Astah exportados em PDF, organizados por requisito: para cada padrão, o diagrama de classes seguido do diagrama de sequência do seu cenário (R01: classes p. 2, máquina de estados p. 3, sequência p. 4; R02 a R10: p. 5 a 22). O sumário está na p. 1 |
| `docs/uml/*.puml` | Fonte de cada diagrama, em PlantUML, com os mesmos nomes de classes, atributos, operações e mensagens do código |

A coluna "Modelos" da tabela de rastreabilidade (seção 5) indica a página de cada requisito no PDF do Astah. Os nomes seguem o código: por exemplo, o estado é representado pela classe abstrata `EstadoLivro` e persistido pela enumeração `Situacao`, e as operações de cadastro usam o CPF como chave.

