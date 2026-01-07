# System Design

## 1. Scaling GET /tasks to 10,000 requests per minute

- Add indexes on `status`, `priority`, `created_at` columns
- Cache frequent queries
- Use read replicas to handle more read traffic
- Run multiple instances to load balancer

## 2. Event Emission for Microservices - Data Consistency

Use the transactional outbox pattern:

1. When task status changes to `done`, insert an event into an `outbox` table in the same database transaction
2. A background worker reads the outbox and publishes it
3. Delete from outbox after successful publish

This avoids the problem where database commits but message publish fails

## 3. Rate Limiting Implementation

At the API Gateway level

Reasons:

- Stops bad traffic before it hits the app
- Easy to configure per endpoint
- Can limit by IP or API key
