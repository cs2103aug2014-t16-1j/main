package tkLogic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	private Storage storage;

	public Logic(String fileName) {
		storage = new Storage(fileName);
	}
	
	public String add(Task task) throws Exception {
		if (isFreeTimeslots(task)) {
			storage.store(task);
		} else {
			return Constants.MESSAGE_CLASHING_TIMESLOTS;
		}
		return Constants.MESSAGE_TASK_ADDED;
	}
	
	public String delete(Task task) throws Exception{
		if(isExistingTask(task)){
			storage.delete(task);
		} else {
			return Constants.MESSAGE_TASK_DOES_NOT_EXIST;
		}
		return Constants.MESSAGE_TASK_DELETED;
	}

	private boolean isFreeTimeslots(Task task){
		return (storage.queryFreeSlot(task.getStartTime()) || storage.queryFreeSlot(task.getEndTime()));
	}
	
	private boolean isExistingTask(Task task){
		return (storage.queryTask(task));
	}

	public String edit(Task taskToBeEdited, Task editedTask) throws Exception {
		if (delete(taskToBeEdited).equals(Constants.MESSAGE_TASK_DOES_NOT_EXIST)) {
			return Constants.MESSAGE_EDIT_TASK_DOES_NOT_EXIST;
		}
		if (add(editedTask).equals(Constants.MESSAGE_CLASHING_TIMESLOTS)) {
			return Constants.MESSAGE_EDIT_TASK_CLASHES;
		}
		return Constants.MESSAGE_TASK_EDITED;
	}
	
	public ArrayList<Task> list(Task task) {
		ArrayList<Task> res = new ArrayList<Task>();
		ArrayList<Task> list = storage.load();
		
		for (Task item : list) {
			if (isIncluded(task, item)) 
				res.add(item);
		}
		
		return res;
	}
	
	private boolean isIncluded(Task feature, Task task) {
		if (feature.getStartTime() != null) {
			if (!convertCalendarToString(feature.getStartTime()).equals(convertCalendarToString(task.getStartTime())))
			return false;
		}
		
		if (feature.getEndTime() != null) {
			if (!convertCalendarToString(feature.getEndTime()).equals(convertCalendarToString(task.getEndTime())))
			return false;
		}
		
		if (feature.getLocation() != null
				&& !feature.getLocation().equals(task.getLocation())) {
			return false;
		}
		
		if (feature.getDescription() != null
				&& !feature.getDescription().equals(task.getDescription())) {
			return false;
		}
		
		return true;
	}
	
	private String convertCalendarToString(Calendar time) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);     
		return formatter.format(time.getTime());
	}

	public String undo() {
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
