package theUI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import tkLibrary.CommandType;
import tkLibrary.Constants;
import tkLibrary.PriorityType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Logic;
import tkLogic.Parser;

public class UserInterface {
    final int LISTUPCOMINGTIME_DEFAULT = 5;
    final String MESSAGE_NO_RESULT = "There is no such task.";
    final String MESSAGE_NO_SEARCH_INFO = "Please specify the keyword!";
    final String MESSAGE_NO_ADD_INFO = "Please specify the task you want to add!";
    final String MESSAGE_NO_DELETE_INFO = "Please specify the task you want to delete!";
    
    private String NO_COMMAND = "";
    private Logic logic;
    private Parser parser;
    private Gui gui;
    private Logger logger;
    
    public void run(String fileName) {
        parser = Parser.getInstance();
        logic = new Logic(fileName);
        gui = new Gui();
        logger = Logger.getLogger("log" + fileName); 
        
        while (true) {
            try {
                Thread.sleep(50);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            String userCommand = gui.getUserCommand();
            if (!userCommand.equals(NO_COMMAND)) {
                try {
                    executeCommands(userCommand);
                    gui.setUserCommand(NO_COMMAND);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void executeCommands(String userCommand) {
        UserInput userInput; 
        CommandType command;
        Task task;
        
        try {
            userInput = parser.format(userCommand);
            command = userInput.getCommand();
            task = userInput.getTask();
        } catch (Exception e) {
            if (e.getMessage().contains("invalid command")) {
                gui.displayWarning("Invalid command: " + userCommand, false);
            } else {
                System.out.println(e.getMessage());
            }
            return;
        }
        
        if (command == CommandType.ADD) {
            add(task);
        } else if (command == CommandType.DELETE) {
            delete(task);
        } else if (command == CommandType.UNDO) {
            undo();
        } else if (command == CommandType.EDIT) {
            Task newTask = userInput.getEditedTask();
            edit(task, newTask);
        } else if (command == CommandType.CLEAR) {
            clear();
        } else if (command == CommandType.LIST) {
            list(task);
        } else if (command == CommandType.SEARCH) {
            search(task);
        } else {
            gui.displayWarning("Informat command", false);
        }
    }
    
    private void add(Task task) {
        if (task.getDescription() == null) {
            gui.displayWarning(MESSAGE_NO_ADD_INFO, false);
            return;
        }
        
        try {
            String feedback = logic.add(task);
            if (feedback.equals(Constants.MESSAGE_TASK_ADDED)) {
                gui.displayDone(feedback, false);
                Task newTask = new Task();
                newTask.setStartTime(task.getStartTime());
                gui.display(logic.list(newTask), true);
            } else {
                gui.displayWarning(feedback, false);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            logger.log(Level.WARNING, "processing error", e);
        }
    }

    private void list(Task task) {
        try {
            String cmdInfo = task.getDescription();
            if (cmdInfo != null) {
                cmdInfo = cmdInfo.toLowerCase();
            }
            
            if (cmdInfo != null && cmdInfo.contains("completed")) {
                task.setState(StateType.DONE);
            }
            
            if (cmdInfo != null && cmdInfo.contains("discarded")) {
                task.setState(StateType.GIVEUP);
            }
            
            if (cmdInfo != null && cmdInfo.contains("upcoming") && task.getStartTime() == null) {
                int time = LISTUPCOMINGTIME_DEFAULT;
                String s = getFirstInt(cmdInfo);
                if (!s.equals("")) {
                    time = Integer.parseInt(s);
                }
                
                task.setStartTime(Calendar.getInstance());
                
                Calendar endTime = Calendar.getInstance();
                endTime.add(Calendar.DAY_OF_MONTH, time);
                task.setEndTime(endTime);
            }
            
            if (cmdInfo != null && cmdInfo.contains("priority")) {
                if (cmdInfo.contains("high")) {
                    task.setPriority(PriorityType.HIGH);
                } else if (cmdInfo.contains("medium")) {
                    task.setPriority(PriorityType.MEDIUM);
                } else if (cmdInfo.contains("low")) {
                    task.setPriority(PriorityType.LOW);
                }  
            }
            
            ArrayList<Task> result = logic.list(task);
                
            if (result.size() == 0) {
                gui.displayDone(MESSAGE_NO_RESULT, false);
            } else {
                gui.display(result, false);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.log(Level.WARNING, "processing error", e);
        }
    }
    
    private void delete(Task task) {
        if (task.getDescription() == null) {
            gui.displayWarning(MESSAGE_NO_DELETE_INFO, false);
            return;
        }
        
        try {
            String feedback = logic.delete(task);       
            
            assert feedback.equals(Constants.MESSAGE_TASK_DELETED) 
                || feedback.equals(Constants.MESSAGE_TASK_DOES_NOT_EXIST);
    
            if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
                gui.displayDone(feedback, false);
            } else {
                gui.displayWarning(feedback, false);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.log(Level.WARNING, "processing error", e);
        }
    }
    
    private void edit(Task taskToBeEdited, Task newTask) {
        try {
            String feedback = logic.edit(taskToBeEdited, newTask);
            if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
                gui.displayDone(feedback, false);
            } else {
                gui.displayWarning(feedback, false);
            }
            //gui.display(logic.search(newTask), false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.log(Level.WARNING, "processing error", e);
        }
    }

    private void clear() { 
        try {
            String feedback = logic.clear();
            if (feedback.equals(Constants.MESSAGE_TASK_CLEARED)) {
                gui.displayDone(feedback, false);
            } else {
                gui.displayWarning(feedback, false);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.log(Level.WARNING, "processing error", e);
        }
    }

    private void undo() {
        try {
            String feedback = logic.undo();
            gui.displayDone(feedback, false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.log(Level.WARNING, "processing error", e);
        }
    }
    
    private void search(Task task) {
        if (task.getDescription() == null) {
            gui.displayWarning(MESSAGE_NO_SEARCH_INFO, false);
            return;
        }
        try {
            ArrayList<Task> result = logic.search(task.getDescription());
            if (result.size() == 0) {
                gui.displayDone(MESSAGE_NO_RESULT, false);
            } else {
                gui.display(result, false);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.log(Level.WARNING, "processing error", e);
        }
    }
    
    private String getFirstInt(String s) {
        String result = "";
        for(int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                for (int j = i; j < s.length() && Character.isDigit(s.charAt(j)); j ++) {
                    result = result + s.charAt(j);
                }
                break;
            }
        }
        return result;
    }
}
