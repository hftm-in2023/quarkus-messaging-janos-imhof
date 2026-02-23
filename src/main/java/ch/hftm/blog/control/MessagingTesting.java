package ch.hftm.blog.control;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class MessagingTesting {

    // Aufgabe a) - Source: Wird automatisch von Quarkus beim Start angestossen,
    // da ein Consumer auf den Channel "source" hört. Quarkus verbindet die
    // definierten Channels und öffnet die Pipeline beim Start.
    // Aufgabe d) - Auskommentiert, da Messages nun über einen HTTP-Endpunkt
    // via Emitter gesendet werden.
    // @Outgoing("source")
    // public Multi<String> source() {
    //     return Multi.createFrom().items("hallo", "böse", "quarkus", "fans");
    // }

    // Aufgabe a) - Transformer: Wandelt alle Strings in Grossbuchstaben um
    @Incoming("source")
    @Outgoing("processed-a")
    public String toUpperCase(String payload) {
        return payload.toUpperCase();
    }

    // Aufgabe b) - Transformer: Ersetzt "BÖSE" durch "LIEBE" nach toUpperCase()
    @Incoming("processed-a")
    @Outgoing("processed-b")
    public String replaceBoese(String payload) {
        if ("BÖSE".equals(payload)) {
            return "LIEBE";
        }
        return payload;
    }

    // Aufgabe c) - Filter: Schliesst Zeichenketten aus, die kürzer als 6 Zeichen sind.
    // Verwendet Multi<String> als Input- und Output-Typ.
    @Incoming("processed-b")
    @Outgoing("processed-c")
    public Multi<String> filterShort(Multi<String> input) {
        return input.filter(s -> s.length() >= 6);
    }

    // Aufgabe a) - Sink: Gibt die verarbeiteten Messages in der Konsole aus
    @Incoming("processed-c")
    public void sink(String word) {
        System.out.println(">> " + word);
    }

}
