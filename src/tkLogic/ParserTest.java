package tkLogic;

import static org.junit.Assert.*;
import org.junit.Test;
import tkLibrary.CommandType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;

public class ParserTest {
    @Test
    public void testParserAdd() {
        Parser parser = new Parser();
        String input = "add Meeting -f 9am -t 10am -o 12 Sep 2014 -@ Boardroom";
        UserInput userInput = parser.format(input);
        CommandType command = userInput.getCommand();
        assertEquals("Test that method format works correctly", CommandType.ADD,
                command);
        Task task = userInput.getTask();
        assertEquals("Test that the Description is correctly recorded", "Meeting",
                task.getDescription());
        assertEquals("Test that the Start Time is correctly recorded",
                "Fri Sep 12 09:00:00 SGT 2014", task.getStartTime().getTime()
                        .toString());
        assertEquals("Test that the End Time is correctly recorded",
                "Fri Sep 12 10:00:00 SGT 2014", task.getEndTime().getTime()
                        .toString());
        assertEquals("Test that the Location is correctly recorded", "Boardroom",
                task.getLocation());
        assertEquals("Test that the State is correctly set", StateType.PENDING,
                task.getState());
        assertEquals("Test that the frequency is as default", 0, task.getFrequency());
        assertEquals("Test that the frequency is as default", null,
                task.getFrequencyType());
    }

    @Test
    public void testParserDelete() {
        Parser parser = new Parser();
        String input = "delete Meeting";
        UserInput userInput = parser.format(input);
        CommandType command = userInput.getCommand();
        assertEquals("Test that method format works correctly", CommandType.DELETE,
                command);
        Task task = userInput.getTask();
        assertEquals("Test that the Description is correctly recorded", "Meeting",
                task.getDescription());
        assertEquals("Test that the Start Time is correctly recorded", null,
                task.getStartTime());
        assertEquals("Test that the End Time is correctly recorded", null,
                task.getEndTime());
        assertEquals("Test that the Location is correctly recorded", null,
                task.getLocation());
        assertEquals("Test that the State is correctly set", StateType.PENDING,
                task.getState());
        assertEquals("Test that the frequency is as default", 0, task.getFrequency());
        assertEquals("Test that the frequency is as default", null,
                task.getFrequencyType());
    }

    @Test
    public void testParserEdit() {
        Parser parser = new Parser();
        String input =
                "edit Meeting -f 9am -t 10am -o 12 Sep 2014 -@ Boardroom"
                        + " -c Board Meeting -f 9pm -t 11pm -o 12 Dec 2014 -@ Home";
        UserInput userInput = parser.format(input);
        CommandType command = userInput.getCommand();
        assertEquals("Test that method format works correctly", CommandType.EDIT,
                command);
        Task task = userInput.getTask();
        assertEquals("Test that the Description is correctly recorded", "Meeting",
                task.getDescription());
        assertEquals("Test that the Start Time is correctly recorded",
                "Fri Sep 12 09:00:00 SGT 2014", task.getStartTime().getTime()
                        .toString());
        assertEquals("Test that the End Time is correctly recorded",
                "Fri Sep 12 10:00:00 SGT 2014", task.getEndTime().getTime()
                        .toString());
        assertEquals("Test that the Location is correctly recorded", "Boardroom",
                task.getLocation());
        assertEquals("Test that the State is correctly set", StateType.PENDING,
                task.getState());
        assertEquals("Test that the frequency is as default", 0, task.getFrequency());
        assertEquals("Test that the frequency is as default", null,
                task.getFrequencyType());
        Task editedTask = userInput.getEditedTask();
        assertEquals("Test that the Description is correctly recorded",
                "Board Meeting", editedTask.getDescription());
        assertEquals("Test that the Start Time is correctly recorded",
                "Fri Dec 12 21:00:00 SGT 2014", editedTask.getStartTime().getTime()
                        .toString());
        assertEquals("Test that the End Time is correctly recorded",
                "Fri Dec 12 23:00:00 SGT 2014", editedTask.getEndTime().getTime()
                        .toString());
        assertEquals("Test that the Location is correctly recorded", "Home",
                editedTask.getLocation());
        assertEquals("Test that the State is correctly set", StateType.PENDING,
                editedTask.getState());
        assertEquals("Test that the frequency is as default", 0,
                editedTask.getFrequency());
        assertEquals("Test that the frequency is as default", null,
                editedTask.getFrequencyType());
    }

    @Test
    public void testParserUndo() {
        Parser parser = new Parser();
        String input = "undo";
        UserInput userInput = parser.format(input);
        CommandType command = userInput.getCommand();
        assertEquals("Test that method format works correctly", CommandType.UNDO,
                command);
        Task task = userInput.getTask();
        assertEquals("Test that the Description is correctly recorded", null,
                task.getDescription());
        assertEquals("Test that the Start Time is correctly recorded", null,
                task.getStartTime());
        assertEquals("Test that the End Time is correctly recorded", null,
                task.getEndTime());
        assertEquals("Test that the Location is correctly recorded", null,
                task.getLocation());
        assertEquals("Test that the State is correctly set", null, task.getState());
        assertEquals("Test that the frequency is as default", 0, task.getFrequency());
        assertEquals("Test that the frequency is as default", null,
                task.getFrequencyType());
    }
}