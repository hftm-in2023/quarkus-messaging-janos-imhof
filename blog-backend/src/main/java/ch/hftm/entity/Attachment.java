package ch.hftm.entity;

import java.time.LocalDateTime;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Attachment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonbTransient
    private Blog blog;

    private String fileName;

    private String contentType;

    private long fileSize;

    private String objectKey;

    private String thumbnailKey;

    private LocalDateTime createdAt;

    public Attachment() {}

    public Attachment(Blog blog, String fileName, String contentType, long fileSize, String objectKey) {
        this.blog = blog;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.objectKey = objectKey;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public Blog getBlog() {
        return this.blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getObjectKey() {
        return this.objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getThumbnailKey() {
        return this.thumbnailKey;
    }

    public void setThumbnailKey(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
