package ch.hftm.blog.boundry;

import java.time.Duration;

import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/reactive")
public class ReactiveResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("uni/{name}")
    public Uni<String> greeting(String name) {
        return Uni.createFrom().item(name)
                .onItem().transform(n -> String.format("hello %s", name));
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/multi/{name}/{count}")
    public Multi<String> greetings(String name, int count) {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transform(n -> String.format("hello %s - %d", name, n))
                .select().first(count);
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    @Path("/sse/{name}/{count}")
    public Multi<String> greetingsAsServerSentEvents(int count, String name) {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transform(n -> String.format("hello %s - %d", name, n))
                .select().first(count);
    }
}
