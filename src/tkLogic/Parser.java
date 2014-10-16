package tkLogic;

import java.util.ArrayList;
import java.util.Calendar;

import tkLibrary.CommandType;
import tkLibrary.Constants;
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
    private static Parser theOneParser;
    private UserInput userInput;
    private Task task;
    private int[] startTime;
    private int[] endTime;
    private int[] date;
    private boolean isEdit;

    private Parser() {
        task = new Task();
        startTime = new int[0];
        endTime = new int[0];
        date = new int[3];
        isEdit = false;
    }

    public static Parser getInstance() {
        if (theOneParser == null) {
            theOneParser = new Parser();
        }
        return theOneParser;
    }

    public UserInput format(String userCommand) throws Exception {
        String[] userInputArray = splitUserInput(userCommand);
        
        userInput = new UserInput(determineCommandType(userInputArray[0]), task);

        if (userInputArray.length > 1) {
            parseAll(userInputArray);
            parseTime();
            task.setState(StateType.PENDING);
            if (isEdit) {
                userInput.setEditedTask(task);
            }
        }
        task = new Task();
        startTime = new int[0];
        endTime = new int[0];
        date = new int[3];
        isEdit = false;
        return userInput;
    }

    private String[] splitUserInput(String userCommand) {
        return userCommand.trim().split("\\s+");
    }

    private CommandType determineCommandType(String commandTypeString) throws Exception {
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
        } else if (commandTypeString.equalsIgnoreCase("search")) {
        	return CommandType.SEARCH;
        } else if (commandTypeString.equalsIgnoreCase("list")) {
            return CommandType.LIST;
        } else {
            throw new Exception("invalid command: " + commandTypeString);
        }
    }

    private void parseAll(String[] userInputArray) {
        ArrayList<String> word = new ArrayList<String>();
        String newWord;
        CommandKey newCommandKey;
        CommandKey commandKey = determineCommandKey("description");
        for (int i = 1; i < userInputArray.length; i++) {
            newWord = userInputArray[i];
            newCommandKey = determineCommandKey(newWord);
            if (newCommandKey != null) {
                executeCmdKey(word, commandKey);
                commandKey = determineCommandKey(newWord);
                word = new ArrayList<String>();
            } else {
                word.add(newWord);
            }
        }
        executeCmdKey(word, commandKey);
    }

    private CommandKey determineCommandKey(String commandKeyString) {
        if (commandKeyString.equalsIgnoreCase("description")) {
            return CommandKey.DESCRIPTION;
        } else if (commandKeyString.equalsIgnoreCase("from")) {
            return CommandKey.FROM;
        } else if (commandKeyString.equalsIgnoreCase("to")
                || commandKeyString.equalsIgnoreCase("by")) {
            return CommandKey.TO;
        } else if (commandKeyString.equalsIgnoreCase("at")) {
            return CommandKey.AT;
        } else if (commandKeyString.equalsIgnoreCase("on")) {
            return CommandKey.ON;
        } else if (commandKeyString.equalsIgnoreCase("correct")) {
            return CommandKey.EDIT;
        } else if (commandKeyString.equalsIgnoreCase("every")) {
            return CommandKey.EVERY;
        } else {
            return null;
        }
    }

    private boolean executeCmdKey(ArrayList<String> word, CommandKey commandKey)
            throws Error {
        switch (commandKey) {
            case DESCRIPTION:
                return parseDescription(word);
            case FROM:
                return parseStartTime(word);
            case TO:
                return parseEndTime(word);
            case ON:
                return parseDate(word);
            case AT:
                return parseLocation(word);
            case EVERY:
                return parseFrequency(word);
            case EDIT:
                return changeTaskObject(word);
            default:
                throw new Error("Unrecognized command key: " + commandKey);
        }
    }

    private boolean parseDescription(ArrayList<String> description) {
        String completeDescription = description.get(0);
        if (description.size() > 1) {
            for (int i = 1; i < description.size(); i++) {
                completeDescription += " " + description.get(i);
            }
        }
        task.setDescription(completeDescription);
        return true;
    }

    private boolean parseStartTime(ArrayList<String> time) {
        int[] startingTime = new int[2];
        startTime = updateTime(time, startingTime);
        return true;
    }

    private boolean parseEndTime(ArrayList<String> time) {
        int[] endingTime = new int[2];
        endTime = updateTime(time, endingTime);
        return true;
    }

    private int[] updateTime(ArrayList<String> time, int[] requiredTime) {
        String newTime = time.get(0);
        if (newTime.contains("h")) {
            requiredTime[0] = Integer.valueOf(newTime.substring(0, 2));
            requiredTime[0] = Integer.valueOf(newTime.substring(2, 4));
        } else {
            Float timeValue =
                    Float.valueOf(newTime.substring(0, newTime.length() - 2));
            Float minuteValue = timeValue - Float.valueOf(timeValue.intValue() + "");
            requiredTime[1] = Float.valueOf(minuteValue * 100).intValue();
            if (newTime.contains("a")) {
                requiredTime[0] = timeValue.intValue();
            } else if (newTime.contains("a") && timeValue == 12) {
                requiredTime[0] = timeValue.intValue() - 12;
            } else if (newTime.contains("p") && timeValue != 12) {
                requiredTime[0] = timeValue.intValue() + 12;
            } else {
                requiredTime[0] = timeValue.intValue();
            }
        }
        return requiredTime;
    }

    private boolean parseDate(ArrayList<String> day) {
        date[0] = Integer.valueOf(day.get(0));
        date[1] = determineMonth(day.get(1));
        date[2] = Integer.valueOf(day.get(2));
        return true;
    }

    private int determineMonth(String month) throws Error {
        if (month.length() != 3) {
            return Integer.valueOf(month) - 1;
        } else if (month.equalsIgnoreCase("Jan")) {
            return 0;
        } else if (month.equalsIgnoreCase("Feb")) {
            return 1;
        } else if (month.equalsIgnoreCase("Mar")) {
            return 2;
        } else if (month.equalsIgnoreCase("Apr")) {
            return 3;
        } else if (month.equalsIgnoreCase("May")) {
            return 4;
        } else if (month.equalsIgnoreCase("Jun")) {
            return 5;
        } else if (month.equalsIgnoreCase("Jul")) {
            return 6;
        } else if (month.equalsIgnoreCase("Aug")) {
            return 7;
        } else if (month.equalsIgnoreCase("Sep")) {
            return 8;
        } else if (month.equalsIgnoreCase("Oct")) {
            return 9;
        } else if (month.equalsIgnoreCase("Nov")) {
            return 10;
        } else {
            return 11;
        }
    }

    private boolean parseLocation(ArrayList<String> location) {
        String completeLocation = location.get(0);
        if (location.size() > 1) {
            for (int i = 1; i < location.size(); i++) {
                completeLocation += " " + location.get(i);
            }
        }
        task.setLocation(completeLocation);
        return true;
    }

    private boolean parseFrequency(ArrayList<String> frequency) {
        FrequencyType frequencyType = determineFrequencyType(frequency.get(1));
        task.setFrequency(Integer.valueOf(frequency.get(0)), frequencyType);
        return true;
    }

    private FrequencyType determineFrequencyType(String frequency) {
        if (frequency.equalsIgnoreCase(Constants.FREQUENCY_DAY)) {
            return FrequencyType.DAY;
        } else if (frequency.equalsIgnoreCase(Constants.FREQUENCY_WEEK)) {
            return FrequencyType.WEEK;
        } else if (frequency.equalsIgnoreCase(Constants.FREQUENCY_MONTH)) {
            return FrequencyType.MONTH;
        } else if (frequency.equalsIgnoreCase(Constants.FREQUENCY_YEAR)) {
            return FrequencyType.YEAR;
        } else {
            return null;
        }
    }

    private boolean changeTaskObject(ArrayList<String> word) {
        parseTime();
        task.setState(StateType.PENDING);
        task = new Task();
        executeCmdKey(word, determineCommandKey("description"));
        isEdit = true;
        return true;
    }

    private void parseTime() {
        if (startTime.length != 0) {
            Calendar startingTime = Calendar.getInstance();
            startingTime.set(date[2], date[1], date[0], startTime[0], startTime[1], 0);
            task.setStartTime(startingTime);
        }
        if (endTime.length != 0) {
            Calendar endingTime = Calendar.getInstance();
            endingTime.set(date[2], date[1], date[0], endTime[0], endTime[1], 0);
            task.setEndTime(endingTime);
        }
    }
}