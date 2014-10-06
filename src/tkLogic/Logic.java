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
	
	public String add(Task task) throws Exception {
		if(storage.queryFreeSlot(task.getStartTime()) || storage.queryFreeSlot(task.getEndTime())){
			try{
				storage.store(task);
			} catch (Exception e){
					throw new Exception("Unable to add to TasKoord.");
			}
		}
		else{
			return Constants.MESSAGE_CLASHING_TIMESLOTS;
		}
		return Constants.MESSAGE_TASK_ADDED;
	}
	
	public String edit(Task taskToBeEdited, Task editedTask) {
		Logic tempLogic = new Logic(fileName);
		try{
			tempLogic.delete(taskToBeEdited);
			tempLogic.add(editedTask);
			return Constants.MESSAGE_TASK_EDITED;
		} catch (Exception e){
			return ("Unable to edit!");
		}
	}

	public String undo() {
		return null;
	}
	
	public String delete(int lineNum) {
		return null;
	}
	
	public String delete(Task task) throws Exception{
		if(storage.queryTask(task.getDescription())){
			try{
				storage.delete(task);
			} catch (Exception e){
				throw new Exception("Unable to delete from TasKoord.");
			}
		}
		else{
			return Constants.MESSAGE_TASK_DOES_NOT_EXIST;
		}
		return Constants.MESSAGE_TASK_DELETED;
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
