# Sistema de Biblioteca

Sistema de gerenciamento de biblioteca desenvolvido em **Java**, utilizando **JDBC**, **MySQL**, **Docker** e arquitetura em camadas.

> **Status:** Em desenvolvimento (Etapa 8 concluída)

O projeto está sendo desenvolvido como peça de portfólio com o objetivo de aplicar conceitos utilizados no desenvolvimento back-end de aplicações reais, priorizando organização do código, separação de responsabilidades, regras de negócio, tratamento de exceções e boas práticas de programação.

---

# Objetivo

Desenvolver uma aplicação capaz de gerenciar o funcionamento de uma biblioteca, permitindo o controle de usuários, autores, categorias, livros e empréstimos através de uma arquitetura organizada, desacoplada e escalável.

Durante o desenvolvimento são praticados conceitos como:

- Programação Orientada a Objetos
- JDBC
- SQL
- MySQL
- Transações
- Tratamento de Exceções
- Padrões de Projeto
- Streams
- Arquitetura em Camadas
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

## Autores

- Cadastro
- Atualização
- Consulta por ID
- Consulta por nome
- Listagem completa
- Exclusão

## Categorias

- Cadastro
- Atualização
- Consulta por ID
- Consulta por nome
- Listagem completa
- Exclusão

## Livros

- Cadastro
- Atualização
- Consulta por ID
- Consulta por ISBN
- Listagem completa
- Controle de estoque
- Exclusão

## Empréstimos

- Registro de empréstimos
- Registro de devoluções
- Consulta por ID
- Listagem completa
- Controle transacional utilizando JDBC
- Atualização automática do estoque
- Controle de concorrência para empréstimos e devoluções
- Cálculo de multas utilizando Strategy Pattern
- Determinação da situação do empréstimo (Ativo, Devolvido ou Atrasado)
- Relatórios de empréstimos atrasados
- Identificação de usuários com empréstimos em atraso
- Cálculo de multas projetadas por usuário
- Cálculo do total de multas projetadas

## Relatórios

## Relatórios implementados

- Usuários com empréstimos atrasados
- Multas projetadas por usuário
- Total de multas projetadas


## Relatórios planejados

- Livros mais emprestados
- Usuários com mais empréstimos
- Usuários inadimplentes
- Ranking de devedores
- Estatísticas gerais

---

# Tecnologias

- Java 21
- Maven
- JDBC
- MySQL 8.4
- SQL (DDL)
- Docker
- Lombok
- Log4j2
- JUnit 5

---

# Arquitetura

O projeto segue uma arquitetura em camadas.

```
CLI
        │
        ▼
Service
        │
        ▼
Repository
        │
        ▼
MySQL
```

Cada camada possui uma responsabilidade específica.

| Camada | Responsabilidade |
|---------|------------------|
| Model | Representação das entidades |
| Repository | Comunicação com o banco utilizando JDBC |
| Service | Regras de negócio, validações e tratamento de exceções |
| Validator | Validação e normalização dos dados |
| Exception | Exceções específicas da aplicação |
| Util | Classes utilitárias (ConexaoFactory, etc.) |

---

# Fluxo da aplicação

Toda operação segue o mesmo fluxo.

```
Entrada do usuário (CLI)

↓

Service

↓

Validação dos dados

↓

Aplicação das regras de negócio

↓

Repository

↓

Banco de Dados

↓

Tratamento de exceções

↓

Retorno ao usuário
```

Dessa forma a camada de apresentação permanece desacoplada das regras de negócio e do acesso ao banco.

---

# Fluxo das principais operações

## Cadastro de usuário

### Processamento

1. O CPF é validado e normalizado.
2. O e-mail é validado e normalizado.
3. É realizada consulta para verificar duplicidade de CPF.
4. É realizada consulta para verificar duplicidade de e-mail.
5. O usuário é persistido utilizando o `UsuarioRepository`.
6. Violações de constraints UNIQUE são traduzidas para exceções específicas.

### Saída

Retorna o usuário cadastrado.

### Possíveis exceções

- CpfInvalidoException
- EmailInvalidoException
- CpfJaCadastradoException
- EmailJaCadastradoException
- PersistenciaException

---

## Cadastro de autor

### Processamento

1. Valida o nome do autor.
2. Normaliza os dados.
3. Persiste o autor.
4. Traduz erros de persistência para exceções da aplicação.

### Saída

Retorna o autor cadastrado.

---

## Cadastro de categoria

### Processamento

1. Valida o nome da categoria.
2. Normaliza os dados.
3. Verifica duplicidade.
4. Persiste a categoria.
5. Traduz violações de UNIQUE para exceções específicas.

### Saída

Retorna a categoria cadastrada.

---

## Cadastro de livro

### Processamento

1. O ISBN é validado (ISBN-10 ou ISBN-13).
2. A quantidade total é validada.
3. A quantidade disponível é inicializada com a quantidade total.
4. Verifica duplicidade de ISBN.
5. Persiste o livro.
6. Traduz violações de UNIQUE para exceções específicas.

### Saída

Retorna o livro cadastrado.

## Registro de empréstimo

### Processamento

1. Verifica se o usuário existe.
2. Verifica se o livro existe.
3. Inicia uma transação JDBC.
4. Realiza uma atualização atômica do estoque.
5. Caso exista disponibilidade, registra o empréstimo.
6. Confirma a transação (`commit`).
7. Em caso de falha, desfaz todas as alterações (`rollback`).

---

## Registro de devolução

### Processamento

1. Localiza o empréstimo.
2. Verifica se ele ainda está ativo.
3. Inicia uma transação JDBC.
4. Atualiza o status do empréstimo para DEVOLVIDO.
5. Atualiza o estoque do livro.
6. Calcula a multa conforme a estratégia configurada.
7. Confirma a transação (`commit`).
8. Em caso de erro realiza `rollback`.

### Saída

Retorna o empréstimo atualizado juntamente com o valor da multa.

### Possíveis exceções

- EmprestimoNaoEncontradoException
- EmprestimoJaDevolvidoException
- PersistenciaException

### Saída

Retorna o empréstimo registrado.

### Possíveis exceções

- UsuarioNaoEncontradoException
- LivroNaoEncontradoException
- EstoqueIndisponivelException
- PersistenciaException

---

# Validações implementadas

## Usuário

- Validação de CPF com cálculo dos dígitos verificadores.
- Normalização do CPF.
- Validação de e-mail.
- Verificação de duplicidade de CPF.
- Verificação de duplicidade de e-mail.

## Livro

- Validação de ISBN-10.
- Validação de ISBN-13.
- Normalização do ISBN.
- Verificação de duplicidade de ISBN.
- Validação da quantidade total de exemplares.

## Autor

- Nome obrigatório.
- Remoção de espaços excedentes.

## Categoria

- Nome obrigatório.
- Remoção de espaços excedentes.
- Verificação de duplicidade.

---

# O que já foi implementado

## Banco de Dados

- Modelagem relacional completa.
- Script versionado (`database/schema.sql`).
- Chaves primárias.
- Chaves estrangeiras.
- Constraints UNIQUE.
- Constraints CHECK.
- Integridade referencial.

## Camada Model

- Autor
- Categoria
- Livro
- Usuario
- Emprestimo
- StatusEmprestimo

## Camada Repository

### UsuarioRepository

- CRUD completo
- Consulta por CPF
- Consulta por e-mail
- Consulta por nome

### LivroRepository

- CRUD completo
- Consulta por ISBN

### AutorRepository

- CRUD completo
- Consulta por ID
- Consulta por nome
- Listagem completa

### CategoriaRepository

- CRUD completo
- Consulta por ID
- Consulta por nome
- Listagem completa

### EmprestimoRepository

- Cadastro de empréstimos
- Registro de devoluções
- Consulta por ID
- Listagem completa
- Atualizações atômicas
- Suporte a transações compartilhando a mesma Connection

## Camada Service

### UsuarioService

- Cadastro
- Atualização
- Exclusão
- Consultas
- Validações
- Tratamento de exceções
- Tradução de constraints UNIQUE

### LivroService

- Cadastro
- Atualização
- Exclusão
- Consultas
- Controle inicial de estoque
- Validação de ISBN
- Tratamento de exceções
- Tradução de constraints UNIQUE

### AutorService

- Cadastro
- Atualização
- Exclusão
- Consultas
- Validação dos dados
- Tratamento de exceções

### CategoriaService

- Cadastro
- Atualização
- Exclusão
- Consultas
- Validação dos dados
- Verificação de duplicidade
- Tratamento de exceções
- Tradução de constraints UNIQUE

### EmprestimoService

- Registro de empréstimos
- Registro de devoluções
- Controle de transações JDBC
- Atualização automática do estoque
- Controle de concorrência
- Cálculo de multas
- Determinação da situação do empréstimo
- Tratamento de exceções

## Validator

- CpfValidator
- EmailValidator
- IsbnValidator
- NomeValidator

## Padrões de Projeto

- Singleton — gerenciamento da conexão com o banco (`ConexaoFactory`)
- Strategy — cálculo de multas por atraso

## Exception

Hierarquia de exceções específicas para validação, persistência e regras de negócio.

## Logging

A aplicação utiliza **Log4j2** para registro de eventos e erros da camada de serviço.

Atualmente está configurado para:

- Logs da aplicação em nível **INFO**
- Logs das bibliotecas externas em nível **WARN**
- Saída formatada no console

## Testes

O projeto utiliza **JUnit 5** para testes unitários e de integração.

Atualmente foram implementados testes para:

- Estratégias de cálculo de multa;
- Determinação da situação do empréstimo;
- Fluxo de devolução;
- Fluxo completo de empréstimo e devolução;
- Controle de concorrência durante empréstimos.
- Relatórios de atraso e multas projetadas;
- Cenários dentro e fora da carência;
- Validação de resultados vazios quando não existem atrasos.

Os testes de integração permitiram identificar e corrigir um bug real no cálculo de multas, reforçando a importância da validação de cenários completos além da cobertura de código.
Além disso, a criação dos dados utilizados nos testes de integração é centralizada através de uma `TestDataFactory`, reduzindo duplicação e facilitando a manutenção da suíte de testes.

---

# Banco de Dados

O projeto possui um script SQL versionado localizado em:

```
database/
└── schema.sql
```

O script cria automaticamente:

- Banco de dados;
- Tabelas;
- Chaves primárias;
- Chaves estrangeiras;
- Constraints UNIQUE;
- Constraints CHECK;
- Regras de integridade.

Isso permite recriar todo o banco de dados apenas executando um único arquivo SQL.

---

# Decisões de Projeto

- O controle de estoque é realizado por título utilizando `quantidade_total` e `quantidade_disponivel`.
- As entidades se relacionam através de IDs, reduzindo o acoplamento entre objetos.
- As regras de negócio permanecem concentradas na camada Service.
- O banco de dados é responsável pela integridade referencial e pelas constraints.
- Violações de constraints do banco são traduzidas para exceções específicas da aplicação.
- O cálculo de multas foi implementado utilizando o Strategy Pattern, permitindo adicionar novas regras sem alterar a lógica da aplicação.
- A situação do empréstimo (Ativo, Atrasado ou Devolvido) é determinada dinamicamente, evitando persistência de estados derivados no banco de dados.
- O `IsbnValidator` valida ISBN-10 e ISBN-13, porém não realiza conversão entre os formatos para manter o escopo da primeira versão do projeto.
- O controle de concorrência no empréstimo é realizado através de uma atualização atômica do estoque, garantindo consistência mesmo com acessos simultâneos.
- Os relatórios que dependem de regras de negócio (como multas projetadas) são calculados na camada Service, mantendo a lógica fora do banco de dados.

---

# Estrutura do Projeto

```text
database
└── schema.sql

src
├── main
│   ├── java
│   │   └── br.com.biblioteca
│   │       ├── app
│   │       ├── config
│   │       ├── database
│   │       ├── enums
│   │       ├── exception
│   │       ├── model
│   │       ├── repository
│   │       ├── service
│   │       ├── util
│   │       └── validator
│   └── resources
test
└── java
    ├── service
    ├── strategy
    ├── manual
    └── util
        └── TestDataFactory
```

---

# Próximas etapas

- Implementar relatórios analíticos utilizando consultas SQL;
- Criar rankings e estatísticas da biblioteca;
- Construir a interface CLI;
- Expandir a cobertura dos testes automatizados.

---

# Autor

Desenvolvido por **Isaque Costa da Cunha** como projeto de portfólio para aprofundamento em desenvolvimento Back-end com Java.