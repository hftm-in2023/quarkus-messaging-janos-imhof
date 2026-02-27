package ch.hftm.control;

import java.util.List;

import ch.hftm.entity.ValidationRecord;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TextValidationService {

    private static final int MIN_LENGTH = 5;
    private static final List<String> BLOCKED_WORDS = List.of("spam", "scam", "verboten", "hftm sucks");

    public boolean validate(String text) {
        if (text == null || text.isBlank()) {
            Log.info("Validation failed: text is empty");
            return false;
        }
        if (text.length() < MIN_LENGTH) {
            Log.info("Validation failed: text too short (" + text.length() + " chars)");
            return false;
        }
        String lowerText = text.toLowerCase();
        for (String blocked : BLOCKED_WORDS) {
            if (lowerText.contains(blocked)) {
                Log.info("Validation failed: blocked word found '" + blocked + "'");
                return false;
            }
        }
        Log.info("Validation passed");
        return true;
    }

    public String getReason(String text) {
        if (text == null || text.isBlank()) {
            return "Text is empty";
        }
        if (text.length() < MIN_LENGTH) {
            return "Text too short (min " + MIN_LENGTH + " chars)";
        }
        String lowerText = text.toLowerCase();
        for (String blocked : BLOCKED_WORDS) {
            if (lowerText.contains(blocked)) {
                return "Contains blocked word: " + blocked;
            }
        }
        return null;
    }

    @Transactional
    public void saveRecord(long sourceId, String sourceType, String text, boolean valid) {
        String result = valid ? "APPROVED" : "REJECTED";
        String reason = valid ? null : getReason(text);
        ValidationRecord record = new ValidationRecord(sourceId, sourceType, text, result, reason);
        record.persist();
        Log.info("Saved ValidationRecord for sourceId=" + sourceId + " result=" + result);
    }
}
