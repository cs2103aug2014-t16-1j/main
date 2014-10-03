package tkLogic;

import java.util.Date;

import tkLibrary.FrequencyType;
import tkLibrary.StateType;
import tkLibrary.Task;

/*
 * receive a string of command
 * and do parse it, return our type of tasks. 
 * The string is all information of the command, except the command type.
 */
public class Parser {

    private static Date parseStartTime(String userCommand) {
        return null;
    }

    private static Date parseEndTime(String userCommand) {
        return null;
    }

    private static String parseDescription(String userCommand) {
        return null;
    }

    private static String parseLocation(String userCommand) {
        return null;
    }

    private static FrequencyType parseFrequency(String userCommand) {
        return null;
    }

    public static Task format(String userCommand) {
        Task task = new Task();
        parseAll(userCommand, task);
        return task;
    }

    private static void parseAll(String userCommand, Task task) {
        String[] userInput = splitUserInput(userCommand);
        task.setStartTime(parseStartTime(userCommand));
        task.setEndTime(parseEndTime(userCommand));
        task.setDescription(parseDescription(userCommand));
        task.setLocation(parseLocation(userCommand));
        task.setFrequency(parseFrequency(userCommand));
        task.setState(StateType.PENDING);
    }

    public static String[] splitUserInput(String userCommand) {
        return userCommand.trim().split("\\s+");
    }
}
