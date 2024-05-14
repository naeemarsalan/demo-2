package functions;

import io.quarkus.funqy.Funq;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Your Function class
 */

public class Function {

    /**
     * Use the Quarkus Funqy extension for our function. This function simply echoes its input
     * @param input a Java bean
     * @return a Java bean
     * 
     */

    private final Logger logger = Logger.getLogger(Function.class);

    @Funq
    @Incoming("generated-reports")
    public Output function(Input input) {

        // Add business logic here
        logger.info("Received message: " + input.getMessage());

        return new Output(input.getMessage());
    }

}
