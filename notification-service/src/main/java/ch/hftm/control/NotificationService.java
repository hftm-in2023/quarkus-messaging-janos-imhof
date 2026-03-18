package ch.hftm.control;

import java.util.List;

import ch.hftm.entity.Notification;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class NotificationService {

    public List<Notification> getAll() {
        return Notification.listAll();
    }

    @Transactional
    public void createNotification(long blogId, String blogTitle, String commentAuthor, String commentText) {
        String message = commentAuthor + " hat einen Kommentar auf \"" + blogTitle + "\" hinterlassen: " + commentText;
        Notification notification = new Notification(blogId, blogTitle, commentAuthor, message);
        notification.persist();
        Log.info("Created notification for blog id=" + blogId + " by " + commentAuthor);
    }

    @Transactional
    public void createAttachmentNotification(long blogId, String blogTitle, String fileName) {
        String message = "New attachment \"" + fileName + "\" added to blog \"" + blogTitle + "\"";
        Notification notification = new Notification(blogId, blogTitle, null, message);
        notification.persist();
        Log.info("Created attachment notification for blog id=" + blogId + ", file=" + fileName);
    }

    @Transactional
    public Notification markAsRead(long id) {
        Notification notification = Notification.findById(id);
        if (notification != null) {
            notification.read = true;
            Log.info("Marked notification id=" + id + " as read");
        }
        return notification;
    }
}
