import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;
import java.util.Set;

public class TasKoord {
	// This is for magic strings
	
	// This is for magic numbers
	
	// These are the possible command types
	enum CommandType {
		
	};

	// A new class Task
	public static class Task {
		public Date startTime;
		public Date endTime;
		public String location;
		public String description;
	}

	// store variables declared here
	// 4 sets to store 4 types of tasks. 
	private Set<Task> timedTasks, deadlineTasks, regularTasks, floatingTasks;
		
	private static Scanner scanner = new Scanner(System.in);
	private static PrintWriter printer;

	public void main(String[] args) {
		initiate();
		interactWithUser();
	}
	
	// read data
	public void initiate() {
	}
	
	/** This function will read the command and give feedback to user */	
	public void interactWithUser() {
		while (true) {
			showToUser("Welcome");
			String userCommand = enterCommand();
			Set<Task> feedback = executeCommand(userCommand);
			showToUser(feedback);
		}
	}
	
	public String enterCommand() {
		return scanner.nextLine();
	}
	
	/** This function is to display text/ feedback to user*/
	public void showToUser(String text) {
		System.out.println(text);
	}
	
	public void showToUser(Set<Task> list) {
		
	}
	
	/** This function is to process the user command
	 *  Determine the type (the first word)
	 *  Then call the correct function
	*/
	public Set<Task> executeCommand(String userCommand) {
		
	}
	
	private CommandType determineCommandType(String commandTypeString) {
	}
	
	/** 
	 * This function is to add a line to file 
	 * parse the command, call the correct type of adding.
	 * return the tasks of that day (think more abt regular task + floating task)
	*/
	private Set<Task> add(String userCommand) {
		
	}
	
	private Set<Task> addTimed(Task task) {
	}
	
	private Set<Task> addDeadline(Task task) {
	}
	
	private Set<Task> addRegular(Task task) {
	}
	
	private Set<Task> floatingTask(Task task) {
	}
	
	/**
	 * This is to edit a task
	 * parse the command, locate the task + reformat the info, edit the info
	 * return the tasks of that day
	 */
	private Set<Task> edit(String userCommand) {
	}
		
	/**
	 * This is for undo.
	 */
	private Set<Task> undo(String userCommand) {
		
	}
	
	/** This function is to delete something 
	 *  Parse the command, locate the task, delete it. the information of the task may be "all", "shown", 
	 *  	a number or a description of the task
	 *  Return the tasks of that day. 
	*/
	private Set<Task> delete(String userCommand) {
		
	}
	
	/**
	 *  This function is to list something.
	 *  Parse the command, determine the kind of listing and call the correct function 
	 */
	private Set<Task> list(String userCommand) {
	}
	
	private Set<Task> listTaskOfDay(Date date) {
	}
	
	private Set<Task> listUpcomingTask() {
	}
	
	private Set<Task> listCompletedTask() {
	}
	
	// this 2 can be done later
	private Set<Task> listRank(int rank) {
	}
	
	private Set<Task> listCategory(ArrayList<String> categories) {
	}
	
	// this function takes the usercommand and converts it to the right format.
	private Task parseCommand(String userCommand) {
		
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
