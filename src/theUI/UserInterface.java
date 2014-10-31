package theUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

import tkLibrary.CommandType;
import tkLibrary.Constants;
import tkLibrary.PriorityType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Logic;
import tkLogic.Parser;

public class UserInterface {

    private String NO_COMMAND = "";
    private Logic logic;
    private Parser parser;
    private Gui gui;
    private ArrayList<Task> tasksOnScreen;
    private Stack<String> statusForUndo;
    private Stack<Task> tasksForUndo;
    
    public UserInterface(String fileName) {
    	parser = Parser.getInstance();
        logic = new Logic(fileName);
        gui = new Gui();
        tasksOnScreen = new ArrayList<Task> ();
        statusForUndo = new Stack<String> ();
        tasksForUndo = new Stack<Task> ();
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
        } else if (command == CommandType.BLOCK) {
        	block(task);
        } else if (command == CommandType.EXIT) {
        	gui.displayDone("Good Bye !!!", false);
        	 try {
                 Thread.sleep(1000);
             } catch(InterruptedException e) {
                 Thread.currentThread().interrupt();
             }
        	System.exit(0);
        } else {
            gui.displayWarning("Informat command", false);
        }
    }
    
    private void add(Task task) {
        if (task.getDescription() == null) {
            gui.displayWarning(Constants.MESSAGE_NO_ADD_INFO, false);
            return;
        }
        
        if (task.getStartTime() != null && task.getEndTime() != null) {
        	if (task.getStartTime().compareTo(task.getEndTime()) > 0) {
        		gui.displayWarning(Constants.MESSAGE_ENDTIME_BEFORE_STARTTIME, false);
        		gui.displayWarning("<br>", true);
        		gui.displayWarning("Start time: " + convertCalendarToString(task.getStartTime(), Constants.FORMAT_DATE_DATE_AND_HOUR), true);
        		gui.displayWarning("End time  : " + convertCalendarToString(task.getEndTime(), Constants.FORMAT_DATE_DATE_AND_HOUR), true);
        		return;
        	}
        }
        
        try {
        	if (task.getState() == null) {
        		task.setState(StateType.PENDING);
        	}
        	if (task.getPriorityLevel() == null) {
        		task.setPriority(PriorityType.MEDIUM);
        	}
        	
            String feedback = logic.add(task);
            if (feedback.equals(Constants.MESSAGE_TASK_ADDED)) {
            	gui.displayDone(feedback, false);
            	addToStackForUndo(task, "Task deleted.");
            } else {
            	gui.displayWarning(feedback, false);
            }
            
            Task newTask = new Task();
            newTask.setStartTime(task.getStartTime());
            if (newTask.getStartTime() == null) {
            	newTask.setDescription("floating");
            }
            showToUser(logic.list(newTask), task, Constants.HIGHLIGH, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void block(Task task) {
    	
    }

    private void list(Task task) {
        try {
            String cmdInfo = task.getDescription();
            if (cmdInfo != null) {
                cmdInfo = cmdInfo.toLowerCase();
            }

            if (task.getState() == null && task.getStartTime() == null) {
            	task.setState(StateType.PENDING);
            }

            if (cmdInfo != null && cmdInfo.contains("upcoming") && task.getStartTime() == null) {
                int time = Constants.LISTUPCOMINGTIME_DEFAULT;
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
                gui.displayDone(Constants.MESSAGE_NO_RESULT_LIST, false);
            } else {
            	showToUser(result, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void delete(Task task) {
        if (task.getDescription() == null) {
            gui.displayWarning(Constants.MESSAGE_NO_DELETE_INFO, false);
            return;
        }
        try {
        	ArrayList<Task> list = new ArrayList<Task> ();
        	
	        if (isInteger(task.getDescription())) {
	        	int noOfTask = toInteger(task.getDescription());
	        	if (noOfTask <= tasksOnScreen.size()) {
	        		String feedback = logic.delete(tasksOnScreen.get(noOfTask - 1));
	        		if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
	        			addToStackForUndo(tasksOnScreen.get(noOfTask - 1), "Task restored.");
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
            	addToStackForUndo(list.get(0), "Task restored.");
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
    
    private void edit(Task task, Task updatedTask) {
        try {
    		ArrayList<Task> list = new ArrayList<Task> ();
        
	        if (isInteger(task.getDescription())) {
	        	int noOfTask = toInteger(task.getDescription());
	        	if (noOfTask <= tasksOnScreen.size()) {
	        		Task newTask = new Task(tasksOnScreen.get(noOfTask - 1));
	        		String feedback = logic.edit(newTask, updatedTask);
	        		addToStackForUndo(newTask, "Task edited.");
	        		newTask.update(updatedTask);
	        		
	        		displayFeedbackForEditing(list, newTask, feedback);
	        		return;
	        	}
	        }
    		
    		list = logic.search(task.getDescription());
            if (list.size() == 0) {
                gui.displayDone(Constants.MESSAGE_TASK_DOES_NOT_EXIST, false);
            } else if (list.size() == 1) {
            	Task newTask = new Task(list.get(0));
            	String feedback = logic.edit(newTask, updatedTask);
            	addToStackForUndo(newTask, "Task edited.");
        		newTask.update(updatedTask);
        		
        		list.clear();
        		displayFeedbackForEditing(list, newTask, feedback);
            } else {
            	gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND, false);
            	showToUser(list, true);
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	private void displayFeedbackForEditing(ArrayList<Task> list, Task newTask,
			String feedback) {
		if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
			list.add(newTask);
			gui.displayDone(feedback, false);
			showToUser(list, true);
		} else {
			gui.displayWarning(feedback, false);
			list.add(newTask);
			gui.displayDone(feedback, false);
			showToUser(list, true);
		}
	}

    private void clear() { 
        try {
            String feedback = logic.clear();
            if (feedback.equals(Constants.MESSAGE_TASK_CLEARED)) {
                gui.displayDone(feedback, false);
                addToStackForUndo(null, "All tasks restored.");
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
            if (feedback.equals(Constants.MESSAGE_UNDO_DONE)) {
            	gui.displayDone(statusForUndo.pop(), false);
            	Task task = tasksForUndo.pop();
            	if (task != null) {
            		ArrayList<Task> list = new ArrayList<Task> ();
            		list.add(task);
            		gui.display(list, -1, Constants.NO_EFFECT, true);
            	}
            	
            } else {
            	gui.displayWarning(feedback, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addToStackForUndo(Task task, String message) {
    	statusForUndo.push(message);
    	if (task == null) {
    		tasksForUndo.push(null);
    	} else {
    			tasksForUndo.push(new Task(task));
    	}
    }
    
    private void search(Task task) {
        if (task.getDescription() == null) {
            gui.displayWarning(Constants.MESSAGE_NO_SEARCH_INFO, false);
            return;
        }
        try {
            ArrayList<Task> result = logic.search(task.getDescription());
            if (result.size() == 0) {
                gui.displayDone(Constants.MESSAGE_NO_RESULT, false);
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
        			Task oldTask = new Task(newTask);
        			updateNewPriorityAndState(task, newTask);
        			String feedback = logic.set(tasksOnScreen.get(noOfTask - 1));
	        		if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
	        			list.add(newTask);
	        			gui.displayDone(String.format(Constants.MESSAGE_UPDATE_STATUS_AND_PRIORITY, newTask.getState(), newTask.getPriorityLevel()), false);
	        			showToUser(list, true);
	        			addToStackForUndo(oldTask, String.format(Constants.MESSAGE_UPDATE_STATUS_AND_PRIORITY, oldTask.getState(), oldTask.getPriorityLevel()));
	        		} else {
	        			gui.displayWarning(feedback, false);
	        		}
	        		return;
	        	}
	        }
    		
    		list = logic.search(task.getDescription());
            if (list.size() == 0) {
                gui.displayDone(Constants.MESSAGE_NO_RESULT, false);
            } else if (list.size() == 1) {
            	Task newTask = list.get(0);
            	Task oldTask = new Task(newTask);
    			updateNewPriorityAndState(task, newTask);
    			
            	logic.set(newTask);
            	gui.displayDone(String.format(Constants.MESSAGE_UPDATE_STATUS_AND_PRIORITY, newTask.getState(), newTask.getPriorityLevel()), false);
                showToUser(list, true);
                addToStackForUndo(oldTask, String.format(Constants.MESSAGE_UPDATE_STATUS_AND_PRIORITY, oldTask.getState(), oldTask.getPriorityLevel()));
            } else {
            	gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND, false);
            	showToUser(list, true);
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	private void updateNewPriorityAndState(Task task, Task newTask) {
		if (task.getPriorityLevel() != null) {
			newTask.setPriority(task.getPriorityLevel());
		}
		if (task.getState() != null) {
			newTask.setState(task.getState());
		}
	}
    
    private void showToUser(ArrayList<Task> list, boolean isAppended) {
		tasksOnScreen = new ArrayList<Task> (list);
		gui.display(list, -1, Constants.NO_EFFECT, isAppended);
	}
    
    private void showToUser(ArrayList<Task> list, Task task, int effect, boolean isAppended) {
		tasksOnScreen = new ArrayList<Task> (list);
		
		int i;
		for(i = 0; i < list.size(); i ++) {
			if (list.get(i).equals(task)) {
				break;
			}
		}
		
		gui.display(list, i, effect, isAppended);
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
    
	private String convertCalendarToString(Calendar time, String FORMAT) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);     
		return formatter.format(time.getTime());
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
