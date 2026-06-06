# Wallet API

API REST em **Spring Boot 3** para gestão de finanças pessoais (transações, categorias e metas), com autenticação multiusuário via **JWT**, persistência em **PostgreSQL** (Flyway) e documentação via **Swagger / OpenAPI**.

Projeto educacional com foco em arquitetura em camadas, regras de domínio testáveis e contratos REST padronizados.

---

## Funcionalidades

- **Autenticação**: cadastro e login com JWT (`HS256`, expiração 1h)
- **Usuários**: perfil próprio (`/users/me`) com saldo materializado
- **Categorias**: CRUD por usuário, unicidade por título
- **Transações**: CRUD com atualização atômica do saldo (INPUT/OUTPUT), paginação e filtros
- **Metas**: CRUD + endpoint de contribuição que marca conclusão automática
- **Swagger UI** em `/swagger-ui.html` com autorização Bearer
- **Erros** padronizados em RFC 7807 (`application/problem+json`)

---

## Stack

- Java 21, Spring Boot 3.3.x, Gradle
- PostgreSQL 16, Flyway (migrations versionadas em SQL)
- Spring Data JPA, Spring Security, Spring Validation
- jjwt 0.12, springdoc-openapi 2.6

---

## Como rodar

### Pré-requisitos

- JDK 21
- Docker + Docker Compose

### Passos

```bash
# 1. subir o Postgres
docker compose up -d

# 2. rodar a API
./gradlew bootRun
```

A API sobe em `http://localhost:8080`. Swagger UI em `http://localhost:8080/swagger-ui.html`.

### Variáveis de ambiente (opcionais)

| Variável | Default | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host do Postgres |
| `DB_PORT` | `5432` | Porta do Postgres |
| `DB_NAME` | `wallet` | Nome do banco |
| `DB_USER` | `wallet` | Usuário |
| `DB_PASSWORD` | `wallet` | Senha |
| `JWT_SECRET` | dev secret | **Trocar em produção** (mínimo 32 bytes) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Origens permitidas |

---

## Fluxo rápido via Swagger

1. Abra `http://localhost:8080/swagger-ui.html`
2. `POST /api/v1/auth/register` — crie um usuário
3. `POST /api/v1/auth/login` — pegue o `accessToken`
4. Clique em **Authorize** (canto superior direito) e cole `Bearer <token>`
5. `POST /api/v1/categories` — crie uma categoria
6. `POST /api/v1/transactions` — registre uma transação (`INPUT` ou `OUTPUT`)
7. `GET /api/v1/users/me` — confira o saldo atualizado

---

## Fluxo rápido via curl

```bash
# Registrar
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Breno","email":"breno@example.com","password":"senha-forte-123"}'

# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"breno@example.com","password":"senha-forte-123"}' \
  | jq -r .accessToken)

# Criar categoria
CAT=$(curl -s -X POST http://localhost:8080/api/v1/categories \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"title":"Alimentação","description":"Gastos com comida"}' \
  | jq -r .id)

# Criar transação INPUT
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "{\"type\":\"INPUT\",\"amount\":1000.00,\"categoryId\":\"$CAT\"}"

# Conferir saldo
curl http://localhost:8080/api/v1/users/me -H "Authorization: Bearer $TOKEN"
```

---

## Arquitetura

```
src/main/java/org/example/
├── WalletApplication.java                # entrypoint Spring Boot
├── domain/
│   ├── entities/                         # JPA + regras de domínio (UserEntity, CategoryEntity, TransactionEntity, GoalEntity)
│   └── enums/                            # TransactionType, Role
├── application/
│   ├── services/                         # AuthService, UserService, CategoryService, TransactionService, GoalService
│   ├── dtos/                             # contrato interno entre controllers e services
│   └── exceptions/                       # ResourceNotFoundException, ConflictException
├── infrastructure/
│   ├── persistence/                      # Spring Data JPA repositories
│   ├── security/                         # SecurityConfig, JwtService, JwtAuthenticationFilter, ...
│   └── config/                           # OpenApiConfig, CorsConfig
└── presentation/rest/
    ├── controllers/                      # AuthController, UserController, CategoryController, TransactionController, GoalController
    ├── dtos/                             # records de request/response REST
    ├── mappers/                          # entity → response
    └── exception/GlobalExceptionHandler.java

## Endpoints principais

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/api/v1/auth/register` | público | Cadastra usuário |
| POST | `/api/v1/auth/login` | público | Retorna token JWT |
| GET | `/api/v1/users/me` | bearer | Perfil + saldo |
| PATCH | `/api/v1/users/me` | bearer | Atualiza nome |
| POST/GET/PATCH/DELETE | `/api/v1/categories[/{id}]` | bearer | CRUD de categorias |
| POST/GET/PATCH/DELETE | `/api/v1/transactions[/{id}]` | bearer | CRUD de transações |
| POST/GET/PATCH/DELETE | `/api/v1/goals[/{id}]` | bearer | CRUD de metas |
| POST | `/api/v1/goals/{id}/contribute` | bearer | Aporta valor numa meta |

---

## Migrations

Versionadas em `src/main/resources/db/migration/`. Flyway aplica em ordem ao subir o app. O Hibernate roda em `ddl-auto: validate` — toda mudança de schema exige nova migration.

---

## Contribuidores

- Nicolas Castro Ribeiro
- João Victor Rodrigues Barreto
- Breno Sampaio Gonçalves
