package theUI;

import java.util.ArrayList;
import java.util.Calendar;
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
    private ArrayList<Task> tasksOnScreen;
    
    public UserInterface(String fileName) {
    	parser = Parser.getInstance();
        logic = new Logic(fileName);
        gui = new Gui();
        tasksOnScreen = new ArrayList<Task> ();
    }
    
    public void run() {
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
    
    public String getDisplayedMessage() {
    	return gui.displayText;
    }

    public void executeCommands(String userCommand) {
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
                e.printStackTrace();
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
        } else if (command == CommandType.SET){
        	set(task);
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
        	task.setState(StateType.PENDING);
        	if (task.getPriorityLevel() == null) {
        		task.setPriority(PriorityType.MEDIUM);
        	}
        	
            String feedback = logic.add(task);
            if (!feedback.equals(Constants.MESSAGE_DUPLICATED_TASK)) {
                if (feedback.equals(Constants.MESSAGE_TASK_ADDED)) {
                	gui.displayDone(feedback, false);
                } else {
                	gui.displayWarning(feedback, false);
                }
                
            	Task newTask = new Task();
                newTask.setStartTime(task.getStartTime());
                if (newTask.getStartTime() == null) {
                	newTask.setDescription("floating");
                }
                showToUser(logic.list(newTask), true);
            } else {
                gui.displayWarning(feedback, false);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void list(Task task) {
        try {
            String cmdInfo = task.getDescription();
            if (cmdInfo != null) {
                cmdInfo = cmdInfo.toLowerCase();
            }

            if (task.getState() == null) {
            	task.setState(StateType.PENDING);
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
            
            ArrayList<Task> result = logic.list(task);
            if (result.size() == 0) {
                gui.displayDone(MESSAGE_NO_RESULT, false);
            } else {
            	showToUser(result, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void delete(Task task) {
        if (task.getDescription() == null) {
            gui.displayWarning(MESSAGE_NO_DELETE_INFO, false);
            return;
        }
        try {
        	ArrayList<Task> list = new ArrayList<Task> ();
        	
	        if (isInteger(task.getDescription())) {
	        	int noOfTask = toInteger(task.getDescription());
	        	if (noOfTask <= tasksOnScreen.size()) {
	        		String feedback = logic.delete(tasksOnScreen.get(noOfTask - 1));
	        		if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
	        			list.add(tasksOnScreen.get(noOfTask - 1));
	        			gui.displayDone(feedback, false);
	        			showToUser(list, true);
	        		} else {
	        			gui.displayWarning(feedback, false);
	        		}
	        		return;
	        	}
	        }
        
            list = logic.search(task.getDescription());
            
            if (list.isEmpty()) { 
            	gui.displayWarning(Constants.MESSAGE_TASK_DOES_NOT_EXIST, false);
                
            } else if (list.size() == 1) {
            	logic.delete(list.get(0));
            	gui.displayDone(Constants.MESSAGE_TASK_DELETED, false);
            	showToUser(list, true);
            } else {
            	gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND, false);
            	showToUser(list, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void edit(Task taskToBeEdited, Task newTask) {
        try {
            String feedback = logic.edit(taskToBeEdited, newTask);
            if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
            	ArrayList<Task> list = new ArrayList<Task> ();
            	list.add(newTask);
                gui.displayDone(feedback, false);
                showToUser(list, true);
            } else {
                gui.displayWarning(feedback, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private void undo() {
        try {
            String feedback = logic.undo();
            gui.displayDone(feedback, false);
        } catch (Exception e) {
            e.printStackTrace();
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
            	showToUser(result, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void set(Task task) {
    	try {
    		ArrayList<Task> list = new ArrayList<Task> ();
        
	        if (isInteger(task.getDescription())) {
	        	int noOfTask = toInteger(task.getDescription());
	        	if (noOfTask <= tasksOnScreen.size()) {
        			Task newTask = tasksOnScreen.get(noOfTask - 1);
        			if (task.getPriorityLevel() != null) {
        				newTask.setPriority(task.getPriorityLevel());
        			}
        			if (task.getState() != null) {
        				newTask.setState(task.getState());
        			}
	        		String feedback = logic.set(tasksOnScreen.get(noOfTask - 1));
	        		
	        		if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
	        			list.add(newTask);
	        			gui.displayDone(feedback, false);
	        			showToUser(list, true);
	        		} else {
	        			gui.displayWarning(feedback, false);
	        		}
	        		return;
	        	}
	        }
    		
    		list = logic.search(task.getDescription());
            if (list.size() == 0) {
                gui.displayDone(MESSAGE_NO_RESULT, false);
            } else if (list.size() == 1) {
            	Task newTask = list.get(0);
    			if (task.getPriorityLevel() != null) {
    				newTask.setPriority(task.getPriorityLevel());
    			}
    			if (task.getState() != null) {
    				newTask.setState(task.getState());
    			}
    			
            	logic.set(newTask);
            	gui.displayDone("Task was set:", false);
                showToUser(list, true);
            } else {
            	gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND, false);
            	showToUser(list, true);
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void showToUser(ArrayList<Task> list, boolean isAppended) {
		tasksOnScreen = new ArrayList<Task> (list);
		gui.display(list, isAppended);
	}

    public boolean isInteger(String input) {
        try {
            Integer.parseInt( input );
            return true;
        } catch( Exception e ) {
            return false;
        }
    }
    
    public int toInteger(String input ) {
        try {
            return Integer.parseInt( input );
        } catch( Exception e ) {
            return 0;
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
