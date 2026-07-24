# Sistema de Biblioteca (Em desenvolvimento)

Sistema de gerenciamento de biblioteca desenvolvido em **Java**, utilizando **JDBC**, **MySQL**, **Docker** e arquitetura em camadas.

O projeto está sendo desenvolvido como peça de portfólio com o objetivo de aplicar conceitos de desenvolvimento back-end utilizados em aplicações reais, priorizando organização do código, separação de responsabilidades, regras de negócio e boas práticas.

---

# Objetivo

Desenvolver uma aplicação capaz de gerenciar o funcionamento de uma biblioteca, permitindo o controle de usuários, livros e empréstimos através de uma arquitetura organizada e escalável.

Durante o desenvolvimento são praticados conceitos como:

- Programação Orientada a Objetos
- JDBC
- SQL
- MySQL
- Docker
- Maven
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

## Livros

- Cadastro
- Atualização
- Consulta por ID
- Consulta por ISBN
- Listagem completa
- Controle de estoque
- Exclusão

## Empréstimos (em desenvolvimento)

- Registrar empréstimos
- Registrar devoluções
- Atualização automática do estoque
- Controle de multas
- Histórico de empréstimos

## Relatórios (planejado)

- Livros mais emprestados
- Usuários com empréstimos ativos
- Usuários inadimplentes
- Ranking de devedores
- Total arrecadado em multas
- Estatísticas gerais

---

# Tecnologias

- Java 21
- Maven
- JDBC
- MySQL 8.4
- Docker
- Lombok
- Log4j2
- JUnit (planejado)

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
| Validator | Validação e normalização dos dados de entrada |
| Exception | Exceções específicas da aplicação |
| Util | Classes utilitárias (ConexaoFactory, etc.) |

---

# Fluxo da aplicação

Toda operação da aplicação segue o mesmo fluxo.

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

### Entrada

Recebe um objeto `Usuario`.

### Processamento

1. O CPF é validado e normalizado.
2. O e-mail é validado e normalizado.
3. É realizada consulta para verificar duplicidade de CPF.
4. É realizada consulta para verificar duplicidade de e-mail.
5. O usuário é persistido utilizando o `UsuarioRepository`.
6. Caso ocorra violação de constraint UNIQUE, ela é traduzida para uma exceção de domínio.

### Saída

Retorna o usuário cadastrado.

### Possíveis exceções

- CpfInvalidoException
- EmailInvalidoException
- CpfJaCadastradoException
- EmailJaCadastradoException
- PersistenciaException

---

## Atualização de usuário

### Entrada

Recebe um objeto `Usuario`.

### Processamento

1. Verifica se o usuário existe.
2. Valida CPF.
3. Valida e-mail.
4. Verifica duplicidade de CPF.
5. Verifica duplicidade de e-mail.
6. Atualiza o registro.

### Saída

Retorna o usuário atualizado.

---

## Exclusão de usuário

### Entrada

Recebe o ID do usuário.

### Processamento

1. Verifica se o usuário existe.
2. Solicita a exclusão ao Repository.
3. Caso existam empréstimos vinculados, uma exceção específica é lançada.

### Saída

Sem retorno.

---

## Cadastro de livro

### Entrada

Recebe um objeto `Livro`.

### Processamento

1. O ISBN é validado (ISBN-10 ou ISBN-13).
2. A quantidade total é validada.
3. A quantidade disponível é inicializada com a quantidade total.
4. Verifica duplicidade de ISBN.
5. Persiste o livro.
6. Traduz violações de UNIQUE para exceções específicas.

### Saída

Retorna o livro cadastrado.

---

## Atualização de livro

### Entrada

Recebe um objeto `Livro`.

### Processamento

1. Verifica existência do livro.
2. Valida ISBN.
3. Valida quantidade.
4. Verifica duplicidade de ISBN.
5. Atualiza o registro.

### Saída

Retorna o livro atualizado.

---

## Exclusão de livro

### Entrada

Recebe o ID do livro.

### Processamento

1. Verifica se o livro existe.
2. Solicita a exclusão ao Repository.
3. Caso existam empréstimos vinculados, lança uma exceção específica.

### Saída

Sem retorno.

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

---

# O que já foi implementado

## Banco de Dados

- Modelagem relacional completa.
- Constraints de integridade.
- Chaves primárias.
- Chaves estrangeiras.
- Constraints UNIQUE.
- Constraints CHECK.

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

## Validator

- CpfValidator
- EmailValidator
- IsbnValidator

## Exception

Hierarquia inicial de exceções de domínio e persistência.

---

# Decisões de Projeto

- O controle de estoque é realizado por título utilizando `quantidade_total` e `quantidade_disponivel`.
- As entidades se relacionam através de IDs, reduzindo o acoplamento entre objetos.
- As regras de negócio permanecem na camada Service.
- O banco de dados é responsável pela integridade referencial e pelas constraints.
- Violações de constraints do banco são traduzidas para exceções específicas da aplicação.
- O cálculo de multas utilizará o Strategy Pattern para permitir alteração da regra de cálculo sem impacto no restante da aplicação.
- O `IsbnValidator` valida ISBN-10 e ISBN-13, porém não realiza conversão entre os formatos, decisão adotada para manter o escopo da primeira versão do projeto.

---

# Estrutura do Projeto

```
src
└── main
    └── java
        └── br.com.biblioteca
            ├── model
            ├── repository
            ├── service
            ├── validator
            ├── exception
            ├── util
            └── enums
```

---

# Próximas etapas

- Implementar `AutorRepository`.
- Implementar `CategoriaRepository`.
- Implementar `EmprestimoRepository`.
- Desenvolver os respectivos Services.
- Implementar gerenciamento de empréstimos.
- Implementar transações JDBC.
- Desenvolver cálculo de multas utilizando Strategy Pattern.
- Criar interface CLI.
- Desenvolver testes automatizados com JUnit.