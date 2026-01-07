# Tasks API

Simple REST API for managing tasks.

## Stack

- Java 17
- Spring Boot
- PostgreSQL

## Setup

Start database:

```bash
docker-compose up -d
```

Run app:

```bash
mvn spring-boot:run
```

## Tests

```bash
mvn test
```

## Endpoints

### Create Task

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "create task", "priority": 2}'
```

With idempotency key:

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: unique-key-123" \
  -d '{"title": "Buy milk", "priority": 2}'
```

### List Tasks

```bash
curl http://localhost:8080/tasks
curl "http://localhost:8080/tasks?status=pending&priority=1&limit=10"
```

### Update Task

```bash
curl -X PATCH http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"status": "done"}'
```

### Delete Task

```bash
curl -X DELETE http://localhost:8080/tasks/1
```

## Swagger

http://localhost:8080/swagger-ui.html
