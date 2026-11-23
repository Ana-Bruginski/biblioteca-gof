# Registro de uso de LLM

**Ferramenta:** Claude (Anthropic), modelo `claude-opus-5-5`, usado no aplicativo Claude (modo Cowork).
**Período:** novembro de 2026.
**Responsável pelo registro:** a equipe (Ana Luiza Bruginski de Melo, Hugo Souza Jamielniak, João Pedro Macena Correa, Nathan Bueno Catenacci Okazaki).

> A coluna **Decisão da equipe** registra, para cada pedido, se a equipe aceitou, corrigiu ou descartou o que o modelo entregou.

## Pedidos principais

| # | Pedido feito ao modelo | O que o modelo entregou | Decisão da equipe |
|---|---|---|---|
| 1 | Ler o enunciado do Trabalho 03, a lista de problemas GoF e o PDF de soluções do professor, e propor a estrutura do projeto | Estrutura em pacotes por padrão, preservando tratadores, delegação e os pontos internos Acervo, Relacionamento e Movimentacao | Aceito: interface em linha de comando escolhida pela equipe |
| 2 | Implementar o ciclo de vida do Livro com State, alinhado à máquina de estados | `Livro`, `EstadoLivro`, sete estados (instâncias únicas), guarda `numRenovacoes < 2`, laudo irrecuperável, gravação do estado como `Situacao` | Aceito: prazo do primeiro empréstimo (7 dias) definido como premissa da equipe |
| 3 | Implementar as políticas de multa com Strategy | `PoliticaDeMulta`, três políticas, `SeletorDePolitica`, `ServicoDeMulta`, `ServicoDeSimulacao`, `Multa` imutável com política aplicada | Aceito |
| 4 | Integrar dois bancos incompatíveis com Adapter | `GatewayDeCobranca`, `ReceptorDePagamento`, adaptadores A e B, APIs simuladas, quitação idempotente | Aceito: bancos simulados, como o enunciado permite |
| 5 | Criar a fachada de circulação | `iCirculacao`, `ServicoDeCirculacao` transacional, comprovantes (DTOs) | Aceito |
| 6 | Reações à devolução com Observer | Publicador com isolamento de falhas, evento com os dados (push), quatro ouvintes | Aceito |
| 7 | Solicitações de reserva e renovação com Command | Solicitações com id, autor e instante; executor idempotente; registro atômico | Aceito |
| 8 | Cadastros com Template Method | `TratadorDeCadastro` com `incluir` final e gancho de pré-requisito; quatro tratadores concretos | Aceito |
| 9 | Famílias de persistência com Abstract Factory | Fábricas em memória e MySQL, unidade de trabalho, repositórios e `schema.sql` | Aceito: família MySQL conferida contra servidor compatível |
| 10 | Consulta com Decorator | Contrato de consulta, implementação básica, decoradores de medição e de cache, montador | Aceito |
| 11 | Organização do acervo com Composite | Grupos e itens, contagem recursiva, recusa de ciclos e de livro em dois grupos | Aceito |
| 12 | Escrever os testes dos dez cenários de verificação | Testes `R01` a `R10`, `FluxoCompletoTest` e teste opcional com MySQL | Aceito: testes executados pela equipe em 23/11/2026 |
| 13 | Escrever o README, a tabela de rastreabilidade e este registro | `README.md` e `REGISTRO_LLM.md` | Aceito: README complementado com execução pelo IntelliJ e seção de modelos |
| 14 | Gerar os modelos UML do Trabalho 03 a partir do código: diagrama de classes de cada padrão, máquina de estados do Livro e sequências dos cenários | `docs/uml/*.puml`; os diagramas foram levados ao Astah (pasta Padroes GoF do `.asta`) com o PlantUML Plugin oficial do Astah. O diagrama de classes do R01 foi desenhado pela equipe no Astah. Os diagramas do Astah foram exportados em `docs/Trabalho03-Modelos-Astah.pdf` | Aceito com correções: nomes conferidos com o código; no Astah foram ajustadas as realizações das interfaces e a posição de notas e estados que ficaram sobrepostos |
| 15 | Explicar o projeto para a arguição (arquitetura, cada padrão, perguntas prováveis) | Guia de estudo e manual dos modelos, usados pela equipe na preparação (fora do repositório) | Aceito como material de estudo |

## Verificações feitas antes da entrega

- Testes executados pela equipe em 23/11/2026, com Maven, no IntelliJ: `Tests run: 51, Failures: 0, Errors: 0, Skipped: 1` e `BUILD SUCCESS`. O teste ignorado é o opcional da família MySQL, que só roda com o banco informado na linha de comando.
- A família MySQL foi testada contra um servidor compatível com MySQL (MariaDB 10.11): tabelas criadas pelo `schema.sql` e fluxo de reserva, empréstimo, renovação e devolução gravado e lido.
- O fluxo completo da demonstração foi executado pela interface de linha de comando no perfil de teste.
- Os nomes dos modelos UML (`docs/`) foram conferidos com as classes, atributos, operações e mensagens do código.

## Correções e descartes feitos pela equipe

- Nenhum trecho de código foi descartado.
- O README foi complementado com a execução pelo IntelliJ: o perfil padrão é o de produção (MySQL); para rodar sem banco, usa-se `-Dperfil=TESTE`.
- Os modelos do Trabalho 02 foram atualizados nos diagramas do Trabalho 03 para seguir os nomes do código: `EstadoLivro` passou a ser a classe abstrata do State, e a enumeração persistida passou a se chamar `Situacao`; as operações de `iEstudante`, `iAtendente` e `iPessoa` usam o CPF como chave; o tratador do financeiro é `TratadorDeMulta`.
