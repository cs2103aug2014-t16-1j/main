package tkLogic;

import java.util.Date;

import tkLibrary.CommandType;
import tkLibrary.FrequencyType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLibrary.CommandKey;

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
    
    private static Date parseDate(String userCommand) {
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

    public static UserInput format(String userCommand) {
        Task task = new Task();
        UserInput userInput;

        userInput = parseAll(userCommand, task);
        return userInput;
    }

    private static UserInput parseAll(String userCommand, Task task) {
        String[] userInputArray = splitUserInput(userCommand);
        UserInput userInput = new UserInput(determineCommandType(userInputArray[0]), task);

        String word;
        CommandKey commandKey = determineCommandKey("-d");
        for (int i = 0; i < userInputArray.length; i++) {
            word = userInputArray[i];
            if (word.substring(0, 1) == "-") {
                
                executeCommandKey(word, commandKey);
                
                // do something about the previous description
                // store the command type instead of command string?
                // -f from -t to -@ -o on -b by -e every

                commandKey = determineCommandKey(word);

               
            } else {
                word += " " + word;
            }
        }

        task.setStartTime(parseStartTime(userCommand));
        task.setEndTime(parseEndTime(userCommand));
        task.setDescription(parseDescription(userCommand));
        task.setLocation(parseLocation(userCommand));
        task.setFrequency(parseFrequency(userCommand));
        task.setState(StateType.PENDING);

        return userInput;
    }

    private static CommandKey determineCommandKey(String commandKeyString) throws Error {
        if (commandKeyString == null) {
            throw new Error("command type string cannot be null!");
        }

        if (commandKeyString.equalsIgnoreCase("-d")) {
            return CommandKey.DESCRIPTION;
        } else if (commandKeyString.equalsIgnoreCase("-f")) {
            return CommandKey.FROM;
        } else if (commandKeyString.equalsIgnoreCase("-t")) {
            return CommandKey.TO;
        } else if (commandKeyString.equalsIgnoreCase("-@")) {
            return CommandKey.AT;
        } else if (commandKeyString.equalsIgnoreCase("-o")) {
            return CommandKey.ON;
        } else if (commandKeyString.equalsIgnoreCase("-b")) {
            return CommandKey.BY;
        } else {
            return CommandKey.EVERY;
        }
    }

    private static void executeCommandKey(String word, CommandKey commandKey)
            throws Error {
        switch (commandKey) {
        case DESCRIPTION:
            parseDescription(word);
        case FROM:
            parseStartTime(word);
        case TO:
            parseEndTime(word);
        case AT:
            parseLocation(word);
        case ON:
            parseDate(word);
        case BY:
            parseEndTime(word);
        case EVERY:
            parseFrequency(word);
        default:
            throw new Error("Unrecognized command type");
        }
    }

    public static String[] splitUserInput(String userCommand) {
        return userCommand.trim().split("\\s+");
    }

    private static CommandType determineCommandType(String commandTypeString) {
        if (commandTypeString == null) {
            throw new Error("command type string cannot be null!");
        }

        if (commandTypeString.equalsIgnoreCase("add")) {
            return CommandType.ADD;
        } else if (commandTypeString.equalsIgnoreCase("delete")) {
            return CommandType.DELETE;
        } else if (commandTypeString.equalsIgnoreCase("undo")) {
            return CommandType.UNDO;
        } else if (commandTypeString.equalsIgnoreCase("edit")) {
            return CommandType.EDIT;
        } else if (commandTypeString.equalsIgnoreCase("clear")) {
            return CommandType.CLEAR;
        } else {
            return CommandType.LIST;
        }
    }
}
