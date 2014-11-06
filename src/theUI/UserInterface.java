package theUI;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import tkLibrary.CommandType;
import tkLibrary.Constants;
import tkLibrary.GcPacket;
import tkLibrary.PriorityType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Logic;
import tkLogic.Parser;
import GCal.BrowserExecution;;

//@author A0112068N
public class UserInterface {
	private final int COLOR_DONE = 1;
	private final int COLOR_WARNING = 2;
    private final String NO_COMMAND = "";
    
    private String clientEmail = "658469510712-compute@developer.gserviceaccount.com";
    private String helpFile = "help";
    
    private Logic logic;
    private Parser parser;
    private Gui gui;
    private BrowserExecution browser;
    
    private ArrayList<Task> tasksOnScreen;
    private ArrayList<String> statusForUndo;
    private ArrayList<String> statusForRedo;
    private ArrayList<ArrayList<Task>> tasksForUndo;
    private ArrayList<ArrayList<Task>> tasksForRedo;
    private ArrayList<Integer> effectForUndo;
    private ArrayList<Integer> effectForRedo;
    private ArrayList<Integer> posToDoEffectForUndo;
    private ArrayList<Integer> posToDoEffectForRedo;
    private ArrayList<Integer> colorForUndo;
    private ArrayList<Integer> colorForRedo;
    private int currentPos;
    private int availablePos;
    
    private boolean isSyncing;
    private String accessToken;
    
    public UserInterface(String fileName) {
    	parser = Parser.getInstance();
        logic = new Logic(fileName);
        gui = new Gui();
        browser = new BrowserExecution();
        
        tasksOnScreen = new ArrayList<Task> ();
        statusForUndo = new ArrayList<String> ();
        statusForRedo = new ArrayList<String> ();
        tasksForUndo = new ArrayList<ArrayList<Task>> ();
        tasksForRedo = new ArrayList<ArrayList<Task>> ();
        effectForUndo = new ArrayList<Integer> ();
        effectForRedo = new ArrayList<Integer> ();
        posToDoEffectForUndo = new ArrayList<Integer> ();
        posToDoEffectForRedo = new ArrayList<Integer> ();
        colorForUndo = new ArrayList<Integer> ();
        colorForRedo = new ArrayList<Integer> ();
        currentPos = availablePos = -1;
        
        isSyncing = false;
        accessToken = "";
    }
    
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            String userCommand = gui.getUserCommand();
            if (!userCommand.equals(NO_COMMAND)) {
            	if (isSyncing) {
            		gui.setUserCommand(NO_COMMAND);
            		accessToken = userCommand;
            		syncWithToken();
            		continue;
            	}
                try {
                	gui.setUserCommand(NO_COMMAND);
                    executeCommands(userCommand);
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
            if (e.getMessage().contains("invalid")) {
                gui.displayWarning(String.format(Constants.EXCEPTIONS_INVALID_USERCOMMAND, 
                        userCommand, e.getMessage()), false);
            } else {
            	gui.displayWarning("Cannot understand the command!", false);
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
        } else if (command == CommandType.REDO) {
        	redo();
        } else if (command == CommandType.SYNC) {
        	sync();
        } else if (command == CommandType.HELP) {
        	help();
        } else if (command == CommandType.EXIT) {
        	gui.displayDone("Good Bye !!!", false);
        	 try {
                 Thread.sleep(100);
             } catch(InterruptedException e) {
                 Thread.currentThread().interrupt();
             }
        	System.exit(0);
        } else {
            gui.displayWarning("Informat command", false);
        }
    }
    
    /* Standard main method to try that test as a standalone application. */
    
	public void getTokenPopup(String url)  {
		gui.displayDone("Please do not close the browser - press q", false);
			//java.awt.Desktop.getDesktop().browse(new URI(url));
			browser.init(url);
			browser.start();
	}
    
	@SuppressWarnings({ "deprecated", "deprecation" })
	private void syncWithToken() {
		isSyncing = false;
		try {
			browser.stop();
			//logic.connectByNewToken(accessToken);
			gui.displayDone(Constants.MESSAGE_SYNCING, false);
			gui.displayDone("Make sure that you shared your calendar with: " + clientEmail, true);
			GcPacket packet = logic.sync(logic.load());
			gui.displayDone(Constants.MESSAGE_SYNC_COMPLETE, false);
			displayPacket(packet);
		} catch (Exception e) {
			e.printStackTrace();
			gui.displayWarning(Constants.MESSAGE_SYNC_ERROR, false);
			gui.displayWarning("Make sure that you shared your calendar with: " + clientEmail, true);
		}
	}
	
    private void sync() {
    	isSyncing = true;
    	if (!logic.isOnline()) {
    		gui.displayWarning(Constants.MESSAGE_NO_INTERNET, false);
    		isSyncing = false;
    	} else {
    		gui.displayDone(Constants.MESSAGE_SYNCING, false);
    		gui.displayDone("Make sure that you shared your calendar with: " + clientEmail, true);
    		if (logic.connectUsingExistingToken()) {
	    		try {
					GcPacket packet = logic.sync(logic.load());
					gui.displayDone(Constants.MESSAGE_SYNC_COMPLETE, false);
					displayPacket(packet);
					isSyncing = false;
					return;
				} catch (IOException e) {
					getTokenPopup(logic.getURL());
					e.printStackTrace();
				}
	    	} else {
	    		getTokenPopup(logic.getURL());
	    	}
    	}
    }
    
    private void displayPacket(GcPacket packet) {
    	for (Task item : packet.taskDeletedFromTK) {
    		logic.delete(item);
    	}
    	
    	for (Task item : packet.taskAddedToTK) {
    		try {
	    		item.setPriority(Constants.PRIORITY_MEDIUM);
	    		item.setState(Constants.STATE_PENDING);
    		} catch(Exception e) {
    			
    		}
    		logic.add(item);
    	}
    	logic.setSynced();
    }
    
    private void help() {
    	gui.displayFile(helpFile);
    	tasksOnScreen.clear();
    }
    
    private void add(Task task) {
        if (task.getDescription() == null) {
            gui.displayWarning(Constants.MESSAGE_NO_ADD_INFO, false);
            return;
        }
        
        if (task.getStartTime() != null && task.getEndTime() != null) {
        	if (task.getStartTime().compareTo(task.getEndTime()) >= 0) {
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
            ArrayList<Task> list = searchTaskOfSameDay(task);
            
            if (feedback.equals(Constants.MESSAGE_TASK_ADDED)) {
            	addToStackForUndoAndRedo(list, task, Constants.DELETED, "Task deleted.", COLOR_DONE,
            			list, task, Constants.HIGHLIGH, feedback, COLOR_DONE);
            	gui.displayDone(feedback, false);
            	showToUser(list, task, Constants.HIGHLIGH, true);
            } else if (feedback.equals(Constants.MESSAGE_CLASHING_TIMESLOTS)) {
            	addToStackForUndoAndRedo(list, task, Constants.DELETED, "Task deleted.", COLOR_DONE,
            			list, task, Constants.HIGHLIGH, feedback, COLOR_WARNING);
            	gui.displayWarning(feedback, false);
            	showToUser(list, task, Constants.HIGHLIGH, true);
            } else if (feedback.equals(Constants.MESSAGE_NO_START_TIME)) {
            	gui.displayWarning(feedback, false);
            } else {
            	gui.displayWarning(feedback, false);
            	showToUser(list, task, Constants.HIGHLIGH, true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private ArrayList<Task> searchTaskOfSameDay(Task task) {
		Task taskToSearch = new Task();
		taskToSearch.setStartTime(task.getStartTime());
		taskToSearch.setEndTime(task.getEndTime());
		if (taskToSearch.getStartTime() == null) {
			taskToSearch.setDescription("floating");
		}
		ArrayList<Task> list = logic.list(taskToSearch);
		return list;
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

                Calendar endTime = Calendar.getInstance();
                endTime.add(Calendar.DAY_OF_MONTH, time);
                task.setStartTime(Calendar.getInstance());
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
	        		Task taskToBeDeleted = tasksOnScreen.get(noOfTask - 1);
	        		list = searchTaskOfSameDay(taskToBeDeleted);
	        		
	        		String feedback = logic.delete(taskToBeDeleted);
	        		if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
	        			addToStackForUndoAndRedo(list, taskToBeDeleted, Constants.HIGHLIGH, "Task restored.", COLOR_DONE,
	        					list, taskToBeDeleted, Constants.DELETED, feedback, COLOR_DONE);
	        			gui.displayDone(feedback, false);
	        			showToUser(list, taskToBeDeleted, Constants.DELETED, true);
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
            	Task taskToBeDeleted = list.get(0);
            	list = searchTaskOfSameDay(taskToBeDeleted);
        		
        		String feedback = logic.delete(taskToBeDeleted);
        		if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
        			addToStackForUndoAndRedo(list, taskToBeDeleted, Constants.HIGHLIGH, "Task restored.", COLOR_DONE,
        					list, taskToBeDeleted, Constants.DELETED, feedback, COLOR_DONE);
        			gui.displayDone(feedback, false);
        			showToUser(list, taskToBeDeleted, Constants.DELETED, true);
        		} else {
        			gui.displayWarning(feedback, false);
        		}
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
	        		Task taskToBeEdited = new Task(tasksOnScreen.get(noOfTask - 1));
	        		
	        		ArrayList<Task> oldList = searchTaskOfSameDay(taskToBeEdited);
	        		Task oldTask = new Task(taskToBeEdited);

	        		String feedback = logic.edit(taskToBeEdited, updatedTask);
	        		taskToBeEdited.update(updatedTask);
	        		
	        		list = searchTaskOfSameDay(taskToBeEdited);
	        		
	        		displayFeedbackForEditing(oldList, oldTask, list, taskToBeEdited, feedback);
	        		return;
	        	}
	        }
    		
    		list = logic.search(task.getDescription());
            if (list.size() == 0) {
                gui.displayDone(Constants.MESSAGE_TASK_DOES_NOT_EXIST, false);
            } else if (list.size() == 1) {
            	Task taskToBeEdited = new Task(list.get(0));
            	
            	ArrayList<Task> oldList = searchTaskOfSameDay(taskToBeEdited);
        		Task oldTask = new Task(taskToBeEdited);

        		String feedback = logic.edit(taskToBeEdited, updatedTask);
        		taskToBeEdited.update(updatedTask);
        		
        		list = searchTaskOfSameDay(taskToBeEdited);
        		
        		displayFeedbackForEditing(oldList, oldTask, list, taskToBeEdited, feedback);
            } else {
            	gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND, false);
            	showToUser(list, true);
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	private void displayFeedbackForEditing(ArrayList<Task> oldList, Task oldTask, ArrayList<Task> list, Task task,
			String feedback) {
		if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
			addToStackForUndoAndRedo(oldList, oldTask, Constants.HIGHLIGH, "Task edited.", COLOR_DONE,
					list, task, Constants.HIGHLIGH, feedback, COLOR_DONE);
			gui.displayDone(feedback, false);
			showToUser(list, task, Constants.HIGHLIGH, true);
		} else if (feedback.equals(Constants.MESSAGE_EDIT_CLASHING_TIMESLOTS)) {
			addToStackForUndoAndRedo(oldList, oldTask, Constants.HIGHLIGH, "Task edited.", COLOR_WARNING,
					list, task, Constants.HIGHLIGH, feedback, COLOR_WARNING);
			gui.displayWarning(feedback, false);
			showToUser(list, task, Constants.HIGHLIGH, true);
		} else {
			gui.displayWarning(feedback, false);
		}
	}

    private void clear() { 
        try {
            String feedback = logic.clear();
            if (feedback.equals(Constants.MESSAGE_TASK_CLEARED)) {
            	ArrayList<Task> list = logic.list(new Task()); 
                gui.displayDone(feedback, false);
                addToStackForUndoAndRedo(list, new Task(), Constants.NO_EFFECT, "All tasks restored.", COLOR_DONE,
                		null, null, Constants.NO_EFFECT, feedback, COLOR_DONE);
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
            if (feedback.equals(Constants.MESSAGE_UNDO_DONE) && currentPos >= 0) {
            	if (colorForUndo.get(currentPos) == COLOR_DONE) {
            		gui.displayDone(statusForUndo.get(currentPos), false);
            	} else {
            		gui.displayWarning(statusForUndo.get(currentPos), false);
            	}
            	gui.display(tasksForUndo.get(currentPos), posToDoEffectForUndo.get(currentPos), effectForUndo.get(currentPos), true);
            	currentPos --;
            } else {
            	gui.displayWarning("No command for undoing", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void redo() {
        try {
            String feedback = logic.redo();
            if (feedback.equals(Constants.MESSAGE_REDO_DONE) && currentPos < availablePos) {
            	currentPos ++;
            	if (colorForRedo.get(currentPos) == COLOR_DONE) {
            		gui.displayDone(statusForRedo.get(currentPos), false);
            	} else {
            		gui.displayWarning(statusForRedo.get(currentPos), false);
            	}
            	gui.display(tasksForRedo.get(currentPos), posToDoEffectForRedo.get(currentPos), effectForRedo.get(currentPos), true);
            } else {
            	gui.displayWarning("No command for redoing", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }    	
    }
    
    private void addToStackForUndoAndRedo(ArrayList<Task> listForUndo, Task taskForUndo, int undoEffect, String messageForUndo, int ucolor,
    		ArrayList<Task> listForRedo, Task taskForRedo, int redoEffect, String messageForRedo, int rcolor) {
    	int posForUndo = -1, posForRedo = -1;
    	
    	if (listForUndo != null) {
	    	for (posForUndo = 0; posForUndo < listForUndo.size(); posForUndo ++) {
	    		if (listForUndo.get(posForUndo).equals(taskForUndo)) {
	    			break;
	    		}
	    	}
    	}
    	if (listForRedo != null) {
	    	for (posForRedo = 0; posForRedo < listForRedo.size(); posForRedo ++) {
	    		if (listForRedo.get(posForRedo).equals(taskForRedo)) {
	    			break;
	    		}
	    	}
    	}
    	
    	currentPos ++; availablePos = currentPos;
    	if (currentPos >= statusForUndo.size()) {
	    	statusForUndo.add(messageForUndo);
	    	statusForRedo.add(messageForRedo);
	    	tasksForUndo.add(listForUndo);
	    	tasksForRedo.add(listForRedo);
	    	effectForUndo.add(undoEffect);
	    	effectForRedo.add(redoEffect);
	    	posToDoEffectForUndo.add(posForUndo);
	    	posToDoEffectForRedo.add(posForRedo);
	    	colorForUndo.add(ucolor);
	    	colorForRedo.add(rcolor);
    	} else {
    		statusForUndo.set(currentPos, messageForUndo);
	    	statusForRedo.set(currentPos, messageForRedo);
	    	tasksForUndo.set(currentPos, listForUndo);
	    	tasksForRedo.set(currentPos, listForRedo);
	    	effectForUndo.set(currentPos, undoEffect);
	    	effectForRedo.set(currentPos, redoEffect);
	    	posToDoEffectForUndo.set(currentPos, posForUndo);
	    	posToDoEffectForRedo.set(currentPos, posForRedo);
	    	colorForUndo.set(currentPos, ucolor);
	    	colorForRedo.set(currentPos, rcolor);
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
        			Task taskToBeEdited = tasksOnScreen.get(noOfTask - 1);
        			editTheTask(task, taskToBeEdited);
	        		return;
	        	}
	        }
    		
    		list = logic.search(task.getDescription());
            if (list.size() == 0) {
                gui.displayDone(Constants.MESSAGE_NO_RESULT, false);
            } else if (list.size() == 1) {
            	Task taskToBeEdited = list.get(0);
    			editTheTask(task, taskToBeEdited);
    		} else {
            	gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND, false);
            	showToUser(list, true);
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	private void editTheTask(Task task, Task taskToBeEdited) {
		ArrayList<Task> originalList;
		originalList = new ArrayList<Task> (searchTaskOfSameDay(taskToBeEdited));
		Task originalTask = new Task(taskToBeEdited);
		
		updateNewPriorityAndState(task, taskToBeEdited);
		String feedback = logic.set(taskToBeEdited);
		
		if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
			String message = String.format(Constants.MESSAGE_UPDATE_STATUS_AND_PRIORITY, 
					taskToBeEdited.getState(), taskToBeEdited.getPriorityLevel());
			String messageForUndo = String.format(Constants.MESSAGE_UPDATE_STATUS_AND_PRIORITY, 
					originalTask.getState(), originalTask.getPriorityLevel());
			
			ArrayList<Task> list = searchTaskOfSameDay(taskToBeEdited);
			addToStackForUndoAndRedo(originalList, originalTask, Constants.HIGHLIGH, messageForUndo, COLOR_DONE,
					list, taskToBeEdited, Constants.HIGHLIGH, messageForUndo, COLOR_DONE);
			
			gui.displayDone(message, false);
			showToUser(list, taskToBeEdited, Constants.HIGHLIGH, true);
		} else {
			gui.displayWarning(feedback, false);
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
