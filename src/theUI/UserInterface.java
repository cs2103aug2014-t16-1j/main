package theUI;

import java.util.Scanner;

import storage.Storage;
import tkLibrary.Task;
import tkLogic.Logic;


/*
 * Do some interfaces: simple boxes, colorful text, ...
 */
public class UserInterface {
	/*
	 * Each user have a file to store all the tasks
	 * Later we can add an user ID and automatically generate a file to store for each user.
	 */
	private String fileName;
	private Scanner scanner;
	private Logic logic;
	private Storage storage;
	
	public void run(String fileName) {
		scanner = new Scanner(System.in);
		this.fileName = fileName;
		logic = new Logic(fileName);
		storage = new Storage(fileName);
		while (true) {
			executeCommands(scanner.nextLine());
		}
	}
	
	
	/** This function is to process the user command
	 *  Determine the type (the first word)
	 *  Then call the correct logic function. 
	 *  For each function, try to decide what to show to the user
	 *  Can access storage.
	*/
	private void executeCommands(String userCommand) {
		if (userCommand.equals("exit")) {
			System.out.println("bye!!!");
			System.exit(0);
		}
		System.out.println("hello world");
	}
	
	/** This is to extract the information of the command */
	private String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}

	/** This is to extract the code of the command */
	private String getFirstWord(String userCommand) {
	String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
}
