CREATE DATABASE IF NOT EXISTS biblioteca
   CHARACTER SET utf8mb4
   COLLATE utf8mb4_unicode_ci;

USE biblioteca;

CREATE TABLE autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL ,
    nacionalidade VARCHAR(60) NULL
) ENGINE = InnoDB CHARACTER SET utf8mb4;

CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    nome VARCHAR(80) NOT NULL ,
    CONSTRAINT categoria_unique UNIQUE (nome)
) ENGINE=InnoDB CHARACTER SET utf8mb4;

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    nome VARCHAR(300) NOT NULL ,
    cpf CHAR(11) NOT NULL ,
    email VARCHAR(254) NOT NULL ,
    CONSTRAINT uq_usuario_cpf UNIQUE (cpf),
    CONSTRAINT uq_usuario_email UNIQUE (email)
)ENGINE=InnoDB CHARACTER SET utf8mb4;

CREATE TABLE livro (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    titulo VARCHAR(300) NOT NULL ,
    isbn VARCHAR(17) NOT NULL ,
    autor_id INT NOT NULL ,
    categoria_id INT NOT NULL ,
    quantidade_total INT NOT NULL ,
    quantidade_disponivel INT NOT NULL ,
    CONSTRAINT uq_livro_isbn UNIQUE (isbn),
    CONSTRAINT fk_livro_autor
                   FOREIGN KEY (autor_id) REFERENCES autor(id),
    CONSTRAINT fk_livro_categoria
                   FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    CONSTRAINT chk_livro_estoque
                   CHECK ( quantidade_disponivel BETWEEN 0 AND quantidade_total)
)ENGINE = InnoDB CHARACTER SET utf8mb4;

CREATE TABLE emprestimo (
    id INT AUTO_INCREMENT PRIMARY KEY ,
    usuario_id INT NOT NULL ,
    livro_id INT NOT NULL ,
    data_emprestimo DATE NOT NULL ,
    data_prevista_devolucao DATE NOT NULL ,
    data_devolucao DATE NULL ,
    status VARCHAR(20) NOT NULL ,
    CONSTRAINT fk_emprestimo_usuario
                        FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_emprestimo_livro
                        FOREIGN KEY (livro_id) REFERENCES livro(id),
    CONSTRAINT chk_emprestimo_status
                        CHECK ( status IN ('ATIVO', 'DEVOLVIDO')),
    CONSTRAINT chk_datas
                        CHECK (
                            data_devolucao IS NULL
                            OR data_prevista_devolucao > data_emprestimo )
)ENGINE = InnoDB CHARACTER SET utf8mb4;