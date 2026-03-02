# Quarkus Kafka Messaging

1. **blog-backend** (Port 8080) -- REST-API für Blog-Einträge und Kommentare
2. **text-processing-service** (Port 8081) -- Textvalidierung und Summary-Generierung via Kafka
3. **notification-service** (Port 8082) -- Empfängt Kommentar-Events und verwaltet Benachrichtigungen

## Services im Dev-Modus starten

```bash
# Terminal 1: Blog-Backend (Port 8080)
cd blog-backend
./mvnw quarkus:dev

# Terminal 2: Text-Processing-Service (Port 8081)
cd text-processing-service
./mvnw quarkus:dev

# Terminal 3: Notification-Service (Port 8082)
cd notification-service
./mvnw quarkus:dev
```

> **Hinweis:** Die Dev Services teilen sich automatisch den gleichen Kafka-Broker. Alle Topics werden automatisch erstellt.

## Nützliche Links

- Swagger UI (Blog-Backend): http://localhost:8080/q/swagger-ui
- Swagger UI (Notification-Service): http://localhost:8082/q/swagger-ui
- Kafka Dev UI: http://localhost:8080/q/dev-ui/io.quarkus.quarkus-kafka-client/topics
