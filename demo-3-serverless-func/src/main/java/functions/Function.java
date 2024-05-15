package functions;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.funqy.Funq;


/**
 * Your Function class
 */
public class Function {
    private static final Logger LOGGER = Logger.getLogger(Function.class);

    /**
     * Use the Quarkus Funqy extension for our function. This function simply echoes its input
     * @param input a Java bean
     * @return a Java bean
     */

    @Incoming("reports")
    public void receiveMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Employee person = objectMapper.readValue(message, Employee.class);
            LOGGER.infov("Received message from: {0}", person.getFirstName());
        } catch (Exception e) {
            LOGGER.error("Error processing message: " + e.getMessage(), e);
        }
    }

    @Funq
    public Output function() {

        // Add business logic here

        return new Output();
    }

}
