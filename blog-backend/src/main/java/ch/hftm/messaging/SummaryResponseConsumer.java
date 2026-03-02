package ch.hftm.messaging;

import ch.hftm.control.BlogRepository;
import ch.hftm.entity.Blog;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class SummaryResponseConsumer {

    @Inject
    BlogRepository blogRepository;

    @Incoming("summary-response")
    @Transactional
    public void handleSummaryResponse(SummaryResponse response) {
        Blog blog = blogRepository.findById(response.sourceId());
        if (blog != null) {
            blog.setSummary(response.summary());
            Log.info("Updated blog id=" + response.sourceId() + " with summary");
        } else {
            Log.warn("Blog not found for sourceId=" + response.sourceId());
        }
    }
}
