package ch.hftm.boundary;

import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ErrorMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("error", exception.getMessage())
                        .build())
                .build();
    }
}
