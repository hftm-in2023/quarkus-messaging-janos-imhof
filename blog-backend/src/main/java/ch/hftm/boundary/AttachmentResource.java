package ch.hftm.boundary;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static ch.hftm.control.AttachmentService.ALLOWED_CONTENT_TYPES;
import static ch.hftm.control.AttachmentService.MAX_FILE_SIZE;

import ch.hftm.control.AttachmentService;
import ch.hftm.entity.Attachment;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/blogs/{blogId}/attachments")
public class AttachmentResource {

    @Inject
    AttachmentService attachmentService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAttachment(@PathParam("blogId") Long blogId, @RestForm("file") FileUpload file) {
        Log.info("POST /blogs/" + blogId + "/attachments");

        if (file == null || file.fileName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorJson("No file uploaded."))
                    .build();
        }

        String contentType = file.contentType();
        long fileSize = file.size();
        String fileName = file.fileName();

        if (fileSize > MAX_FILE_SIZE) {
            return Response.status(413)
                    .entity(errorJson("File too large. Maximum size: 5 MB, actual size: "
                            + (fileSize / 1024) + " KB."))
                    .build();
        }

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorJson("Content type '" + contentType + "' not allowed. Allowed: "
                            + ALLOWED_CONTENT_TYPES))
                    .build();
        }

        try (InputStream inputStream = Files.newInputStream(file.uploadedFile())) {
            Attachment attachment = attachmentService.uploadAttachment(blogId, fileName, contentType, fileSize,
                    inputStream);
            return Response.status(Response.Status.CREATED).entity(attachment).build();
        } catch (IOException e) {
            Log.error("Error reading uploaded file", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorJson("Error processing the uploaded file."))
                    .build();
        }
    }

    @GET
    public List<Attachment> getAttachments(@PathParam("blogId") Long blogId) {
        Log.info("GET /blogs/" + blogId + "/attachments");
        return attachmentService.getAttachments(blogId);
    }

    @GET
    @Path("{attachmentId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadAttachment(@PathParam("blogId") Long blogId,
            @PathParam("attachmentId") Long attachmentId) {
        Log.info("GET /blogs/" + blogId + "/attachments/" + attachmentId);
        Attachment attachment = attachmentService.getAttachment(blogId, attachmentId);
        InputStream fileStream = attachmentService.downloadAttachment(blogId, attachmentId);
        return Response.ok(fileStream)
                .header("Content-Type", attachment.getContentType())
                .header("Content-Disposition", "inline; filename=\"" + attachment.getFileName() + "\"")
                .header("Content-Length", attachment.getFileSize())
                .build();
    }

    private String errorJson(String message) {
        return Json.createObjectBuilder().add("error", message).build().toString();
    }
}