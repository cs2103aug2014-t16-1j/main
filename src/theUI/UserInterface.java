package theUI;

import java.util.Scanner;

import storage.Storage;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Logic;
import tkLogic.Parser;

public class UserInterface {
	private String NO_COMMAND = "";
	private String fileName;
	private Logic logic;
	private Parser parser;
	private Storage storage;
	private Gui gui;
	
	//private CommandType command;
	//private Task task;
	
	public void run(String fileName) {
		this.fileName = fileName;
		parser = new Parser();
		logic = new Logic(fileName);
		storage = new Storage(fileName);
		gui = new Gui();
		
		while (true) {
			String userCommand = gui.getUserCommand();
			if (userCommand != NO_COMMAND) {
				executeCommands(userCommand);
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
		gui.display(feedback);
	}

	private void list(UserInput userInput) {
		
	}

	private void clear(UserInput userInput) {
		
	}

	private void edit(UserInput userInput) {
		
	}

	private void undo(UserInput userInput) {
		
	}

	private void delte(UserInput userInput) {
	}
}
