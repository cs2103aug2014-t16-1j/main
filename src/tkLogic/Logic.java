package tkLogic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import storage.Storage;
import tkLibrary.Constants;
import tkLibrary.Task;

/*
 * Basically the logic functions should be very clear and simple.
 * So that UserInterface have to parse the command and call the logic.
 * This maybe be a little bit more complicated so I can help Ben.
 */
public class Logic {
	private Storage storage;
	private Logger logger;
	
	public Logic(String fileName) {
		storage = new Storage(fileName);
		try {  
		    logger = Logger.getLogger("LogicLogFile.log");
	        // This block configure the logger with handler and formatter  
	        FileHandler fh = new FileHandler("LogicLogFile.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	}
	
	public String add(Task task) {
		if (isExistingTask(task)) {
			return Constants.MESSAGE_DUPLICATED_TASK;
		}
		if (isFreeTimeslots(task)) {
			storage.add(task);
			logger.info("Task added.");
			return Constants.MESSAGE_TASK_ADDED;
		} else {
			storage.add(task);
			logger.info("Task added.");
			return Constants.MESSAGE_CLASHING_TIMESLOTS;
		}
	}
	
	// for delete, need the accuracy of the name and the location. Storage does the searching.
	public ArrayList<Task> delete(Task task) {
		ArrayList<Task> deletedTasks = storage.delete(task);
		logger.info("Task deleted.");
		return sort(deletedTasks);
	}

	public String edit(Task taskToBeEdited, Task editedTask) throws Exception {
		if (delete(taskToBeEdited).equals(Constants.MESSAGE_TASK_DOES_NOT_EXIST)) {
			logger.info("Task does not exist.");
			return Constants.MESSAGE_EDIT_TASK_DOES_NOT_EXIST;
		}
		if (add(editedTask).equals(Constants.MESSAGE_CLASHING_TIMESLOTS)) {
			logger.info("Task cannot be edited because new task clashes.");
			return Constants.MESSAGE_EDIT_TASK_CLASHES;
		}
		logger.info("Task edited.");
		return Constants.MESSAGE_TASK_EDITED;
	}
	
	/*
	 * for listing, only mention the time, the frequency and the state.
	 */
	public ArrayList<Task> list(Task task) {
		ArrayList<Task> res = new ArrayList<Task>();
		ArrayList<Task> list = storage.load();
		
		boolean isDeadline = false, isEvent = false, isFloating = false;
		if (task.getDescription() != null) {
			isDeadline = task.getDescription().toLowerCase().contains("deadline");
			isEvent = task.getDescription().toLowerCase().contains("event");
			isFloating = task.getDescription().toLowerCase().contains("floating");
		}
		if (!isDeadline && !isEvent && !isFloating) {
			isDeadline = isEvent = isFloating = true;
		}
		
		for (Task item : list) {
			if (isIncluded(task, item)) {
				if (isDeadline && item.getStartTime() != null && item.getEndTime() == null) {
					res.add(item);
				} else if (isEvent && item.getStartTime() != null && item.getEndTime() != null) {
					res.add(item);
				} else if (isFloating && item.getStartTime() == null && item.getEndTime() == null) {
					res.add(item);
				} 
			}
		}
		res = sort(res);
		logger.info("Tasks acquired to be displayed.");
		return res;
	}

	public String undo() {
		storage.undo();
		logger.info("Last command undone.");
		return Constants.MESSAGE_UNDO_DONE;
	}
	
	public String clear() {
		storage.clear();
		logger.info("Tasks cleared from TasKoord.");
		return Constants.MESSAGE_TASK_CLEARED;
	}

	/*
	 * searching a task by keyword
	 * only consider the description and the location
	 */
	public ArrayList<Task> search(String keyword) {
		ArrayList<Task> allTasks = storage.load();
		ArrayList<Task> searchResults = new ArrayList<Task>();
		
		for(Task item : allTasks){
			if (item.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
			   (item.getLocation() != null && item.getLocation().toLowerCase().contains(keyword.toLowerCase()))){
				searchResults.add(item);
			}
		}
		searchResults = sort(searchResults);
		logger.info("Tasks with keyword found.");
		return searchResults;
	}

	private ArrayList<Task> sort(ArrayList<Task> list) {
		Collections.sort(list, new Comparator<Task>() {
	        @Override
	        public int compare(Task  task1, Task  task2) {
	        	String t1 = convertCalendarToString(task1.getStartTime(), Constants.FORMAT_DATE_CMP);
	        	String t2 = convertCalendarToString(task2.getStartTime(), Constants.FORMAT_DATE_CMP);
	        	
	            if (t1 == null && t2 != null ) {
	            	return 1;
	            } else if (t1 != null && t2 == null ) {
	            	return -1;
	            } else if (t1 == null && t2 == null ) {
	            	return 0;
	            } 
	            return t1.compareTo(t2);
	        }
	    });
		logger.info("Tasks sorted.");
		return list;
	}
	
	public ArrayList<Task> set(Task newTask) {
		ArrayList<Task> changedTasks = storage.set(newTask);
		return sort(changedTasks);
	}
	
	public String setPriorityLevel(Task task){
		if(isExistingTask(task)){
			task.setPriority(task.getPriorityLevel());
			logger.info("Task priority added.");
			return Constants.MESSAGE_PRIORITY_SET;
		}
		logger.info("Task does not exist.");
		return Constants.MESSAGE_PRIORITY_TASK_DOES_NOT_EXIST;
	}
	
	private boolean isIncluded(Task feature, Task task) {
		if (feature.getStartTime() != null) {
			String featureStartTime = convertCalendarToString(feature.getStartTime(), Constants.FORMAT_DATE);
			String featureEndTime = convertCalendarToString(feature.getEndTime(), Constants.FORMAT_DATE);
			if (featureEndTime == null) {
				featureEndTime = featureStartTime;
			}
			
			String startTime = convertCalendarToString(task.getStartTime(), Constants.FORMAT_DATE);
			String endTime = convertCalendarToString(task.getEndTime(), Constants.FORMAT_DATE);
	
			if (startTime == null && endTime == null) {
				return false;
			} else if (startTime != null && endTime !=null) {
				if (startTime.compareTo(featureEndTime) > 0 
						|| featureStartTime.compareTo(endTime) > 0) {
					return false;
				}
			} else if (startTime != null && (startTime.compareTo(featureStartTime) < 0 || startTime.compareTo(featureEndTime) > 0)) {
				return false;
			} else if (endTime != null && (endTime.compareTo(featureStartTime) < 0 || endTime.compareTo(featureEndTime) > 0)) {
				return false;
			}
		}
		
		if (feature.getFrequencyType() != null
				&& feature.getFrequencyType()!=task.getFrequencyType()) {
			return false;
		}
		
		if (feature.getState() != null
				&& feature.getState()!=task.getState()) {
			return false;
		}
		
		if (feature.getPriorityLevel() != null
				&& feature.getPriorityLevel()!=task.getPriorityLevel()) {
			return false;
		}
		
		return true;
	}
	
	private String convertCalendarToString(Calendar time, String FORMAT) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);     
		return formatter.format(time.getTime());
	}
	
	// this is to check if a task already existing. using description and location to check.
	private boolean isExistingTask(Task task){
		ArrayList<Task> queryList = search(task.getDescription());
		for (Task item : queryList) {
			if (item.getDescription().equalsIgnoreCase(task.getDescription())) {
				if (item.getLocation() == null && task.getLocation() == null) {
					return true;
				}
				if (item.getLocation() != null && item.getLocation().equalsIgnoreCase(task.getLocation())) {
					return true;
				}	
			}
		}
		return false;
	}
	
	private boolean isFreeTimeslots(Task task){
		ArrayList<Task> allTasks = storage.load();
		ArrayList<Task> queryList = new ArrayList<Task>();
		
		if(isFloatingTask(task)){
			return true;
		}
		
		for(Task item: allTasks){
			if (isTimedTask(item)){
				queryList.add(item);
			}
		}
		
		for(Task queriedTask: queryList){
			if(isSameStartTime(task, queriedTask)){
				return false;
			}
			if(isBetweenStartAndEndTimeForTaskEndTime(task, queriedTask)){
				return false;
			}
			if(isBetweenStartAndEndTimeForTaskStartTime(queriedTask, task)){
				return false;
			}
			if(isBetweenStartAndEndTimeForTaskStartTime(task, queriedTask)){
				return false;
			}
		}
		return true;
	}
	
	private boolean isFloatingTask(Task task){
		return (task.getEndTime() == null && task.getStartTime() == null);
	}
	
	private boolean isTimedTask(Task task){
		return (task.getStartTime() != null && task.getEndTime() != null);
	}
	
	private boolean isSameStartTime(Task task, Task queriedTask){
		return (task.getStartTime().compareTo(queriedTask.getStartTime()) == 0);
	}
	
	private boolean isBetweenStartAndEndTimeForTaskEndTime(Task task, Task queriedTask){
		return (task.getEndTime().compareTo(queriedTask.getEndTime()) < 0) && (task.getEndTime().compareTo(queriedTask.getStartTime()) > 0);
	}
		
	private boolean isBetweenStartAndEndTimeForTaskStartTime(Task task, Task queriedTask){
		return (queriedTask.getStartTime().compareTo(task.getStartTime()) > 0) && (queriedTask.getStartTime().compareTo(task.getEndTime()) < 0);
	}
	
	public String setState(Task task){
		if(isExistingTask(task)){
			task.setState(task.getState());
			logger.info("Task state changed.");
			switch (task.getState()){
				case COMPLETED:
					logger.info("State changed to completed");
					return Constants.MESSAGE_STATE_COMPLETED;	
				case DISCARDED:
					logger.info("State changed to discarded");
					return Constants.MESSAGE_STATE_DISCARDED;
				case PENDING:
					logger.info("State changed to pending");
					return Constants.MESSAGE_STATE_PENDING;
			}
		}
		logger.info("Task does not exist.");
		return Constants.MESSAGE_PRIORITY_TASK_DOES_NOT_EXIST;
	} 
}