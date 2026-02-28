# Quarkus Kafka Messaging

1. **blog-backend** (Port 8080) -- REST-API für Blog-Einträge und Kommentare
2. **validation-service** (Port 8081) -- Textvalidierung via Kafka

## Services im Dev-Modus starten

```bash
# Terminal 1: Blog-Backend (Port 8080)
cd blog-backend
./mvnw quarkus:dev

# Terminal 2: Validation-Service (Port 8081)
cd validation-service
./mvnw quarkus:dev
```

> **Hinweis:** Die Dev Services teilen sich automatisch den gleichen Kafka-Broker. Alle Topics werden automatisch erstellt.

## Nützliche Links

- Swagger UI: http://localhost:8080/q/swagger-ui
- Kafka Dev UI: http://localhost:8080/q/dev-ui/io.quarkus.quarkus-kafka-client/topics
