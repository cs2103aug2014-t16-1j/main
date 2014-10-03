package tkLogic;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParserTest {

    @Test
    public void testSplitUserInput() {
        String input =
                "add Meeting -between 9am -to 10am -on 12 Sep 2014 -at Boardroom";
        String[] output =
                { "add", "Meeting", "-between", "9am", "-to", "10am",
                        "-on", "12", "Sep", "2014", "-at", "Boardroom" };
        assertArrayEquals("Test that method splitUserInput works correctly",
                Parser.splitUserInput(input), output);
    }
}
