package ch.hftm.control;

import ch.hftm.entity.SummaryRecord;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TextSummaryService {

    private static final int MAX_SUMMARY_LENGTH = 50;

    public String summarize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        if (text.length() <= MAX_SUMMARY_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_SUMMARY_LENGTH) + "...";
    }

    @Transactional
    public void saveRecord(long sourceId, String originalText, String summary) {
        SummaryRecord record = new SummaryRecord(sourceId, originalText, summary);
        record.persist();
        Log.info("Saved SummaryRecord for sourceId=" + sourceId);
    }
}
