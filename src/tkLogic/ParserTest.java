package tkLogic;

import static org.junit.Assert.*;

import org.junit.Test;

import tkLibrary.CommandType;
import tkLibrary.PriorityType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;

public class ParserTest {
    /* This is a boundary case for the optimal partition */
    @Test
    public void testParserAdd() {
        Parser parser = Parser.getInstance();
        String input = "add Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
        UserInput userInput;
        try {
            userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly", CommandType.ADD,
                    command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded",
                    "Meeting", task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Sep 12 09:00:00 SGT 2014", task.getStartTime().getTime()
                            .toString());
            assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", task.getEndTime().getTime()
                            .toString());
            assertEquals("Test that the Location is correctly recorded",
                    "Boardroom", task.getLocation());
            assertEquals("Test that the State is correctly set", StateType.PENDING,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
        } catch (Exception e) {
            System.out.println("testParserAdd: ");
            e.printStackTrace();
            assert (false);
        }
    }

    /* This is a boundary case for the partition with additional date */
    @Test
    public void testParserAddTwoDates() {
        Parser parser = Parser.getInstance();
        String input =
                "add Meeting from 9am on 11 Sep 2014 to 10am on 12 Sep 2014 at Boardroom";
        UserInput userInput;
        try {
            userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly", CommandType.ADD,
                    command);
            Task task = userInput.getTask();
            assertEquals("Test that the Start Time is correctly recorded",
                    "Thu Sep 11 09:00:00 SGT 2014", task.getStartTime().getTime()
                            .toString());
            assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", task.getEndTime().getTime()
                            .toString());
        } catch (Exception e) {
            System.out.println("testParserAddTwoDates: ");
            e.printStackTrace();
            assert (false);
        }
    }

    /* This is a boundary case for the partition with only one date and time */
    @Test
    public void testParserAddDeadline() {
        Parser parser = Parser.getInstance();
        String input = "add Submit audit report by 9am on 12 Sep 2014";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly", CommandType.ADD,
                    command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded",
                    "Submit audit report", task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Sep 12 09:00:00 SGT 2014", task.getStartTime().getTime()
                            .toString());
            assertEquals("Test that the End Time is correctly recorded", null,
                    task.getEndTime());
            assertEquals("Test that the Location is correctly recorded", null,
                    task.getLocation());
            assertEquals("Test that the State is correctly set", StateType.PENDING,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
        } catch (Exception e) {
            System.out.println("testParserAddDeadline: ");
            e.printStackTrace();
            assert (false);
        }
    }
    
    /* This is a boundary case for the partition with no date and time */
    @Test
    public void testParserAddGoodToDo() {
        Parser parser = Parser.getInstance();
        String input = "add Read The Lord of the Rings: The Return of the King";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly", CommandType.ADD,
                    command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded",
                    "Read The Lord of the Rings: The Return of the King",
                    task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded", null,
                    task.getStartTime());
            assertEquals("Test that the End Time is correctly recorded", null,
                    task.getEndTime());
            assertEquals("Test that the Location is correctly recorded", null,
                    task.getLocation());
            assertEquals("Test that the State is correctly set", StateType.PENDING,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
        } catch (Exception e) {
            System.out.println("testParserAddGoodToDo: ");
            e.printStackTrace();
            assert (false);
        }
    }
    
    /* This is a boundary case for the partition with keyword as description */
    @Test
    public void testParserAddDescriptionWithKeys() {
        Parser parser = Parser.getInstance();
        String input = "add ask John /to /add me as a friend /from school";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly", CommandType.ADD,
                    command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded",
                    "ask John to add me as a friend from school",
                    task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded", null,
                    task.getStartTime());
            assertEquals("Test that the End Time is correctly recorded", null,
                    task.getEndTime());
            assertEquals("Test that the Location is correctly recorded", null,
                    task.getLocation());
            assertEquals("Test that the State is correctly set", StateType.PENDING,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
        } catch (Exception e) {
            System.out.println("testParserAddDescriptionWithKeys: ");
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testParserDelete() {
        Parser parser = Parser.getInstance();
        String input = "delete Meeting";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly",
                    CommandType.DELETE, command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded",
                    "Meeting", task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded", null,
                    task.getStartTime());
            assertEquals("Test that the End Time is correctly recorded", null,
                    task.getEndTime());
            assertEquals("Test that the Location is correctly recorded", null,
                    task.getLocation());
            assertEquals("Test that the State is correctly set", StateType.PENDING,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
        } catch (Exception e) {
            System.out.println("testParserDelete: ");
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testParserEdit() {
        Parser parser = Parser.getInstance();
        String input =
                "edit Meeting from 9am to 10am on 12 Sep 2014 at Boardroom"
                        + " correct Board Meeting from 9pm to 11pm on 12 Dec 2014 at Home";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly",
                    CommandType.EDIT, command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded",
                    "Meeting", task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Sep 12 09:00:00 SGT 2014", task.getStartTime().getTime()
                            .toString());
            assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", task.getEndTime().getTime()
                            .toString());
            assertEquals("Test that the Location is correctly recorded",
                    "Boardroom", task.getLocation());
            assertEquals("Test that the State is correctly set", StateType.PENDING,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
            Task editedTask = userInput.getEditedTask();
            assertEquals("Test that the Description is correctly recorded",
                    "Board Meeting", editedTask.getDescription());
            assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Dec 12 21:00:00 SGT 2014", editedTask.getStartTime()
                            .getTime().toString());
            assertEquals("Test that the End Time is correctly recorded",
                    "Fri Dec 12 23:00:00 SGT 2014", editedTask.getEndTime()
                            .getTime().toString());
            assertEquals("Test that the Location is correctly recorded", "Home",
                    editedTask.getLocation());
            assertEquals("Test that the State is correctly set", StateType.PENDING,
                    editedTask.getState());
            assertEquals("Test that the frequency is as default", 0,
                    editedTask.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    editedTask.getFrequencyType());
        } catch (Exception e) {
            System.out.println("testParserEdit: ");
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testParserUndo() {
        Parser parser = Parser.getInstance();
        String input = "undo";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly",
                    CommandType.UNDO, command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded", null,
                    task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded", null,
                    task.getStartTime());
            assertEquals("Test that the End Time is correctly recorded", null,
                    task.getEndTime());
            assertEquals("Test that the Location is correctly recorded", null,
                    task.getLocation());
            assertEquals("Test that the State is correctly set", null,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
        } catch (Exception e) {
            System.out.println("testParserUndo: ");
            e.printStackTrace();
            assert (false);
        }
    }
    
    public void testParserClear() {
        Parser parser = Parser.getInstance();
        String input = "clear";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly",
                    CommandType.CLEAR, command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded", null,
                    task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded", null,
                    task.getStartTime());
            assertEquals("Test that the End Time is correctly recorded", null,
                    task.getEndTime());
            assertEquals("Test that the Location is correctly recorded", null,
                    task.getLocation());
            assertEquals("Test that the State is correctly set", null,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
        } catch (Exception e) {
            System.out.println("testParserUndo: ");
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testParserSetPriority() {
        Parser parser = Parser.getInstance();
        String input = "set Submit audit report priority high";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly", CommandType.SET,
                    command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded",
                    "Submit audit report", task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded", null,
                    task.getStartTime());
            assertEquals("Test that the End Time is correctly recorded", null,
                    task.getEndTime());
            assertEquals("Test that the Location is correctly recorded", null,
                    task.getLocation());
            assertEquals("Test that the State is correctly set", StateType.PENDING,
                    task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
            assertEquals("Test that the priority is correctly recorded",
                    PriorityType.HIGH, task.getPriorityLevel());
        } catch (Exception e) {
            System.out.println("testParserUndo: ");
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testParserSetState() {
        Parser parser = Parser.getInstance();
        String input = "set Submit audit report status completed";
        try {
            UserInput userInput = parser.format(input);
            CommandType command = userInput.getCommand();
            assertEquals("Test that method format works correctly", CommandType.SET,
                    command);
            Task task = userInput.getTask();
            assertEquals("Test that the Description is correctly recorded",
                    "Submit audit report", task.getDescription());
            assertEquals("Test that the Start Time is correctly recorded", null,
                    task.getStartTime());
            assertEquals("Test that the End Time is correctly recorded", null,
                    task.getEndTime());
            assertEquals("Test that the Location is correctly recorded", null,
                    task.getLocation());
            assertEquals("Test that the State is correctly set",
                    StateType.COMPLETED, task.getState());
            assertEquals("Test that the frequency is as default", 0,
                    task.getFrequency());
            assertEquals("Test that the frequency is as default", null,
                    task.getFrequencyType());
            assertEquals("Test that the priority is correctly recorded", null,
                    task.getPriorityLevel());
        } catch (Exception e) {
            System.out.println("testParserUndo: ");
            e.printStackTrace();
            assert (false);
        }
    }
}