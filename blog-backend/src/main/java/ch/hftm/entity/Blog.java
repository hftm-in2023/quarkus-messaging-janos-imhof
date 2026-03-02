package ch.hftm.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Blog {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Title darf nicht leer sein")
    private String title;

    @NotBlank(message = "Content darf nicht leer sein")
    private String content;

    private String validationStatus;

    private String summary;

    private LocalDateTime createdAt;

    public Blog() {}

    public Blog(String title, String content) {
        this.title = title;
        this.content = content;
        this.validationStatus = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getValidationStatus() {
        return this.validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
