package ch.hftm.messaging;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class ValidationRequestDeserializer extends JsonbDeserializer<ValidationRequest> {
    public ValidationRequestDeserializer() {
        super(ValidationRequest.class);
    }
}
