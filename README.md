# Library
Projeto blibioteca digital

## Intuito do Projeto

- O intuito do projeto é criar um sistema que sirva como gerenciador de livros alugados, utilizando TDD.

## Tecnologias utilizadas

- Java
- Spring Boot
- JPA
- H2
- Maven
- Postman
- Git
- JUnite

## Endpoints

### Livros
- GET - /books - Retorna todos os livros cadastrados
- GET - /books/{id} - Retorna um livro específico
- POST - /books - Cadastra um novo livro
- PUT - /books/{id} - Atualiza um livro específico
- DELETE - /books/{id} - Deleta um livro específico

### Emprestimo
- GET - /loans - Retorna todos os empréstimos cadastrados
- GET - /loans/{id} - Retorna um empréstimo específico
- POST - /loans - Cadastra um novo empréstimo
- PATCH - /loans/{id} - Atualiza um empréstimo específico (Retorno do livro)
- DELETE - /loans/{id} - Deleta um empréstimo específico
