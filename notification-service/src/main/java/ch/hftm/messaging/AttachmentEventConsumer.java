package ch.hftm.messaging;

import ch.hftm.control.NotificationService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class AttachmentEventConsumer {

    @Inject
    NotificationService notificationService;

    @Incoming("attachment-events")
    public void handleAttachmentEvent(AttachmentEvent event) {
        Log.info("Received attachment event for blog id=" + event.blogId() + ", file=" + event.fileName());
        notificationService.createAttachmentNotification(event.blogId(), event.blogTitle(), event.fileName());
    }
}
