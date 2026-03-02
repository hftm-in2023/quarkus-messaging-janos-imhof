package ch.hftm.messaging;

import ch.hftm.control.TextSummaryService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class SummaryProcessor {

    @Inject
    TextSummaryService summaryService;

    @Incoming("summary-request")
    @Outgoing("summary-response")
    public Multi<SummaryResponse> summarize(Multi<SummaryRequest> requests) {
        return requests.emitOn(Infrastructure.getDefaultWorkerPool()).map(request -> {
            String summary = summaryService.summarize(request.text());
            Log.info("Generated summary for sourceId=" + request.sourceId()
                    + " (" + summary.length() + " chars)");

            summaryService.saveRecord(request.sourceId(), request.text(), summary);

            return new SummaryResponse(request.sourceId(), summary);
        });
    }
}
