package ch.hftm.messaging;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class AttachmentEventDeserializer extends JsonbDeserializer<AttachmentEvent> {
    public AttachmentEventDeserializer() {
        super(AttachmentEvent.class);
    }
}
