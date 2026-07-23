# Sistema de Biblioteca (Em andamento)

Sistema de gerenciamento de biblioteca desenvolvido com Java, JDBC, MySQL e Docker.

## Objetivo

Sistema de gerencionamento de biblioteca, back-end Java, construído como peça de portfólio. O foco é praticar e demonstrar conhecimentos adquiridos durante minha jornada de estudos, destacando:

- Programação Orientada a Objetos(POO)
- JDBC
- SQL
- Transações
- Padrões de Projeto
- Streams
- Boas práticas de arquitetura

## O que pode ser feito?

- Cadastrar e consultar autores, categorias, livros(com controle de estoque por título, e usuários.
- Registrar empréstimo de livro
- Registrar devolução, com reposição de estoque na mesma transação.
- Calcular multa por atraso
- Gerar relatórios: livros mais emprestados, usuários com empréstimos ativos, estatísticas gerais, e usuários inadimplentes/total de multas/ranking de devedores.
- Interface via CLI para interação com o sistema.
- Testes automatizados com JUnit cobrindo regras de negócio críticas, indo além de testes manuais.

## O que já foi feito?

- Modelagem do banco de dados.
- Criação das entidades (`Autor`, `Categoria`, `Livro`, `Usuario` e `Emprestimo`).
- Implementação do enum `StatusEmprestimo`.
- Configuração do projeto com Maven, Java 21 e Docker.
- Implementação da `ConexaoFactory` utilizando o padrão Singleton.
- Implementação do `UsuarioRepository` com operações de CRUD e consultas por CPF, e-mail e nome.
- Implementação do `LivroRepository` com operações de CRUD e consulta por ISBN.
- Criação da hierarquia inicial de exceções customizadas para validação e persistência.

## Decisões de Projeto

- O controle de estoque é feito por título, utilizando:
    - quantidade_total
    - quantidade_disponivel

- As entidades utilizam IDs para representar relacionamentos, evitando acoplamento entre objetos no modelo.

- Regras de negócio complexas ficam na aplicação Java, enquanto o banco fica responsável pela integridade dos dados.

- O cálculo de multas utiliza Strategy Pattern, permitindo alteração da regra de cálculo sem modificar o restante do sistema.

## Próximo passo

- Implementação dos repositórios de Autor, Categoria e Empréstimo.
- Desenvolvimento da camada de Service com as regras de negócio e tratamento das exceções customizadas.

## Banco de Dados:

O projeto utiliza MySQL via Docker.

Tabelas:

### Autor

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Identificador único do autor |
| nome | VARCHAR(255) | NOT NULL | Nome do autor |
| nacionalidade | VARCHAR(60) | NULL | Nacionalidade do autor |

---

### Categoria

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Identificador único da categoria |
| nome | VARCHAR(255) | NOT NULL, UNIQUE | Nome da categoria |

---

### Usuário

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Identificador único do usuário |
| nome | VARCHAR(255) | NOT NULL | Nome completo do usuário |
| cpf | VARCHAR(14) | NOT NULL, UNIQUE | CPF do usuário |
| email | VARCHAR(255) | NOT NULL, UNIQUE | E-mail do usuário |

---

### Livro

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Identificador único do livro |
| titulo | VARCHAR(300) | NOT NULL | Título do livro |
| isbn | VARCHAR(20) | NOT NULL, UNIQUE | Código ISBN do livro |
| autor_id | INT | NOT NULL, FOREIGN KEY | Referência ao autor |
| categoria_id | INT | NOT NULL, FOREIGN KEY | Referência à categoria |
| quantidade_total | INT | NOT NULL | Quantidade total de exemplares |
| quantidade_disponivel | INT | NOT NULL | Quantidade disponível para empréstimo |

**Constraints adicionais:**

- `fk_livro_autor` → garante que o autor informado exista.
- `fk_livro_categoria` → garante que a categoria informada exista.
- `chk_livro_estoque` → impede estoque negativo ou disponibilidade maior que a quantidade total.

Regra:
quantidade_disponivel >= 0
quantidade_disponivel <= quantidade_total


### Empréstimo

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | Identificador único do empréstimo |
| usuario_id | INT | NOT NULL, FOREIGN KEY | Usuário responsável pelo empréstimo |
| livro_id | INT | NOT NULL, FOREIGN KEY | Livro emprestado |
| data_emprestimo | DATE | NOT NULL | Data em que o empréstimo foi realizado |
| data_prevista_devolucao | DATE | NOT NULL | Data limite para devolução |
| data_devolucao | DATE | NULL | Data real de devolução |
| status | VARCHAR(20) | NOT NULL, CHECK | Estado atual do empréstimo |

Valores permitidos para `status`: ATIVO, DEVOLVIDO


**Constraints adicionais:**

- `fk_emprestimo_usuario` → garante que o usuário exista.
- `fk_emprestimo_livro` → garante que o livro exista.
- `chk_emprestimo_status` → impede status inválidos.
- `chk_datas` → garante que a data prevista seja posterior à data de empréstimo.

---

## Integridade do Banco

O banco utiliza constraints para garantir consistência:

- **PRIMARY KEY:** garante identificadores únicos.
- **FOREIGN KEY:** mantém integridade entre tabelas relacionadas.
- **UNIQUE:** impede dados duplicados em campos identificadores.
- **NOT NULL:** impede campos obrigatórios vazios.
- **CHECK:** valida regras de negócio simples diretamente no banco.

## Arquitetura

O projeto está sendo desenvolvido utilizando arquitetura em camadas, separando as responsabilidades entre:

- **Model:** entidades da aplicação.
- **Repository:** acesso aos dados utilizando JDBC.
- **Service:** regras de negócio, validações e gerenciamento de transações.
- **Util:** classes utilitárias, como a `ConexaoFactory`.
- **Exception:** exceções customizadas para representar erros específicos da aplicação.
## Tecnologias

- Java 21
- Maven
- Lombok
- MySQL 8.4
- Docker
- JDBC

