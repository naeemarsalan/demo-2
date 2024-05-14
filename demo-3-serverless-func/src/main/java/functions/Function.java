package functions;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Function {

    @Incoming("generated-reports")
    public void function(String json) {
        System.out.println("Received JSON: " + json);
    }
}
