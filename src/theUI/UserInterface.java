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
	private Gui gui;
	
	public void run(String fileName) {
		parser = Parser.getInstance();
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
			System.out.println(e.getMessage());
			return;
		}
	
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
				gui.displayWarning("Informat command", false);
				break;
		}
	}
	
	private void add(Task task) {
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
		}
	}

	private void list(Task task) {
		try {
			ArrayList<Task> lists = logic.list(task);
			gui.display(lists, false);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void delete(Task task) {
		try {
			String feedback = logic.delete(task);
			if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
				gui.displayDone(feedback, false);
			} else {
				gui.displayWarning(feedback, false);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
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
		}
	}

	private void clear() { 
		try {
			String feedback = logic.clear();
			feedback = "test";
			if (feedback.equals(Constants.MESSAGE_TASK_CLEARED)) {
				gui.displayDone(feedback, false);
			} else {
				gui.displayWarning(feedback, false);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	private void undo() {
		
	}
}
