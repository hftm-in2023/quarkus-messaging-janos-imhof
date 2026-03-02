package ch.hftm.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Notification extends PanacheEntity {

    public long blogId;
    public String blogTitle;
    public String commentAuthor;
    public String message;
    public boolean read;
    public LocalDateTime createdAt;

    public Notification() {}

    public Notification(long blogId, String blogTitle, String commentAuthor, String message) {
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.commentAuthor = commentAuthor;
        this.message = message;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }
}
