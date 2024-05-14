package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionTest {

    @Test
    public void testFunction() {
        Function function = new Function();
        Input input = new Input("test message");
        Output output = function.function(input);
        assertEquals("Expected response", output.getResponse()); // Adjust the expected response accordingly
    }
}
