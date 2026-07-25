# Sistema de Biblioteca (Em desenvolvimento)

Sistema de gerenciamento de biblioteca desenvolvido em **Java**, utilizando **JDBC**, **MySQL**, **Docker** e arquitetura em camadas.

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
- SQL (DDL)
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

## Validator

- CpfValidator
- EmailValidator
- IsbnValidator
- NomeValidator

## Exception

Hierarquia de exceções específicas para validação, persistência e regras de negócio.

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
- O cálculo de multas será implementado utilizando o Strategy Pattern.
- O `IsbnValidator` valida ISBN-10 e ISBN-13, porém não realiza conversão entre os formatos para manter o escopo da primeira versão do projeto.

---

# Estrutura do Projeto

```
database
└── schema.sql

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

- Implementar `EmprestimoRepository`.
- Implementar `EmprestimoService`.
- Desenvolver o gerenciamento de empréstimos.
- Implementar transações JDBC.
- Desenvolver o cálculo de multas utilizando Strategy Pattern.
- Criar interface CLI.
- Desenvolver testes automatizados com JUnit.

---

# Autor

Desenvolvido por **Isaque Costa da Cunha** como projeto de portfólio para aprofundamento em desenvolvimento Back-end com Java.