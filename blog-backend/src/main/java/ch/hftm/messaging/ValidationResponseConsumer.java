package ch.hftm.messaging;

import ch.hftm.entity.Blog;
import ch.hftm.control.BlogRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ValidationResponseConsumer {

    @Inject
    BlogRepository blogRepository;

    @Incoming("validation-response")
    @Transactional
    public void handleValidationResponse(ValidationResponse response) {
        Blog blog = blogRepository.findById(response.sourceId());
        if (blog != null) {
            blog.setValidationStatus(response.valid() ? "APPROVED" : "REJECTED");
            Log.info("Updated blog id=" + response.sourceId()
                    + " validationStatus=" + blog.getValidationStatus());
        } else {
            Log.warn("Blog not found for sourceId=" + response.sourceId());
        }
    }
}
