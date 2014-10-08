package theUI;

import java.util.ArrayList;

//import storage.Storage;
import tkLibrary.CommandType;
import tkLibrary.Constants;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Logic;
import tkLogic.Parser;

public class UserInterface {
	private String NO_COMMAND = "";
	private Logic logic;
	private Parser parser;
	//private Storage storage;
	private Gui gui;
	
	public void run(String fileName) {
		parser = new Parser();
		logic = new Logic(fileName);
		//storage = new Storage(fileName);
		gui = new Gui();
		
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
					gui.displayFailed(e.getMessage());
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
			gui.displayFailed(e.getMessage());
			return;
		}
				
		//gui.display(task1);
		//System.out.println(task1.getLocation());
		//System.out.println(task1.getStartTime().getTime());
		
		switch (command) {
			case ADD:
				add(task);
				break;
			case DELETE:
				delete(task); 
				break;
			case UNDO:
				undo();
				break;
			case EDIT:
				Task newTask = userInput.getEditedTask();
				edit(task, newTask);
				break;
			case CLEAR:
				clear(); 
				break;
			case LIST:
				list(task); 
				break;
			default:
				gui.displayFailed("informat command");
				break;
		}
	}
	
	private void add(Task task) {
		String feedback;
		
		try {
			feedback = logic.add(task);
		} catch (Exception e){
			feedback = e.getMessage();
		}
	
		if (feedback.equals(Constants.MESSAGE_TASK_ADDED)) {
			gui.displayDone(feedback);
		} else {
			gui.displayFailed(feedback);
		}
	}

	private void list(Task task) {
		ArrayList<Task> lists = logic.list(task);
		//lists = logic.sort(lists);
		gui.display(lists);
	}
	
	private void delete(Task task) {
		String feedback;
		try {
			feedback = logic.delete(task);
		} catch (Exception e) {
			feedback = e.getMessage();
		}
		
		if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
			gui.displayDone(feedback);
		} else {
			gui.displayFailed(feedback);
		}
	}
	
	private void edit(Task taskToBeEdited, Task newTask) {
		String feedback;
		try {
			feedback = logic.edit(taskToBeEdited, newTask);
		} catch (Exception e) {
			feedback = e.getMessage();
		}
		if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
			gui.displayDone(feedback);
		} else {
			gui.displayFailed(feedback);
		}
	}

	private void clear() {
		String feedback; 
		try {
			//feedback = logic.clear();
			feedback = "test";
		} catch (Exception e) {
			feedback = e.getMessage();
		}
		
		if (feedback.equals(Constants.MESSAGE_TASK_CLEARED)) {
			gui.displayDone(feedback);
		} else {
			gui.displayFailed(feedback);
		}
	}

	private void undo() {
		
	}
}
