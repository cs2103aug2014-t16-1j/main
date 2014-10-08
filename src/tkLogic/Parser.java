package tkLogic;

import java.util.ArrayList;
import java.util.Calendar;

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
    private int[] startTime;
    private int[] endTime;
    private int[] date;

    private void parseDescription(ArrayList<String> description) {
        task.setDescription(description.toString());
    }

    private void parseStartTime(ArrayList<String> time) {
        int[] startingTime = new int[2];
        startTime = updateTime(time, startingTime);
    }

    private void parseEndTime(ArrayList<String> time) {
        int[] endingTime = new int[2];
        endTime = updateTime(time, endingTime);
    }

    private int[] updateTime(ArrayList<String> time, int[] startingTime) {
        for (int i = 0; i < time.get(0).length(); i++) {
            if (time.get(0).substring(i, i + 1) == "a") {
                Float timeValue = Float.valueOf(time.get(0).substring(0, i));
                startingTime[0] = timeValue.intValue();
                Float minuteValue =
                        timeValue - Float.valueOf(startingTime[0] + "");
                startingTime[1] = Float.valueOf(minuteValue * 100).intValue();
            } else if (time.get(0).substring(i, i + 1) == "p") {
                Float timeValue = Float.valueOf(time.get(0).substring(0, i));
                startingTime[0] = timeValue.intValue() + 12;
                Float minuteValue =
                        timeValue - Float.valueOf(startingTime[0] + "");
                startingTime[1] = Float.valueOf(minuteValue * 100).intValue();
            } else if (time.get(0).substring(i, i + 1) == "h") {
                startingTime[0] = Integer.valueOf(time.get(0).substring(0, 2));
                startingTime[0] = Integer.valueOf(time.get(0).substring(2, 4));
            }
        }
        return startingTime;
    }

    private void parseDate(ArrayList<String> day) {
        date = new int[3];
        date[0] = Integer.valueOf(day.get(0));
        date[1] = determineMonth(day.get(1));
        date[2] = Integer.valueOf(day.get(2));
    }

    private void parseTime() {
        
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

        ArrayList<String> word = new ArrayList<String>();
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
                word = new ArrayList<String>();

            } else {
                word.add(newWord);
            }
        }

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
        } else if (commandKeyString.equalsIgnoreCase("-t")
                || commandKeyString.equalsIgnoreCase("-b")) {
            return CommandKey.TO;
        } else if (commandKeyString.equalsIgnoreCase("-@")) {
            return CommandKey.AT;
        } else if (commandKeyString.equalsIgnoreCase("-o")) {
            return CommandKey.ON;
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

    private int determineMonth(String month) throws Error {
        if (month == null) {
            throw new Error("command type string cannot be null!");
        }

        if (month.equalsIgnoreCase("Jan") || Integer.valueOf(month) == 1) {
            return 1;
        } else if (month.equalsIgnoreCase("Feb") || Integer.valueOf(month) == 2) {
            return 2;
        } else if (month.equalsIgnoreCase("Mar") || Integer.valueOf(month) == 3) {
            return 3;
        } else if (month.equalsIgnoreCase("Apr") || Integer.valueOf(month) == 4) {
            return 4;
        } else if (month.equalsIgnoreCase("May") || Integer.valueOf(month) == 5) {
            return 5;
        } else if (month.equalsIgnoreCase("Jun") || Integer.valueOf(month) == 6) {
            return 6;
        } else if (month.equalsIgnoreCase("Jul") || Integer.valueOf(month) == 7) {
            return 7;
        } else if (month.equalsIgnoreCase("Aug") || Integer.valueOf(month) == 8) {
            return 8;
        } else if (month.equalsIgnoreCase("Sep") || Integer.valueOf(month) == 9) {
            return 9;
        } else if (month.equalsIgnoreCase("Oct")
                || Integer.valueOf(month) == 10) {
            return 10;
        } else if (month.equalsIgnoreCase("Nov")
                || Integer.valueOf(month) == 11) {
            return 11;
        } else if (month.equalsIgnoreCase("Dec")
                || Integer.valueOf(month) == 12) {
            return 12;
        } else {
            return FrequencyType.NULL;
        }
    }
}
