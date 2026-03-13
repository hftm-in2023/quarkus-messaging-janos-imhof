package ch.hftm.boundary;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

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

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif");
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5 MB

    @Inject
    MinioService minioService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAvatar(@PathParam("username") String username, @RestForm("file") FileUpload file) {
        Log.info("POST /users/" + username + "/avatar");

        if (file == null || file.fileName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorJson("No file uploaded."))
                    .build();
        }

        String contentType = file.contentType();
        long fileSize = file.size();

        if (fileSize > MAX_AVATAR_SIZE) {
            return Response.status(413)
                    .entity(errorJson("File too large. Maximum size: 5 MB, actual size: "
                            + (fileSize / 1024) + " KB."))
                    .build();
        }

        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorJson("Content type '" + contentType + "' not allowed. Allowed: "
                            + ALLOWED_IMAGE_TYPES))
                    .build();
        }

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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorJson("Error processing the uploaded file."))
                    .build();
        }
    }

    @GET
    public Response getAvatar(@PathParam("username") String username) {
        Log.info("GET /users/" + username + "/avatar");

        String objectKey = "avatars/" + username;

        if (!minioService.fileExists(objectKey)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorJson("No avatar found for user '" + username + "'."))
                    .build();
        }

        InputStream fileStream = minioService.downloadFile(objectKey);
        return Response.ok(fileStream)
                .header("Content-Disposition", "inline; filename=\"avatar-" + username + "\"")
                .build();
    }

    private String errorJson(String message) {
        return Json.createObjectBuilder().add("error", message).build().toString();
    }
}
