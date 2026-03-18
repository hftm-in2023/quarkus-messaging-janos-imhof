package ch.hftm.boundary;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import ch.hftm.boundary.exception.FileStorageException;
import ch.hftm.boundary.exception.FileValidationException;
import ch.hftm.boundary.exception.ResourceNotFoundException;
import ch.hftm.control.AttachmentService;
import ch.hftm.control.BlogService;
import ch.hftm.control.FileValidator;
import ch.hftm.control.MinioService;
import ch.hftm.entity.Attachment;
import ch.hftm.entity.Blog;
import ch.hftm.messaging.AttachmentEvent;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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

    @Inject
    BlogService blogService;

    @Inject
    MinioService minioService;

    @Inject
    @Channel("attachment-events")
    Emitter<AttachmentEvent> attachmentEventEmitter;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAttachment(@PathParam("blogId") Long blogId, @RestForm("file") FileUpload file) {
        Log.info("POST /blogs/" + blogId + "/attachments");

        if (file == null || file.fileName() == null) {
            throw new FileValidationException("No file uploaded.");
        }

        String contentType = file.contentType();
        long fileSize = file.size();
        String fileName = file.fileName();

        FileValidator.validateAttachment(contentType, fileSize);

        try (InputStream inputStream = Files.newInputStream(file.uploadedFile())) {
            Attachment attachment = attachmentService.uploadAttachment(blogId, fileName, contentType, fileSize,
                    inputStream);

            Blog blog = blogService.getBlog(blogId);
            attachmentEventEmitter.send(
                    new AttachmentEvent(blogId, blog.getTitle(), fileName, contentType, fileSize));
            Log.info("Attachment event sent for blog id=" + blogId);

            return Response.status(Response.Status.CREATED).entity(attachment).build();
        } catch (IOException e) {
            Log.error("Error reading uploaded file", e);
            throw new FileStorageException("Error processing the uploaded file.", e);
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

    @GET
    @Path("{attachmentId}/thumbnail")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadThumbnail(@PathParam("blogId") Long blogId,
            @PathParam("attachmentId") Long attachmentId) {
        Log.info("GET /blogs/" + blogId + "/attachments/" + attachmentId + "/thumbnail");
        Attachment attachment = attachmentService.getAttachment(blogId, attachmentId);
        if (attachment.getThumbnailKey() == null) {
            throw new ResourceNotFoundException("No thumbnail available for attachment " + attachmentId + ".");
        }
        InputStream thumbStream = minioService.downloadFile(attachment.getThumbnailKey());
        return Response.ok(thumbStream)
                .header("Content-Type", attachment.getContentType())
                .header("Content-Disposition",
                        "inline; filename=\"thumb_" + attachment.getFileName() + "\"")
                .build();
    }

    @DELETE
    @Path("{attachmentId}")
    public Response deleteAttachment(@PathParam("blogId") Long blogId,
            @PathParam("attachmentId") Long attachmentId) {
        Log.info("DELETE /blogs/" + blogId + "/attachments/" + attachmentId);
        attachmentService.deleteAttachment(blogId, attachmentId);
        return Response.noContent().build();
    }
}
