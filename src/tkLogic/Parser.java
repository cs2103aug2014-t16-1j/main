package tkLogic;

import java.util.ArrayList;
import java.util.Calendar;

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
    private String priority;
    private String category;

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
        priority = null;
        category = null;
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
        } else {
            throw new Exception("invalid command: " + commandTypeString);
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

    private void setTaskFields() {
        task.setDescription(completeDescription);
        task.setLocation(completeLocation);
        task.setFrequency(frequencyValue);
        task.setFrequencyType(frequencyType);
        task.setStartTime(startTimeAndDate);
        task.setEndTime(endTimeAndDate);
        task.setState(Constants.STATE_PENDING);
        task.setPriority(priority);
        task.setCategory(category);
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
        } else if (commandKeyString.equalsIgnoreCase("category")) {
            return CommandKey.CATEGORY;
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
            case CATEGORY:
                parseCategory(word);
                break;
        }
    }

    private void parseDescription(ArrayList<String> description) {
        completeDescription = description.get(0);
        for (int i = 1; i < description.size(); i++) {
            completeDescription += " " + description.get(i);
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
        date[0] = day.get(0);
        date[1] = determineMonth(day.get(1));
        date[2] = day.get(2);
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
        if (startTime.length == 0 && endTimeAndDate != null) {
            throw new Exception(
                    "invalid time format, you have not entered the start time.");
        } else if (startTime.length == 0 && endTime.length == 0 && date.length != 0) {
            Calendar.getInstance();
            String[] currentTime = new String[2];
            currentTime[0] = "" + Calendar.HOUR;
            currentTime[1] = "" + Calendar.MINUTE;
            startTimeAndDate = getTime(startTime);
        }
        if (date.length == 0) {
            Calendar.getInstance();
            date = new String[3];
            date[0] = "" + Calendar.DATE;
            date[1] = determineMonth("" + Calendar.MONTH);
            date[2] = "" + Calendar.YEAR;
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

    private void parseCategory(ArrayList<String> category) {
        this.category = category.get(0);
    }
}