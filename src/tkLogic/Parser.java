package tkLogic;

import java.util.ArrayList;

import tkLibrary.CommandType;
import tkLibrary.Constants;
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

    private String completeDescription;
    private String completeLocation;
    private String frequencyType;
    private int frequencyValue;
    private String[] startTime;
    private String[] endTime;
    private String[] date;
    private String startTimeAndDate;
    private String endTimeAndDate;

    private boolean isEdit;

    private Parser() {
    }

    private void resetParser() {
        task = new Task();
        completeDescription = null;
        completeLocation = null;
        frequencyType = null;
        frequencyValue = 0;
        startTime = new String[0];
        endTime = new String[0];
        date = new String[3];
        startTimeAndDate = null;
        endTimeAndDate = null;
        isEdit = false;
    }

    public static Parser getInstance() {
        if (theOneParser == null) {
            theOneParser = new Parser();
        }
        return theOneParser;
    }

    public UserInput format(String userCommand) throws Exception {
        resetParser();
        String[] userInputArray = splitUserInput(userCommand);

        userInput = new UserInput(determineCommandType(userInputArray[0]), task);

        if (userInputArray.length > 1) {
            parseAll(userInputArray);
            if (isEdit) {
                userInput.setEditedTask(task);
            }
        }

        return userInput;
    }

    private String[] splitUserInput(String userCommand) {
        return userCommand.trim().split("\\s+");
    }

    private CommandType determineCommandType(String commandTypeString)
            throws Exception {
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
        parseTime();
        setTaskFields();
    }

    private void setTaskFields() {
        task.setDescription(completeDescription);
        task.setLocation(completeLocation);
        task.setFrequency(frequencyValue);
        task.setFrequencyType(frequencyType);
        task.setStartTime(startTimeAndDate);
        task.setEndTime(endTimeAndDate);
        task.setState(Constants.STATE_PENDING);
    }

    private CommandKey determineCommandKey(String commandKeyString) {
        if (commandKeyString.equalsIgnoreCase("description")) {
            return CommandKey.DESCRIPTION;
        } else if (commandKeyString.equalsIgnoreCase("from")
                || commandKeyString.equalsIgnoreCase("by")) {
            return CommandKey.FROM;
        } else if (commandKeyString.equalsIgnoreCase("to")) {
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
        completeDescription = description.get(0);
        for (int i = 1; i < description.size(); i++) {
            completeDescription += " " + description.get(i);
        }
        return true;
    }

    private boolean parseStartTime(ArrayList<String> time) {
        String[] startingTime = new String[2];
        startTime = updateTime(time, startingTime);
        return true;
    }

    private boolean parseEndTime(ArrayList<String> time) {
        String[] endingTime = new String[2];
        endTime = updateTime(time, endingTime);
        return true;
    }

    private String[] updateTime(ArrayList<String> time, String[] requiredTime) {
        String newTime = time.get(0);
        if (newTime.contains("h")) {
            requiredTime[0] = newTime.substring(0, 2);
            requiredTime[1] = newTime.substring(2, 4);
        } else {

            Float timeValue =
                    Float.valueOf(newTime.substring(0, newTime.length() - 2));
            Float minuteValue = timeValue - Float.valueOf(timeValue.intValue() + "");
            int hour;
            int minute;
            minute = Float.valueOf(minuteValue * 100).intValue();
            if (minute < 10) {
                requiredTime[1] = "0" + minute;
            } else {
                requiredTime[1] = "" + minute;
            }
            if (newTime.contains("a") && timeValue == 12) {
                hour = (timeValue.intValue() - 12);
            } else if (newTime.contains("a")) {
                hour = timeValue.intValue();
            } else if (newTime.contains("p") && timeValue != 12) {
                hour = (timeValue.intValue() + 12);
            } else {
                hour = timeValue.intValue();
            }
            if (hour < 10) {
                requiredTime[0] = "0" + hour;
            } else {
                requiredTime[0] = "" + hour;
            }
        }
        return requiredTime;
    }

    private boolean parseDate(ArrayList<String> day) {
        date[0] = day.get(0);
        date[1] = determineMonth(day.get(1));
        date[2] = day.get(2);
        return true;
    }

    private String determineMonth(String month) throws Error {
        if (month.length() == 3) {
            return month;
        } else if (Integer.valueOf(month) - 1 == 0) {
            return "Jan";
        } else if (Integer.valueOf(month) - 1 == 1) {
            return "Feb";
        } else if (Integer.valueOf(month) - 1 == 2) {
            return "Mar";
        } else if (Integer.valueOf(month) - 1 == 3) {
            return "Apr";
        } else if (Integer.valueOf(month) - 1 == 4) {
            return "May";
        } else if (Integer.valueOf(month) - 1 == 5) {
            return "Jun";
        } else if (Integer.valueOf(month) - 1 == 6) {
            return "Jul";
        } else if (Integer.valueOf(month) - 1 == 7) {
            return "Aug";
        } else if (Integer.valueOf(month) - 1 == 8) {
            return "Sep";
        } else if (Integer.valueOf(month) - 1 == 9) {
            return "Oct";
        } else if (Integer.valueOf(month) - 1 == 10) {
            return "Nov";
        } else {
            return "Dec";
        }
    }

    private boolean parseLocation(ArrayList<String> location) {
        completeLocation = location.get(0);
        for (int i = 1; i < location.size(); i++) {
            completeLocation += " " + location.get(i);
        }
        return true;
    }

    private boolean parseFrequency(ArrayList<String> frequency) {
        frequencyType = frequency.get(1);
        frequencyValue = Integer.valueOf(frequency.get(0));
        return true;
    }

    private boolean changeTaskObject(ArrayList<String> word) {
        parseTime();
        setTaskFields();
        resetParser();
        executeCmdKey(word, determineCommandKey("description"));
        isEdit = true;
        return true;
    }

    private void parseTime() {
        if (startTime.length != 0) {
            startTimeAndDate =
                    date[1] + " " + date[0] + " " + date[2] + " " + startTime[0]
                            + ":" + startTime[1] + ":00";
        }
        if (endTime.length != 0) {
            endTimeAndDate =
                    date[1] + " " + date[0] + " " + date[2] + " " + endTime[0] + ":"
                            + endTime[1] + ":00";
        }
    }
}