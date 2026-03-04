# Quarkus Kafka Messaging

Drei Quarkus-Microservices kommunizieren asynchron über Apache Kafka.

## Schnellstart mit Docker Compose

```bash
docker compose up
```
Das startet automatisch:
1. **Redpanda** (Kafka-Broker)
2. **Topic-Erstellung** (alle 5 Topics)
3. **Blog-Backend** (Port 8080)
4. **Text-Processing-Service** (Port 8081)
5. **Notification-Service** (Port 8082)

Warten bis alle Container gestartet sind (~10 Sekunden).

## Swagger UI

- Blog-Backend: http://localhost:8080/q/swagger-ui
- Notification-Service: http://localhost:8082/q/swagger-ui

## Ports

| Service | Port |
|---|---|
| Blog-Backend | 8080 |
| Text-Processing-Service | 8081 |
| Notification-Service | 8082 |
| Redpanda (Kafka) | 9092 |

## Kafka-Topics

| Topic | Producer | Consumer | Pattern |
|---|---|---|---|
| `text-validation-request` | Blog-Backend | Text-Processing | Request/Response |
| `text-validation-response` | Text-Processing | Blog-Backend | Request/Response |
| `summary-request` | Blog-Backend | Text-Processing | Request/Response |
| `summary-response` | Text-Processing | Blog-Backend | Request/Response |
| `comment-events` | Blog-Backend | Notification | Fire-and-Forget |

## API-Endpoints

### Blog-Backend (Port 8080)

| Methode | Endpoint | Beschreibung |
|---|---|---|
| `GET` | `/blogs` | Alle Blogs auflisten |
| `GET` | `/blogs?status=APPROVED` | Blogs nach Status filtern |
| `GET` | `/blogs/{id}` | Einzelner Blog |
| `POST` | `/blogs` | Blog erstellen |
| `DELETE` | `/blogs/{id}` | Blog löschen |
| `GET` | `/blogs/{id}/comments` | Kommentare eines Blogs |
| `POST` | `/blogs/{id}/comments` | Kommentar erstellen |

### Notification-Service (Port 8082)

| Methode | Endpoint | Beschreibung |
|---|---|---|
| `GET` | `/notifications` | Alle Benachrichtigungen |
| `PUT` | `/notifications/{id}/read` | Als gelesen markieren |

## Beispiel-Requests

### Blog erstellen

```bash
curl -s -X POST http://localhost:8080/blogs \
  -H "Content-Type: application/json" \
  -d '{"title": "Mein Blog", "content": "Das ist ein Blogpost mit genug Inhalt zum Testen."}' | jq
```

Nach wenigen Sekunden ist der Blog validiert und hat eine Summary:

```bash
curl -s http://localhost:8080/blogs/1 | jq
```

### Kommentar erstellen

```bash
curl -s -X POST http://localhost:8080/blogs/1/comments \
  -H "Content-Type: application/json" \
  -d '{"author": "Max", "content": "Toller Beitrag"}' | jq
```

### Notification prüfen

```bash
curl -s http://localhost:8082/notifications | jq
```

### Notification als gelesen markieren

```bash
curl -s -X PUT http://localhost:8082/notifications/1/read | jq
```

## Erwarteter Flow

1. **Blog erstellen** -- Status = `PENDING`, wird in DB gespeichert
2. **Validierung** -- Text-Processing-Service prüft den Text (Blocklist, Mindestlänge)
3. **Status-Update** -- Blog wird `APPROVED` oder `REJECTED`
4. **Summary** -- Bei `APPROVED`: Zusammenfassung wird generiert und im Blog gespeichert
5. **Kommentar** -- Beim Erstellen wird ein Kommentar-Event an Kafka gesendet
6. **Notification** -- Notification-Service erstellt eine Benachrichtigung

## Dev-Modus (Entwicklung)

Für lokal ohne Docker:

```bash
# Terminal 1
cd blog-backend && ./mvnw quarkus:dev

# Terminal 2
cd text-processing-service && ./mvnw quarkus:dev

# Terminal 3
cd notification-service && ./mvnw quarkus:dev
```

Dev Services starten automatisch Kafka und MySQL.