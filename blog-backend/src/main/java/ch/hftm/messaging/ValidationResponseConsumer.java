package ch.hftm.messaging;

import ch.hftm.control.BlogRepository;
import ch.hftm.control.CommentRepository;
import ch.hftm.entity.Blog;
import ch.hftm.entity.Comment;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ValidationResponseConsumer {

    @Inject
    BlogRepository blogRepository;

    @Inject
    CommentRepository commentRepository;

    @Inject
    @Channel("summary-request")
    Emitter<SummaryRequest> summaryRequestEmitter;

    @Incoming("validation-response")
    @Transactional
    public void handleValidationResponse(ValidationResponse response) {
        String status = response.valid() ? "APPROVED" : "REJECTED";

        switch (response.sourceType()) {
            case "BLOG" -> {
                Blog blog = blogRepository.findById(response.sourceId());
                if (blog != null) {
                    blog.setValidationStatus(status);
                    Log.info("Updated blog id=" + response.sourceId() + " validationStatus=" + status);

                    if (response.valid()) {
                        summaryRequestEmitter.send(new SummaryRequest(blog.getId(), blog.getContent()));
                        Log.info("Summary request sent for blog id=" + blog.getId());
                    }
                } else {
                    Log.warn("Blog not found for sourceId=" + response.sourceId());
                }
            }
            case "COMMENT" -> {
                Comment comment = commentRepository.findById(response.sourceId());
                if (comment != null) {
                    comment.setValidationStatus(status);
                    Log.info("Updated comment id=" + response.sourceId() + " validationStatus=" + status);
                } else {
                    Log.warn("Comment not found for sourceId=" + response.sourceId());
                }
            }
            default -> Log.warn("Unknown sourceType: " + response.sourceType());
        }
    }
}
