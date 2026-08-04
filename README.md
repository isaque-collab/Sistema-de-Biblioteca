# Sistema de Gerenciamento de Biblioteca

Sistema de gerenciamento de biblioteca desenvolvido em **Java 21**, utilizando **JDBC**, **MySQL**, **Docker** e arquitetura em camadas.

> **Status: Projeto concluído**

O projeto foi desenvolvido como peça de portfólio com o objetivo de aplicar conceitos utilizados no desenvolvimento back-end de aplicações reais, priorizando organização do código, separação de responsabilidades, regras de negócio, integridade dos dados, tratamento de exceções, testes e boas práticas de programação.

---

## Objetivo

Desenvolver uma aplicação capaz de gerenciar o funcionamento de uma biblioteca, permitindo o controle de usuários, autores, categorias, livros e empréstimos através de uma arquitetura organizada e desacoplada.

Durante o desenvolvimento foram aplicados conceitos como:

- Programação Orientada a Objetos
- JDBC
- SQL
- MySQL
- Transações
- Controle de concorrência
- Tratamento de exceções
- Padrões de Projeto
- Streams
- Arquitetura em Camadas
- Testes unitários
- Testes de integração
- Testes de concorrência
- Boas práticas de desenvolvimento

---

# Funcionalidades

## Usuários

- Cadastro
- Atualização
- Consulta por ID
- Consulta por nome
- Listagem completa
- Exclusão
- Validação de CPF
- Validação de e-mail
- Controle de registros duplicados

## Autores

- Cadastro
- Atualização
- Consulta por ID
- Consulta por nome
- Listagem completa
- Exclusão
- Validação de dados
- Controle de registros duplicados

## Categorias

- Cadastro
- Atualização
- Consulta por ID
- Listagem completa
- Exclusão
- Validação de dados
- Controle de registros duplicados

## Livros

- Cadastro
- Atualização
- Consulta por ID
- Listagem completa
- Controle de estoque
- Validação de ISBN-10
- Validação de ISBN-13
- Relacionamento com autor
- Relacionamento com categoria
- Exclusão

## Empréstimos

- Registro de empréstimos
- Registro de devoluções
- Consulta por ID
- Listagem completa
- Controle transacional utilizando JDBC
- Atualização automática do estoque
- Controle de concorrência
- Proteção contra empréstimos simultâneos do último exemplar
- Regra de um empréstimo ativo por livro e usuário
- Cálculo de multas
- Determinação da situação do empréstimo:
  - ATIVO
  - DEVOLVIDO
  - ATRASADO

---

# Relatórios

O módulo de relatórios foi dividido em duas categorias de acordo com a natureza do processamento.

## Categoria A — Relatórios analíticos SQL

Esses relatórios utilizam consultas SQL com agregações e agrupamentos realizados diretamente no banco de dados.

### Livros mais emprestados

Apresenta os livros ordenados de acordo com a quantidade de empréstimos registrados.

### Usuários com mais empréstimos

Apresenta os usuários ordenados de acordo com a quantidade de empréstimos realizados.

### Empréstimos por categoria

Apresenta a quantidade de empréstimos agrupada por categoria.

### Total de empréstimos

Apresenta a quantidade total de empréstimos registrados, independentemente do status.

---

## Categoria B — Relatórios baseados em regras de negócio

Esses relatórios utilizam regras implementadas na camada de serviço.

### Usuários com empréstimos em atraso

Identifica usuários que possuem empréstimos atrasados em relação à data de referência.

### Multas projetadas por usuário

Calcula o valor das multas projetadas para cada usuário.

### Valor total de multas projetadas

Calcula o valor total das multas projetadas de todos os usuários.

O cálculo das multas utiliza o **Strategy Pattern**, permitindo diferentes estratégias de cálculo sem modificar a lógica principal do serviço.

---

# Tecnologias

- **Java 21**
- **Maven**
- **JDBC**
- **MySQL 8.4**
- **SQL**
- **Docker**
- **Docker Compose**
- **Lombok**
- **Log4j2**
- **JUnit 5**

---

# Arquitetura

O projeto segue uma arquitetura em camadas, separando responsabilidades entre a interface de console, regras de negócio e acesso aos dados.

```text
┌──────────────────────────────┐
│          CLI / App           │
│       Menus e Console        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│           Service            │
│       Regras de negócio      │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         Repository           │
│       Acesso via JDBC        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│           MySQL              │
│         Banco de dados       │
└──────────────────────────────┘
```

## Camadas

| Camada | Responsabilidade |
|---|---|
| `Model` | Representação das entidades |
| `App` | Interface de console e menus |
| `Service` | Regras de negócio, validações e orquestração |
| `Repository` | Comunicação com o banco utilizando JDBC |
| `DTO` | Representação de dados utilizados nos relatórios |
| `Strategy` | Estratégias de cálculo de multas |
| `Validator` | Validação e normalização dos dados |
| `Exception` | Exceções específicas da aplicação |
| `Util` | Funcionalidades utilitárias |
| `Database` | Configuração e conexão com o banco |

---

# Fluxo da aplicação

As operações seguem o fluxo:

```text
Entrada do usuário
        │
        ▼
       CLI
        │
        ▼
     Service
        │
        ├── Validações
        │
        ├── Regras de negócio
        │
        ▼
    Repository
        │
        ▼
      MySQL
        │
        ▼
Tratamento de exceções
        │
        ▼
Retorno ao usuário
```

A camada de apresentação permanece desacoplada das regras de negócio e do acesso ao banco de dados.

---

# Transações e concorrência

As operações de empréstimo e devolução utilizam **transações JDBC** para garantir a consistência das alterações realizadas no empréstimo e no estoque do livro.

O controle de estoque utiliza operações atômicas no banco de dados.

Exemplo:

```sql
UPDATE livro
SET quantidade_disponivel = quantidade_disponivel - 1
WHERE id = ?
  AND quantidade_disponivel > 0;
```

A aplicação verifica a quantidade de linhas afetadas para determinar se o exemplar estava disponível.

Dessa forma, duas operações concorrentes não conseguem retirar simultaneamente o último exemplar disponível.

Esse mecanismo evita problemas de condição de corrida durante empréstimos concorrentes.

---

# Controle de estoque

O estoque dos livros é representado por duas informações:

```text
quantidade_total
quantidade_disponivel
```

Exemplo:

```text
Livro
├── quantidade_total = 5
└── quantidade_disponivel = 3
```

Quando um empréstimo é realizado:

```text
5 total
3 disponíveis
      ↓
2 disponíveis
```

Quando o livro é devolvido:

```text
2 disponíveis
      ↓
3 disponíveis
```

As alterações são realizadas de forma atômica para preservar a consistência do estoque.

---

# Sistema de multas

O cálculo de multas foi implementado utilizando o **Strategy Pattern**.

Atualmente existem estratégias para:

- Multa linear
- Multa com período de carência

A estratégia pode ser substituída sem alterar a lógica principal do `EmprestimoService`.

Estrutura conceitual:

```text
              MultaStrategy
                   │
          ┌────────┴────────┐
          ▼                 ▼
 MultaLinearStrategy   MultaComCarenciaStrategy
```

A estratégia utilizada é definida na composição da aplicação.

Exemplo:

```java
new MultaComCarenciaStrategy(
    new BigDecimal("2.00"),
    3
);
```

Nesse exemplo, a multa diária é de `R$ 2,00` e existe um período de carência de `3` dias.

---

# Situação do empréstimo

O banco persiste os estados:

```text
ATIVO
DEVOLVIDO
```

O estado `ATRASADO` é derivado pela aplicação de acordo com a data de referência.

A lógica é conceitualmente:

```text
                 Empréstimo
                     │
                     ▼
              Foi devolvido?
               /          \
             SIM           NÃO
              │             │
              ▼             ▼
          DEVOLVIDO    Passou da data?
                         /       \
                       SIM       NÃO
                        │         │
                        ▼         ▼
                    ATRASADO    ATIVO
```

Dessa forma, o estado de atraso não precisa ser persistido diretamente no banco de dados.

---

# Validações

O projeto possui validadores específicos para diferentes tipos de dados.

### CPF

O `CpfValidator` realiza as validações relacionadas ao CPF.

### E-mail

O `EmailValidator` valida o formato dos endereços de e-mail.

### ISBN

O `IsbnValidator` realiza validações para:

- ISBN-10
- ISBN-13

As validações são realizadas antes da persistência dos dados.

---

# Tratamento de exceções

A aplicação possui exceções específicas para diferentes situações.

Entre elas:

- `PersistenciaException`
- `ValidacaoException`
- `RegistroNaoEncontradoException`
- `RegistroDuplicadoException`
- `EstoqueIndisponivelException`
- `EmprestimoAtivoExistenteException`
- `EmprestimoJaDevolvidoException`

Na interface de console, as exceções esperadas são tratadas de forma centralizada através do `ConsoleUtil`.

Isso permite apresentar mensagens amigáveis ao usuário sem expor stack traces para erros previstos da aplicação.

Exemplo:

```text
Usuário não encontrado.
```

em vez de apresentar uma exceção completa no console.

Erros inesperados continuam sendo registrados no log para facilitar o diagnóstico.

---

# Logging

A aplicação utiliza **Log4j2** para registro de eventos e erros.

O logging é utilizado principalmente para:

- Erros de persistência
- Erros inesperados
- Informações importantes para diagnóstico

As exceções inesperadas são registradas com seus detalhes técnicos no log, enquanto o usuário recebe uma mensagem genérica e amigável.

---

# Testes

O projeto utiliza **JUnit 5** para testes unitários e de integração.

Foram implementados testes para diferentes partes do sistema.

## Testes unitários

Incluem testes relacionados a:

- Estratégias de cálculo de multa
- Validações
- Regras de negócio
- Determinação da situação do empréstimo

## Testes de integração

Incluem testes relacionados a:

- Cadastro de entidades
- Empréstimos
- Devoluções
- Atualização de estoque
- Relatórios
- Cálculo de multas
- Integração entre diferentes serviços e repositórios

## Testes de concorrência

Também foi desenvolvido um teste específico para verificar o comportamento do estoque diante de operações concorrentes de empréstimo.

O objetivo é garantir que o último exemplar disponível não seja emprestado simultaneamente para dois usuários.

---

# Banco de Dados

O projeto utiliza **MySQL** como banco de dados relacional.

O script de criação do banco está localizado em:

```text
database/
└── schema.sql
```

O banco possui:

- Chaves primárias
- Chaves estrangeiras
- Constraints `UNIQUE`
- Constraints `CHECK`
- Integridade referencial
- Relacionamentos entre entidades

As operações de exclusão respeitam as dependências entre as tabelas.

Por exemplo:

```text
Autor
  │
  └── Livro
        │
        └── Empréstimo
```

A integridade referencial é garantida pelo próprio banco de dados.

---

# Estrutura do Projeto

```text
database/
└── schema.sql

src/
├── main/
│   ├── java/
│   │   └── br/com/biblioteca/
│   │       ├── app/
│   │       ├── config/
│   │       ├── database/
│   │       ├── dto/
│   │       ├── enums/
│   │       ├── exception/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       ├── strategy/
│   │       ├── util/
│   │       └── validator/
│   │
│   └── resources/
│
└── test/
    └── java/
        ├── integration/
        ├── service/
        ├── strategy/
        └── manual/
```

---

# Padrões de Projeto

## Singleton

Utilizado na `ConexaoFactory` para centralizar o gerenciamento da conexão com o banco de dados.

## Strategy

Utilizado no sistema de multas para permitir diferentes estratégias de cálculo.

A utilização do Strategy evita que regras diferentes de cálculo fiquem acopladas diretamente ao `EmprestimoService`.

---

# Decisões de Projeto

Durante o desenvolvimento foram tomadas algumas decisões importantes.

### Controle de estoque

O livro utiliza:

```text
quantidade_total
quantidade_disponivel
```

permitindo representar tanto a quantidade total de exemplares quanto a quantidade atualmente disponível.

### Regras de negócio

As regras de negócio permanecem concentradas na camada `Service`.

### Acesso ao banco

O acesso ao banco é realizado exclusivamente pelos `Repository`, utilizando JDBC.

### Integridade dos dados

O banco de dados possui constraints e chaves estrangeiras para garantir a integridade dos dados.

### Situação do empréstimo

O estado `ATRASADO` é derivado pela aplicação em vez de ser persistido diretamente.

### Multas

O cálculo de multas utiliza o Strategy Pattern.

### Concorrência

O controle de estoque utiliza atualização atômica no banco para evitar condições de corrida.

### Relatórios

Os relatórios analíticos utilizam agregações SQL, enquanto os relatórios dependentes de regras de negócio reutilizam os serviços responsáveis por essas regras.

### Exceções

O tratamento das exceções esperadas na interface de console é centralizado através do `ConsoleUtil`.

---

# Interface de Console

A aplicação possui uma interface de console organizada em menus.

## Menu principal

```text
========================================
Sistema de Gerenciamento de Biblioteca
========================================

1 - Usuários
2 - Autor / Categoria
3 - Livros
4 - Empréstimos
5 - Relatórios
0 - Sair
```

Os menus são responsáveis somente pela interação com o usuário.

As regras de negócio continuam sendo executadas pelos Services.

---

# Fluxo de demonstração

Para uma demonstração completa da aplicação, pode ser utilizado o seguinte roteiro:

### 1. Cadastrar autor e categoria

Menu:

```text
2 - Autor / Categoria
```

Cadastrar:

- 1 autor
- 1 categoria

### 2. Cadastrar livro

Menu:

```text
3 - Livros
```

Utilizar os IDs apresentados para autor e categoria.

### 3. Cadastrar usuário

Menu:

```text
1 - Usuários
```

Cadastrar 1 usuário.

### 4. Realizar empréstimo

Menu:

```text
4 - Empréstimos
1 - Emprestar
```

A aplicação apresenta os usuários e livros cadastrados antes de solicitar os respectivos IDs.

### 5. Executar os relatórios

Menu:

```text
5 - Relatórios
```

Executar as sete opções disponíveis.

Com apenas um empréstimo registrado, os relatórios analíticos devem apresentar contagem igual a `1` para o livro, usuário e categoria envolvidos.

### 6. Devolver o empréstimo

Menu:

```text
4 - Empréstimos
2 - Devolver
```

Informar o ID do empréstimo.

### 7. Testar entrada inválida

Informar uma opção fora do intervalo permitido, como:

```text
99
```

O sistema deve solicitar uma nova opção sem encerrar a aplicação.

### 8. Testar registro inexistente

Tentar buscar um ID inexistente, como:

```text
9999
```

O sistema deve apresentar uma mensagem amigável e retornar ao menu sem exibir stack trace.

---

# Como executar

## Pré-requisitos

Antes de executar o projeto, certifique-se de possuir:

- Java 21
- Maven
- Docker
- Docker Compose

---

## 1. Clonar o projeto

```bash
git clone <URL_DO_REPOSITORIO>
```

Entre no diretório:

```bash
cd <NOME_DO_PROJETO>
```

---

## 2. Subir o banco de dados

Inicie o ambiente MySQL utilizando o Docker Compose configurado no projeto.

```bash
docker compose up -d
```

Verifique os containers:

```bash
docker compose ps
```

---

## 3. Criar o banco e as tabelas

Execute o script:

```text
database/schema.sql
```

Esse script cria a estrutura necessária para execução da aplicação.

---

## 4. Compilar e executar os testes

Utilize o Maven:

```bash
mvn clean verify
```

Esse comando realiza a compilação do projeto e executa a suíte de testes configurada.

---

## 5. Executar a aplicação

Execute a classe principal:

```text
br.com.biblioteca.app.Main
```

A aplicação será iniciada através da interface de console.

---

# Principais aprendizados

O desenvolvimento deste projeto permitiu aprofundar conhecimentos em:

- Java
- Programação Orientada a Objetos
- JDBC
- SQL
- MySQL
- Maven
- JUnit
- Testes de integração
- Testes de concorrência
- Transações
- Controle de estoque
- Integridade referencial
- Tratamento de exceções
- Logging
- Streams
- Padrões de Projeto
- Arquitetura em camadas
- Separação de responsabilidades
- Regras de negócio
- Desenvolvimento de aplicações back-end

Um dos principais aprendizados foi perceber que desenvolver uma aplicação não significa apenas fazer o código funcionar.

Durante o projeto foi necessário lidar com problemas reais relacionados a:

- Concorrência
- Transações
- Integridade de dados
- Regras de negócio
- Validação
- Testes
- Tratamento de erros
- Organização arquitetural

Esses desafios contribuíram para uma compreensão mais prática do desenvolvimento de software além da implementação das funcionalidades básicas.

---

# Status

## Projeto concluído

O escopo definido para a primeira versão foi implementado.

O sistema possui:

- Gerenciamento de usuários
- Gerenciamento de autores
- Gerenciamento de categorias
- Gerenciamento de livros
- Controle de estoque
- Empréstimos
- Devoluções
- Controle transacional
- Controle de concorrência
- Sistema de multas
- Relatórios
- Tratamento de exceções
- Logging
- Testes unitários
- Testes de integração
- Testes de concorrência
- Interface de console

Novas funcionalidades podem ser adicionadas futuramente como evolução do projeto, mas não fazem parte do escopo atual.

---

# Autor

Desenvolvido por **Isaque Costa da Cunha** como projeto de portfólio para aprofundamento em **Java e desenvolvimento Back-end**.