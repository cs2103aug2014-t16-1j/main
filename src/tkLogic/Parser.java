package tkLogic;

import java.util.ArrayList;
import java.util.Calendar;

import tkLibrary.CommandType;
import tkLibrary.Constants;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLibrary.CommandKey;

//@author A0110493N
/*
 * Parser is a singleton class
 * 
 * It formats a string of command and convert it into a UserInput object
 * containing the CommandType and the Task(s) details
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
    private String priority;
    private String state;

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
        date = new String[0];
        startTimeAndDate = null;
        endTimeAndDate = null;
        priority = Constants.PRIORITY_NULL;
        state = Constants.STATE_NULL;
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
        } else if (commandTypeString.equalsIgnoreCase("set")) {
            return CommandType.SET;
        } else if (commandTypeString.equalsIgnoreCase("redo")) {
            return CommandType.REDO;
        } else if (commandTypeString.equalsIgnoreCase("sync")) {
            return CommandType.SYNC;
        } else if (commandTypeString.equalsIgnoreCase("exit")) {
            return CommandType.EXIT;
        } else {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_COMMAND,
                    commandTypeString));
        }
    }

    private void parseAll(String[] userInputArray) throws Exception {
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

    private void setTaskFields() throws Exception {
        task.setDescription(completeDescription);
        task.setLocation(completeLocation);
        task.setFrequency(frequencyValue);
        task.setFrequencyType(frequencyType);
        task.setStartTime(startTimeAndDate);
        task.setEndTime(endTimeAndDate);
        task.setState(state);
        task.setPriority(priority);
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
        } else if (commandKeyString.equalsIgnoreCase("priority")) {
            return CommandKey.PRIORITY;
        } else if (commandKeyString.equalsIgnoreCase("status")) {
            return CommandKey.STATE;
        } else {
            return null;
        }
    }

    private void executeCmdKey(ArrayList<String> word, CommandKey commandKey)
            throws Exception {
        switch (commandKey) {
            case DESCRIPTION:
                parseDescription(word);
                break;
            case FROM:
                parseStartTime(word);
                break;
            case TO:
                parseEndTime(word);
                break;
            case ON:
                parseDate(word);
                parseTime();
                break;
            case AT:
                parseLocation(word);
                break;
            case EVERY:
                parseFrequency(word);
                break;
            case EDIT:
                changeTaskObject(word);
                break;
            case PRIORITY:
                parsePriority(word);
                break;
            case STATE:
                parseState(word);
                break;
        }
    }

    private void parseDescription(ArrayList<String> description) {
        if (description.size() == 0) {
            completeDescription = null;
        } else {
            completeDescription = description.get(0);
            for (int i = 1; i < description.size(); i++) {
                completeDescription += " " + description.get(i);
            }
            completeDescription = completeDescription.replaceAll("/", "");
        }
    }

    private void parseStartTime(ArrayList<String> time) {
        String[] startingTime = new String[2];
        startTime = updateTime(time, startingTime);
    }

    private void parseEndTime(ArrayList<String> time) {
        String[] endingTime = new String[2];
        endTime = updateTime(time, endingTime);
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
            getMinute(requiredTime, minuteValue);
            getHour(requiredTime, newTime, timeValue);
        }
        return requiredTime;
    }

    private void getMinute(String[] requiredTime, Float minuteValue) {
        int minute;
        minute = Float.valueOf(minuteValue * 100).intValue();
        if (minute < 10) {
            requiredTime[1] = "0" + minute;
        } else {
            requiredTime[1] = "" + minute;
        }
    }

    private void getHour(String[] requiredTime, String newTime, Float timeValue) {
        int hour;
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

    private void parseDate(ArrayList<String> day) {
        date = new String[3];
        if (day.size() == 1) {
            String enteredDate = day.get(0);
            date[0] = enteredDate.substring(0, enteredDate.indexOf("/"));
            enteredDate = enteredDate.replaceFirst(date[0] + "/", "");
            date[1] = enteredDate.substring(0, enteredDate.indexOf("/"));
            enteredDate = enteredDate.replaceFirst(date[1] + "/", "");
            date[1] = determineMonth(date[1]);
            date[2] = enteredDate;
            if (date[2].length() == 2) {
                if (Integer.valueOf(date[2]) > 50) {
                    date[2] = "19" + date[2];
                } else {
                    date[2] = "20" + date[2];
                }
            }
        } else {
            date[0] = day.get(0);
            date[1] = determineMonth(day.get(1));
            date[2] = day.get(2);
        }
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

    private void parseLocation(ArrayList<String> location) {
        completeLocation = location.get(0);
        for (int i = 1; i < location.size(); i++) {
            completeLocation += " " + location.get(i);
        }
        completeLocation = completeLocation.replaceAll("/", "");
    }

    private void parseFrequency(ArrayList<String> frequency) {
        frequencyType = frequency.get(1);
        frequencyValue = Integer.valueOf(frequency.get(0));
    }

    private void changeTaskObject(ArrayList<String> word) throws Exception {
        parseTime();
        setTaskFields();
        resetParser();
        executeCmdKey(word, determineCommandKey("description"));
        isEdit = true;
    }

    private void parseTime() throws Exception {
        if (startTime.length == 0 && endTime.length == 0 && date.length != 0) {
            Calendar.getInstance();
            String[] currentTime = new String[2];
            currentTime[0] = "00";
            currentTime[1] = "00";
            startTime = currentTime;
            startTimeAndDate = getTime(currentTime);
            currentTime = new String[2];
            currentTime[0] = "23";
            currentTime[1] = "59";
            endTime = currentTime;
            endTimeAndDate = getTime(currentTime);
        }
        if (date.length == 0) {
            Calendar calendar = Calendar.getInstance();
            date = new String[3];
            date[0] = "" + calendar.get(5);
            date[1] = determineMonth("" + (calendar.get(2) + 1));
            date[2] = "" + calendar.get(1);
        }
        if (endTime.length != 0 && endTimeAndDate == null) {
            endTimeAndDate = getTime(endTime);
        }
        if (startTime.length != 0 && startTimeAndDate == null) {
            startTimeAndDate = getTime(startTime);
        }

    }

    private String getTime(String[] time) {
        return date[1] + " " + date[0] + " " + date[2] + " " + time[0] + ":"
                + time[1] + ":00";
    }

    private void parsePriority(ArrayList<String> priorityLevel) {
        priority = priorityLevel.get(0);
    }

    private void parseState(ArrayList<String> status) {
        state = status.get(0);
    }
}