package ch.hftm.messaging;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class CommentEventDeserializer extends JsonbDeserializer<CommentEvent> {
    public CommentEventDeserializer() {
        super(CommentEvent.class);
    }
}
