package tkLogic;

import java.util.ArrayList;
import java.util.Date;

import storage.Storage;
import tkLibrary.StateType;
import tkLibrary.Task;

/*
 * Basically the logic functions should be very clear and simple.
 * So that UserInterface have to parse the command and call the logic.
 * This maybe be a little bit more complicated so I can help Ben.
 */
public class Logic {
	
	private String fileName;
	private ArrayList<Task> lists;
	private Storage storage;
	
	public Logic(String fileName) {
		this.fileName = fileName;
		storage = new Storage(fileName);
	}
	
	public String add(Task task) {
		return null;
	}
	
	public String edit(int lineNum) {
		return null;
	}

	public String undo() {
		return null;
	}
	
	public String delete(int lineNum) {
		return null;
	}
	
	
	public ArrayList<Task> list() {
		return null;
	}
	
	public ArrayList<Task> list(StateType state) {
		return null;
	}
	
	public ArrayList<Task> list(Date date) {
		return null;
	}
	
	public ArrayList<Task> listUpcomingTasks() {
		return null;
	}
	
	public ArrayList<Task> search(String keyword) {
		return null;
		
	}
	
	// this logic function is to sort an array of tasks, helpful when printing
	public ArrayList<Task> sort(ArrayList<Task> tasks) {
		return null;
	}
}
