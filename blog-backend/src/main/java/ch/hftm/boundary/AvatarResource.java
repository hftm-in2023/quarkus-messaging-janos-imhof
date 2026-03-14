package ch.hftm.boundary;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import ch.hftm.boundary.exception.FileStorageException;
import ch.hftm.boundary.exception.FileValidationException;
import ch.hftm.boundary.exception.ResourceNotFoundException;
import ch.hftm.control.FileValidator;
import ch.hftm.control.MinioService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/users/{username}/avatar")
public class AvatarResource {

    @Inject
    MinioService minioService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAvatar(@PathParam("username") String username, @RestForm("file") FileUpload file) {
        Log.info("POST /users/" + username + "/avatar");

        if (file == null || file.fileName() == null) {
            throw new FileValidationException("No file uploaded.");
        }

        String contentType = file.contentType();
        long fileSize = file.size();

        FileValidator.validateAvatar(contentType, fileSize);

        String objectKey = "avatars/" + username;

        try (InputStream inputStream = Files.newInputStream(file.uploadedFile())) {
            minioService.uploadFile(objectKey, inputStream, fileSize, contentType);
            Log.info("Avatar uploaded for user: " + username);
            return Response.ok()
                    .entity(Json.createObjectBuilder()
                            .add("message", "Avatar uploaded successfully.")
                            .add("username", username)
                            .build().toString())
                    .build();
        } catch (IOException e) {
            Log.error("Error reading uploaded avatar file", e);
            throw new FileStorageException("Error processing the uploaded file.", e);
        }
    }

    @GET
    public Response getAvatar(@PathParam("username") String username) {
        Log.info("GET /users/" + username + "/avatar");

        String objectKey = "avatars/" + username;

        if (!minioService.fileExists(objectKey)) {
            throw new ResourceNotFoundException("No avatar found for user '" + username + "'.");
        }

        InputStream fileStream = minioService.downloadFile(objectKey);
        return Response.ok(fileStream)
                .header("Content-Disposition", "inline; filename=\"avatar-" + username + "\"")
                .build();
    }
}
