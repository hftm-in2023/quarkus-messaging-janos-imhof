package ch.hftm.messaging;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class ValidationResponseDeserializer extends JsonbDeserializer<ValidationResponse> {

    public ValidationResponseDeserializer() {
        super(ValidationResponse.class);
    }
}
