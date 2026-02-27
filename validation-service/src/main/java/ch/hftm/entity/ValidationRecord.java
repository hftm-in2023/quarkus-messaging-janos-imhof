package ch.hftm.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class ValidationRecord extends PanacheEntity {

    public long sourceId;
    public String sourceType;
    public String text;
    public String result;
    public String reason;
    public LocalDateTime processedAt;

    public ValidationRecord() {}

    public ValidationRecord(long sourceId, String sourceType, String text, String result, String reason) {
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.text = text;
        this.result = result;
        this.reason = reason;
        this.processedAt = LocalDateTime.now();
    }
}
