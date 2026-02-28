package ch.hftm.messaging;

import ch.hftm.control.TextValidationService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class ValidationProcessor {

    @Inject
    TextValidationService validationService;

    @Incoming("validation-request")
    @Outgoing("validation-response")
    public Multi<ValidationResponse> validate(Multi<ValidationRequest> requests) {
        return requests.emitOn(Infrastructure.getDefaultWorkerPool()).map(request -> {
            boolean valid = validationService.validate(request.text());
            Log.info("Validated sourceId=" + request.sourceId()
                    + " sourceType=" + request.sourceType()
                    + " -> " + (valid ? "APPROVED" : "REJECTED"));

            validationService.saveRecord(
                    request.sourceId(), request.sourceType(), request.text(), valid);

            return new ValidationResponse(request.sourceId(), request.sourceType(), valid);
        });
    }
}
