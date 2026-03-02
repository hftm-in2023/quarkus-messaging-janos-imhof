package ch.hftm.messaging;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class SummaryResponseDeserializer extends JsonbDeserializer<SummaryResponse> {
    public SummaryResponseDeserializer() {
        super(SummaryResponse.class);
    }
}
