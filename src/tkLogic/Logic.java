package tkLogic;

import java.util.ArrayList;
import java.util.Date;

import storage.Storage;
import tkLibrary.Constants;
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
		if(storage.queryFreeSlot(task.getStartTime()) || storage.queryFreeSlot(task.getEndTime())){
			storage.store(task);
		}
		else{
			return Constants.MESSAGE_CLASHING_TIMESLOTS;
		}
		return Constants.MESSAGE_TASK_ADDED;
	}
	
	public String edit(int lineNum) {
		return null;
	}

	public String undo() {
		return null;
	}
	
	public String delete(int lineNum) {
	/**public String delete(Task task){
	 * 		find description [using task.getDescription()] in storage, then delete it
	 * }
	 */
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
	
	public void storeCommand() {
	    // store the last command in the storage for the method Undo
	}
	
	// this logic function is to sort an array of tasks, helpful when printing
	public ArrayList<Task> sort(ArrayList<Task> tasks) {
		return null;
	}
}
