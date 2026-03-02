package ch.hftm.messaging;

import ch.hftm.control.NotificationService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class CommentEventConsumer {

    @Inject
    NotificationService notificationService;

    @Incoming("comment-events")
    public void handleCommentEvent(CommentEvent event) {
        Log.info("Received comment event for blog id=" + event.blogId() + " by " + event.commentAuthor());
        notificationService.createNotification(event.blogId(), event.blogTitle(), event.commentAuthor(), event.commentText());
    }
}
