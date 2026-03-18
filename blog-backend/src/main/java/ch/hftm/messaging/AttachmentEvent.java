package ch.hftm.messaging;

public record AttachmentEvent(long blogId, String blogTitle, String fileName, String contentType, long fileSize) {}
