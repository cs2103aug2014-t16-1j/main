package tkLogic;

import java.util.ArrayList;
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

    private UserInput userInput;
    private Task task;

    private void parseDescription(ArrayList<String> description) {
        task.setDescription(description.toString());
    }

    private Date parseStartTime(String userCommand) {
        return null;
    }

    private Date parseEndTime(String userCommand) {
        return null;
    }

    private Date parseDate(String userCommand) {
        return null;
    }

    private void parseLocation(ArrayList<String> location) {
        task.setLocation(location.toString());
    }

    private void parseFrequency(ArrayList<String> frequency) {
        FrequencyType frequencyType = determineFrequencyType(frequency.get(1));
        task.setFrequency(Integer.valueOf(frequency.get(0)), frequencyType);
    }

    public Parser() {
        task = new Task();
    }

    public UserInput format(String userCommand) {
        String[] userInputArray = splitUserInput(userCommand);
        userInput =
                new UserInput(determineCommandType(userInputArray[0]), task);

        ArrayList<String> word;
        String newWord;
        CommandKey commandKey = determineCommandKey("-d");
        for (int i = 0; i < userInputArray.length; i++) {
            newWord = userInputArray[i];
            if (newWord.substring(0, 1) == "-") {

                executeCommandKey(word, commandKey);

                // do something about the previous description
                // store the command type instead of command string?
                // -f from -t to -@ -o on -b by -e every

                commandKey = determineCommandKey(newWord);

            } else {
                word.add(newWord);
            }
        }

        task.setStartTime(parseStartTime(userCommand));
        task.setEndTime(parseEndTime(userCommand));
        task.setState(StateType.PENDING);

        return userInput;
    }

    private CommandKey determineCommandKey(String commandKeyString)
            throws Error {
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

    private void
            executeCommandKey(ArrayList<String> word, CommandKey commandKey)
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

    public String[] splitUserInput(String userCommand) {
        return userCommand.trim().split("\\s+");
    }

    private CommandType determineCommandType(String commandTypeString) {
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

    private FrequencyType determineFrequencyType(String frequency) throws Error {
        if (frequency == null) {
            throw new Error("command type string cannot be null!");
        }

        if (frequency.equalsIgnoreCase("day")) {
            return FrequencyType.DAY;
        } else if (frequency.equalsIgnoreCase("week")) {
            return FrequencyType.WEEK;
        } else if (frequency.equalsIgnoreCase("month")) {
            return FrequencyType.MONTH;
        } else if (frequency.equalsIgnoreCase("year")) {
            return FrequencyType.YEAR;
        } else {
            return FrequencyType.NULL;
        }
    }
}
