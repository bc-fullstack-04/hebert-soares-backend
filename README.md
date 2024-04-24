# Bootcamp Sysmap
Backend Project - Hebert Ferreira Soares

## Portas dos Aplicativos

- `app-user`: porta 8081
- `app-integration`: porta 8082
- `swagger-UI app-user`<a href="http://localhost:8081/api/swagger-ui/index.html#/" target="_blank">Clique aqui para acessar o Swagger UI do app-user</a>
-  `swagger-UI app-integration` <a href="http://localhost:8082/api/swagger-ui/index.html#/" target="_blank">Clique aqui para acessar o Swagger UI do app-integration</a>

## Instalação

Certifique-se de ter o Docker e o Docker Compose instalados antes de prosseguir.

1. Clone o repositório:
 ``` git clone https://github.com/bc-fullstack-04/hebert-soares-backend.git```
2. Navegue até o diretório do projeto:
```cd hebert-soares-backend```
3. Execute o comando Docker Compose para build:
```docker-compose build```
4. Inicie os contêineres:
```docker-compose up -d```
   
### Isso iniciará os contêineres em segundo plano. Você pode acessar os aplicativos através das portas especificadas.
