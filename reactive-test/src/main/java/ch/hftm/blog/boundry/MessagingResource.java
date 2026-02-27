package ch.hftm.blog.boundry;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

// Aufgabe d) - HTTP-Endpunkt zum Senden von Messages über einen Emitter.
// Ersetzt die source()-Methode, sodass Messages aktiv über HTTP angestossen werden.
@Path("/messaging")
public class MessagingResource {

    @Inject
    @Channel("source-outgoing")
    Emitter<String> emitter;

    // Aufgabe d) - Sendet eine einzelne Message in den "source"-Channel
    @POST
    @Path("/send/{message}")
    @Produces(MediaType.TEXT_PLAIN)
    public String send(String message) {
        emitter.send(message);
        return "Sent: " + message;
    }

    // Aufgabe d) - Sendet die Original-Beispielwörter in den "source"-Channel
    @POST
    @Path("/send-all")
    @Produces(MediaType.TEXT_PLAIN)
    public String sendAll() {
        String[] words = {"hallo", "böse", "quarkus", "fans"};
        for (String word : words) {
            emitter.send(word);
        }
        return "All messages sent";
    }

}
