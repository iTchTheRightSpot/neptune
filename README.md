# Neptune

Challenge is a microservices-based order processing system designed
to manage and process orders efficiently using the following technologies:

- Frontend (Angular): Allows users to create and view orders.
- Backend (Spring Boot + gRPC): Handles order management and
communicates between microservices.
- Docker: Containers for all services.

## Requirement
1. Node.js.
2. (Maven, Java 23, and PostgreSQL) or Docker.

## Getting Started

- Clone
```bash
git clone https://github.com/iTchTheRightSpot/neptune.git && cd neptune
```

### Frontend
- Navigate to the `frontend` directory and install required dependencies:
```bash
cd frontend && npm i
```
- If using Docker, change `domain: 'http://localhost:8080/api/v1/'` in
`frontend/src/environments/environment.development.ts` to `domain: 'http://localhost:4000/api/v1/'`.
- Start the Angular dev server:
```bash
ng serve
```

For more detailed instructions on setting up the frontend, refer to the [README](./frontend/README.md)

## Project without Docker

#### Setup
- Create a PostgreSQL database named `neptune_db` and set the username & password to `neptune`.

### Run the Backend Services
- Gateway Service:
```bash
cd gateway && mvn clean spring-boot:run
``` 
- Order Service:
```bash
cd order && mvn clean spring-boot:run
``` 
- Inventory Service
```bash
cd inventory && mvn clean spring-boot:run
```

- You can interact with the API via the [API routes documentation](./API.md)

### Run tests
**NOTE:** Restart services if they are running when testing.

- Order Service Tests:
```bash
cd order && mvn clean test
``` 
- Inventory Service Tests
```bash
cd inventory && mvn clean test
```

### Run End-to-End tests
- Restart the services if running before running tests.
- Replace `${INVENTORY_PROFILE:default}` in `inventory/src/main/resources/application.yml` to `${INVENTORY_PROFILE:e2e}`.
- Run the command
```bash
cd e2e/ && javac main.java && java main.java 8080
```

## Project using Docker

### Initialize variables

- Create `.env` file and configure it:
```bash
touch .env
echo DB_NAME=neptune_db >> .env
echo DB_HOST_PORT=5432 >> .env
echo DB_URL='jdbc:postgresql://database:5432/neptune_db?sslmode=disable' >> .env
echo DB_USERNAME=neptune >> .env
echo DB_PASSWORD=neptune >> .env
echo GATEWAY_PORT=4000 >> .env
echo GATEWAY_ORDER_ROUTE=http://order:4002 >> .env
echo GATEWAY_INVENTORY_ROUTE=http://inventory:4003 >> .env
echo ORDER_PORT=4002 >> .env
echo ORDER_PROFILE=default >> .env
echo INVENTORY_CLIENT_ADDRESS=static://inventory:4005 >> .env
echo INVENTORY_PORT=4003 >> .env
echo INVENTORY_PROFILE=default >> .env
echo INVENTORY_GRPC_SERVER_PORT=4005 >> .env
```

#### Run the Backend Services Using Docker

- Start all backend services with Docker Compose:
```bash
docker compose up -d
``` 
- You can interact with the API via the [API routes documentation](./API.md)

#### Run End-to-End Tests

- Note: If the containers are running, restart them using the command:
```bash
docker compose restart
```
- Replace `INVENTORY_PROFILE=default` in `.env` to `INVENTORY_PROFILE=e2e`.
- Run the command:
```bash
cd e2e/ && javac main.java && java main.java 4000
```

### Dev docs
1. [sample spring project GitHub.](https://github.com/spring-projects/spring-petclinic/blob/main/src/main/resources/application-postgres.properties)
2. [grpc springboot.](https://github.com/grpc-ecosystem/grpc-spring)