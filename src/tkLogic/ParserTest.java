package tkLogic;

import static org.junit.Assert.*;

import org.junit.Test;

import tkLibrary.CommandType;
import tkLibrary.Task;
import tkLibrary.UserInput;

public class ParserTest {

    @Test
    public void testParser() {
        Parser parser = new Parser();
        String input = "add Meeting -f 9am -t 10am -o 12 Sep 2014 -@ Boardroom";
        UserInput userInput = parser.format(input);
        CommandType command = userInput.getCommand();
        assertEquals("Test that method splitUserInput works correctly",
                command, CommandType.ADD);
        Task task = userInput.getTask();
    }

}
