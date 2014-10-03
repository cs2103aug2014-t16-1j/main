package tkLogic;

import java.util.Date;

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
        UserInput userInput = new UserInput(userInputArray[0], task);

        String word;
        String command = "description";
        for (int i = 0; i < userInputArray.length; i++) {
            word = userInputArray[i];
            if (word.substring(0, 1) == "-") {

                // do something about the previous description
                // store the command type instead of command string?
                // -f from -t to -@ -o on -b by -e every

                CommandKey commandKey = determineCommandType(word);

                switch (commandKey) {
                case FROM:
                    return add(userCommand);
                case TO:
                    return display();
                case AT:
                    return delete(userCommand);
                case ON:
                    return clear();
                case BY:
                    return sort();
                case EVERY:
                    return search(userCommand);
                default:
                    throw new Error("Unrecognized command type");
                }
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

    public static String[] splitUserInput(String userCommand) {
        return userCommand.trim().split("\\s+");
    }

    private static CommandType determineCommandType(String commandTypeString) {
        if (commandTypeString == null) {
            throw new Error("command type string cannot be null!");
        }

        if (commandTypeString.equalsIgnoreCase("-a")) {
            return CommandType.ADD;
        } else if (commandTypeString.equalsIgnoreCase("-d")) {
            return CommandType.DELETE;
        } else if (commandTypeString.equalsIgnoreCase("-u")) {
            return CommandType.UNDO;
        } else if (commandTypeString.equalsIgnoreCase("-e")) {
            return CommandType.EDIT;
        } else if (commandTypeString.equalsIgnoreCase("-c")) {
            return CommandType.CLEAR;
        } else {
            return CommandType.LIST;
        }
    }
}
