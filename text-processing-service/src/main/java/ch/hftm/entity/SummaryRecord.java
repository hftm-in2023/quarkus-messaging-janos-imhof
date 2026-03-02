package ch.hftm.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class SummaryRecord extends PanacheEntity {

    public long sourceId;

    @Column(columnDefinition = "TEXT")
    public String originalText;

    public String summary;
    public LocalDateTime processedAt;

    public SummaryRecord() {}

    public SummaryRecord(long sourceId, String originalText, String summary) {
        this.sourceId = sourceId;
        this.originalText = originalText;
        this.summary = summary;
        this.processedAt = LocalDateTime.now();
    }
}
