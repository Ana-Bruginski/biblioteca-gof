# Registro de uso de LLM

**Ferramenta:** Claude (Anthropic), modelo `claude-opus-5-5`, no aplicativo Claude (modo Cowork).
**Período:** novembro de 2026.
**Equipe:** Ana Luiza Bruginski de Melo, Hugo Souza Jamielniak, João Pedro Macena Correa e Nathan Bueno Catenacci Okazaki.

## Participação da equipe

Cada integrante ficou responsável por desenvolver sua própria parte do projeto, conforme a divisão dos requisitos apresentada abaixo. A equipe utilizou o Claude (Anthropic) como apoio para ajustes na interface, principalmente na organização e apresentação das informações, buscando deixá-la mais clara e agradável. As implementações dos requisitos foram realizadas individualmente por cada responsável.

### Divisão por integrante

| Integrante | Requisitos sob sua responsabilidade |
|---|---|
| Nathan Bueno Catenacci Okazaki | R01 e R04 |
| João Pedro Macena Correa | R05, R02 e R03 |
| Ana Luiza Bruginski de Melo | R06, R07 e R08 |
| Hugo Souza Jamielniak | R09 e R10 |

## Uso do modelo

O Claude (Anthropic) foi utilizado como ferramenta de apoio para aprimorar a interface do sistema, especialmente na organização visual e na apresentação das informações. Seu uso teve como objetivo deixar a interação mais clara e agradável.

A implementação dos requisitos R01 a R10 foi dividida entre os integrantes, e cada um desenvolveu a parte sob sua responsabilidade, conforme indicado na seção anterior.

## Verificações feitas pela equipe

- Testes com Maven no IntelliJ em 23/11/2026: `Tests run: 51, Failures: 0, Errors: 0, Skipped: 1` e `BUILD SUCCESS`. O teste ignorado é o opcional da família MySQL.
- Perfil de produção executado no MySQL 8.0: banco `biblioteca_ib2b` e tabelas criados pelo `schema.sql`, e livros cadastrados pela interface conferidos no MySQL Workbench.
- Fluxo completo executado pela interface no perfil de teste.
- Nomes dos modelos UML conferidos com as classes, atributos, operações e mensagens do código.

## Correções e descartes

- Nenhum trecho de código foi descartado.
- README complementado com a execução pelo IntelliJ (`-Dperfil=TESTE` para rodar sem banco) e com a senha do MySQL passada por `-Dmysql.senha`.
- Diagramas do Astah corrigidos: interfaces com realizações, notas e estados sem sobreposição, máquina de estados reorganizada.
- Modelos do Trabalho 02 atualizados para os nomes do código: `EstadoLivro` como classe abstrata do State, enumeração `Situacao`, CPF como chave em `iEstudante`, `iAtendente` e `iPessoa`, e `TratadorDeMulta` no financeiro.
