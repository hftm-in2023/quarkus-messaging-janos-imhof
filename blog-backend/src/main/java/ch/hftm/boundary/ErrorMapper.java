package ch.hftm.boundary;

import ch.hftm.boundary.exception.FileStorageException;
import ch.hftm.boundary.exception.FileValidationException;
import ch.hftm.boundary.exception.ResourceNotFoundException;
import ch.hftm.boundary.exception.StorageQuotaExceededException;
import jakarta.json.Json;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

public class ErrorMapper {

    @Provider
    public static class NotFoundMapper implements ExceptionMapper<ResourceNotFoundException> {
        @Override
        public Response toResponse(ResourceNotFoundException exception) {
            return errorResponse(Response.Status.NOT_FOUND, exception.getMessage());
        }
    }

    @Provider
    public static class FileValidationMapper implements ExceptionMapper<FileValidationException> {
        @Override
        public Response toResponse(FileValidationException exception) {
            return errorResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }
    }

    @Provider
    public static class FileStorageMapper implements ExceptionMapper<FileStorageException> {
        @Override
        public Response toResponse(FileStorageException exception) {
            return errorResponse(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @Provider
    public static class StorageQuotaMapper implements ExceptionMapper<StorageQuotaExceededException> {
        @Override
        public Response toResponse(StorageQuotaExceededException exception) {
            return errorResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }
    }

    @Provider
    public static class IllegalArgumentMapper implements ExceptionMapper<IllegalArgumentException> {
        @Override
        public Response toResponse(IllegalArgumentException exception) {
            return errorResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }
    }

    private static Response errorResponse(Response.Status status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Json.createObjectBuilder()
                        .add("error", message)
                        .build())
                .build();
    }
}
