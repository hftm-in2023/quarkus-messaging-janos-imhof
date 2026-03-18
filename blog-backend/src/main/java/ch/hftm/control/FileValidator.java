package ch.hftm.control;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import ch.hftm.boundary.exception.FileStorageException;
import ch.hftm.boundary.exception.FileValidationException;
import io.quarkus.logging.Log;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public final class FileValidator {

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "application/pdf");

    public static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif");

    private FileValidator() {
    }

    public static void validateFile(String contentType, long fileSize, List<String> allowedTypes) {
        if (fileSize == 0) {
            throw new FileValidationException("File is empty. Please upload a non-empty file.");
        }

        if (fileSize > MAX_FILE_SIZE) {
            throw new FileValidationException(
                    "File too large. Maximum size: 5 MB, actual size: " + (fileSize / 1024) + " KB.");
        }

        if (!allowedTypes.contains(contentType)) {
            throw new FileValidationException(
                    "Content type '" + contentType + "' not allowed. Allowed: " + allowedTypes);
        }
    }

    public static void validateAttachment(String contentType, long fileSize) {
        validateFile(contentType, fileSize, ALLOWED_CONTENT_TYPES);
    }

    public static void validateAvatar(String contentType, long fileSize) {
        validateFile(contentType, fileSize, ALLOWED_IMAGE_TYPES);
    }

    public static void validateUpload(FileUpload file) {
        if (file == null || file.fileName() == null) {
            throw new FileValidationException("No file uploaded.");
        }
    }

    public static InputStream openStream(FileUpload file) {
        try {
            return Files.newInputStream(file.uploadedFile());
        } catch (IOException e) {
            Log.error("Error reading uploaded file", e);
            throw new FileStorageException("Error processing the uploaded file.", e);
        }
    }
}
