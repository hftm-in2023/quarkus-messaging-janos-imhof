package ch.hftm.boundary;

import java.util.List;

import ch.hftm.control.NotificationService;
import ch.hftm.entity.Notification;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/notifications")
public class NotificationResource {

    @Inject
    NotificationService notificationService;

    @GET
    public List<Notification> getNotifications() {
        Log.info("GET /notifications");
        return notificationService.getAll();
    }

    @PUT
    @Path("{id}/read")
    public Response markAsRead(@PathParam("id") long id) {
        Log.info("PUT /notifications/" + id + "/read");
        Notification notification = notificationService.markAsRead(id);
        if (notification == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(notification).build();
    }
}
