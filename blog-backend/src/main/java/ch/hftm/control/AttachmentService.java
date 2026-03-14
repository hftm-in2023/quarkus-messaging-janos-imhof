package ch.hftm.control;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import ch.hftm.boundary.exception.FileStorageException;
import ch.hftm.boundary.exception.ResourceNotFoundException;
import ch.hftm.entity.Attachment;
import ch.hftm.entity.Blog;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AttachmentService {

    @Inject
    AttachmentRepository attachmentRepository;

    @Inject
    BlogRepository blogRepository;

    @Inject
    MinioService minioService;

    @Inject
    ThumbnailService thumbnailService;

    @Transactional
    public Attachment uploadAttachment(Long blogId, String fileName, String contentType, long fileSize,
            InputStream fileData) {
        Blog blog = blogRepository.findById(blogId);
        if (blog == null) {
            throw new ResourceNotFoundException("Blog with ID " + blogId + " not found.");
        }

        FileValidator.validateAttachment(contentType, fileSize);

        String uuid = UUID.randomUUID().toString();
        String objectKey = "blogs/" + blogId + "/" + uuid + "_" + fileName;

        byte[] fileBytes;
        try {
            fileBytes = fileData.readAllBytes();
        } catch (IOException e) {
            throw new FileStorageException("Error reading uploaded file.", e);
        }

        minioService.uploadFile(objectKey, new ByteArrayInputStream(fileBytes), fileSize, contentType);
        Log.info("File uploaded to MinIO: " + objectKey);

        Attachment attachment = new Attachment(blog, fileName, contentType, fileSize, objectKey);

        if (thumbnailService.isImage(contentType)) {
            try {
                InputStream thumbStream = thumbnailService.generateThumbnail(
                        new ByteArrayInputStream(fileBytes), contentType);
                byte[] thumbBytes = thumbStream.readAllBytes();
                String thumbnailKey = "blogs/" + blogId + "/thumbs/" + uuid + "_" + fileName;
                minioService.uploadFile(thumbnailKey, new ByteArrayInputStream(thumbBytes), thumbBytes.length,
                        contentType);
                attachment.setThumbnailKey(thumbnailKey);
                Log.info("Thumbnail uploaded to MinIO: " + thumbnailKey);
            } catch (IOException e) {
                Log.warn("Failed to generate thumbnail, skipping: " + e.getMessage());
            }
        }

        attachmentRepository.persist(attachment);
        Log.info("Attachment saved: " + attachment.getId());

        return attachment;
    }

    public List<Attachment> getAttachments(Long blogId) {
        Blog blog = blogRepository.findById(blogId);
        if (blog == null) {
            throw new ResourceNotFoundException("Blog with ID " + blogId + " not found.");
        }
        return attachmentRepository.findByBlogId(blogId);
    }

    public Attachment getAttachment(Long blogId, Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId);
        if (attachment == null || !attachment.getBlog().getId().equals(blogId)) {
            throw new ResourceNotFoundException(
                    "Attachment with ID " + attachmentId + " not found for blog " + blogId + ".");
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
        if (attachment.getThumbnailKey() != null) {
            minioService.deleteFile(attachment.getThumbnailKey());
        }
        Log.info("File deleted from MinIO: " + attachment.getObjectKey());
        attachmentRepository.delete(attachment);
        Log.info("Attachment deleted: " + attachmentId);
    }

    @Transactional
    public void deleteAttachmentsByBlogId(Long blogId) {
        List<Attachment> attachments = attachmentRepository.findByBlogId(blogId);
        for (Attachment attachment : attachments) {
            minioService.deleteFile(attachment.getObjectKey());
            if (attachment.getThumbnailKey() != null) {
                minioService.deleteFile(attachment.getThumbnailKey());
            }
        }
        attachmentRepository.deleteByBlogId(blogId);
        Log.info("All attachments deleted for blog: " + blogId);
    }
}
