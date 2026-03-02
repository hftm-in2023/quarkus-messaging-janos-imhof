package ch.hftm.messaging;

public record CommentEvent(long blogId, String blogTitle, String commentAuthor, String commentText) {}
