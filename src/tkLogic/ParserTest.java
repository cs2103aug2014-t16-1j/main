package tkLogic;

import static org.junit.Assert.*;

import org.junit.Test;

import tkLibrary.CommandType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;

public class ParserTest {

    @Test
    public void testParser() {
        Parser parser = new Parser();
        String input = "add Meeting -f 9am -t 10am -o 12 Sep 2014 -@ Boardroom";
        UserInput userInput = parser.format(input);
        CommandType command = userInput.getCommand();
        assertEquals("Test that method format works correctly",
                CommandType.ADD, command);
        Task task = userInput.getTask();
        assertEquals("Test that the Description is correctly recorded",
                "Meeting", task.getDescription());
        assertEquals("Test that the Start Time is correctly recorded",
                "Fri Sep 12 09:00:00 SGT 2014", task.getStartTime().getTime().toString());
        assertEquals("Test that the End Time is correctly recorded",
                "Fri Sep 12 10:00:00 SGT 2014", task.getEndTime().getTime().toString());
        assertEquals("Test that the Location is correctly recorded",
                "Boardroom", task.getLocation());
        assertEquals("Test that the State is correctly set",
                StateType.PENDING, task.getState());
        assertEquals("Test that the frequency is as default",
                0, task.getFrequency());
        assertEquals("Test that the frequency is as default",
                null, task.getFrequencyType());
    }

}
