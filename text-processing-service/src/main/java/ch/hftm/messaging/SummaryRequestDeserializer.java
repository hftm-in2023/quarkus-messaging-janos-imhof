package ch.hftm.messaging;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class SummaryRequestDeserializer extends JsonbDeserializer<SummaryRequest> {
    public SummaryRequestDeserializer() {
        super(SummaryRequest.class);
    }
}
