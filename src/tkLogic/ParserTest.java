package tkLogic;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class ParserTest {

    @Test
    public void testSplitUserInput() {
        Parser parser = new Parser();
        String input =
                "add Meeting -between 9am -to 10am -on 12 Sep 2014 -@ Boardroom";
        String[] output =
                { "add", "Meeting", "-between", "9am", "-to", "10am", "-on",
                        "12", "Sep", "2014", "-@", "Boardroom" };
        assertArrayEquals("Test that method splitUserInput works correctly",
                parser.splitUserInput(input), output);
    }

}
