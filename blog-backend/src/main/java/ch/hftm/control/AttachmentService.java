package ch.hftm.control;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import ch.hftm.entity.Attachment;
import ch.hftm.entity.Blog;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AttachmentService {

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    public static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "application/pdf");

    @Inject
    AttachmentRepository attachmentRepository;

    @Inject
    BlogRepository blogRepository;

    @Inject
    MinioService minioService;

    @Transactional
    public Attachment uploadAttachment(Long blogId, String fileName, String contentType, long fileSize,
            InputStream fileData) {
        Blog blog = blogRepository.findById(blogId);
        if (blog == null) {
            throw new IllegalArgumentException("Blog with ID " + blogId + " not found.");
        }

        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File too large. Maximum size: 5 MB, actual size: " + (fileSize / 1024) + " KB.");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Content type '" + contentType + "' not allowed. Allowed: " + ALLOWED_CONTENT_TYPES);
        }

        String objectKey = "blogs/" + blogId + "/" + UUID.randomUUID() + "_" + fileName;

        minioService.uploadFile(objectKey, fileData, fileSize, contentType);
        Log.info("File uploaded to MinIO: " + objectKey);

        Attachment attachment = new Attachment(blog, fileName, contentType, fileSize, objectKey);
        attachmentRepository.persist(attachment);
        Log.info("Attachment saved: " + attachment.getId());

        return attachment;
    }

    public List<Attachment> getAttachments(Long blogId) {
        Blog blog = blogRepository.findById(blogId);
        if (blog == null) {
            throw new IllegalArgumentException("Blog with ID " + blogId + " not found.");
        }
        return attachmentRepository.findByBlogId(blogId);
    }

    public Attachment getAttachment(Long blogId, Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId);
        if (attachment == null || !attachment.getBlog().getId().equals(blogId)) {
            throw new IllegalArgumentException("Attachment with ID " + attachmentId + " not found for blog " + blogId + ".");
        }
        return attachment;
    }

    public InputStream downloadAttachment(Long blogId, Long attachmentId) {
        Attachment attachment = getAttachment(blogId, attachmentId);
        return minioService.downloadFile(attachment.getObjectKey());
    }

    @Transactional
    public void deleteAttachment(Long blogId, Long attachmentId) {
        Attachment attachment = getAttachment(blogId, attachmentId);
        minioService.deleteFile(attachment.getObjectKey());
        Log.info("File deleted from MinIO: " + attachment.getObjectKey());
        attachmentRepository.delete(attachment);
        Log.info("Attachment deleted: " + attachmentId);
    }

    @Transactional
    public void deleteAttachmentsByBlogId(Long blogId) {
        List<Attachment> attachments = attachmentRepository.findByBlogId(blogId);
        for (Attachment attachment : attachments) {
            minioService.deleteFile(attachment.getObjectKey());
        }
        attachmentRepository.deleteByBlogId(blogId);
        Log.info("All attachments deleted for blog: " + blogId);
    }
}
