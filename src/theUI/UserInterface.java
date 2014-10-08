package theUI;

import java.util.ArrayList;
import java.util.Scanner;

import storage.Storage;
import tkLibrary.CommandType;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Logic;
import tkLogic.Parser;

public class UserInterface {
	private String NO_COMMAND = "";
	private Logic logic;
	private Parser parser;
	private Storage storage;
	private Gui gui;
	
	public void run(String fileName) {
		parser = new Parser();
		logic = new Logic(fileName);
		storage = new Storage(fileName);
		gui = new Gui();
		
		while (true) {
			String userCommand = gui.getUserCommand();
			if (userCommand != NO_COMMAND) {
				try {
					executeCommands(userCommand);
				} catch (Exception e) {
					gui.displayFailed(e.getMessage());
				}
			}
		}
	}

	private void executeCommands(String userCommand) {
		UserInput userInput = parser.format(userCommand);
		CommandType command = userInput.getCommand();
		Task task = userInput.getTask();
		
		//ADD, DELETE, UNDO, EDIT, CLEAR, LIST
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
				edit();
				break;
			case CLEAR:
				clear(); 
				break;
			default:
				list(); 
				break;
		}
	}
	
	private void add(Task task) {
		String feedback = logic.add(task);
		gui.displaySuccessed(feedback);
	}

	private void list(Task task) {
		ArrayList<Task> lists = logic.list(task);
		lists = logic.sort(lists);
		gui.display(task);
	}
	
	private void delete(Task task) {
		String feedback = logic.delete(task);
		gui.displaySuccessed(feedback);
	}
	
	private void edit(Task taskToBeEdited, Task newTask) {
		String feedback = logic.delete(taskToBeEdited, newTask);
		gui.displaySuccessed(feedback);
	}

	private void clear(UserInput userInput) {
		String feedback = logic.clear(task);
		gui.displaySuccessed(feedback);
	}

	private void undo(UserInput userInput) {
		
	}
}
