package ch.hftm.control;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ThumbnailService {

    private static final int THUMBNAIL_MAX_SIZE = 200;

    public boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    public InputStream generateThumbnail(InputStream originalImage, String contentType) throws IOException {
        BufferedImage original = ImageIO.read(originalImage);
        if (original == null) {
            throw new IOException("Could not read image for thumbnail generation.");
        }

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        double scale = Math.min(
                (double) THUMBNAIL_MAX_SIZE / originalWidth,
                (double) THUMBNAIL_MAX_SIZE / originalHeight);

        if (scale >= 1.0) {
            scale = 1.0;
        }

        int thumbWidth = (int) (originalWidth * scale);
        int thumbHeight = (int) (originalHeight * scale);

        BufferedImage thumbnail = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, thumbWidth, thumbHeight, null);
        g2d.dispose();

        String formatName = getFormatName(contentType);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, formatName, baos);

        Log.info("Thumbnail generated: " + thumbWidth + "x" + thumbHeight + " (" + baos.size() + " bytes)");
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public long getThumbnailSize(InputStream thumbnailStream) throws IOException {
        byte[] bytes = thumbnailStream.readAllBytes();
        return bytes.length;
    }

    private String getFormatName(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> "jpg";
        };
    }
}
