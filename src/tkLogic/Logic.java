package tkLogic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import java.io.IOException;

import storage.Storage;
import tkLibrary.Constants;
import tkLibrary.Task;
import tkLibrary.LogFile;

import GCal.GCal;



/*
 * Basically the logic functions should be very clear and simple.
 * So that UserInterface have to parse the command and call the logic.
 * This maybe be a little bit more complicated so I can help Ben.
 */
public class Logic {
	private Storage storage;
	private static Logger LOGGER = Logger.getLogger(".TasKoordLogFile.log");
	private GCal gcal;

	public Logic(String fileName) {
		storage = new Storage(fileName);
		gcal = new GCal(fileName);
		LogFile.newLogger();
	}
	
	public String add(Task task) {		
		if (isExistingTask(task)) {
			return Constants.MESSAGE_DUPLICATED_TASK;
		}
		if (isFreeTimeslots(task)) {
			storage.add(task);
			LOGGER.info("Task added.");
			return Constants.MESSAGE_TASK_ADDED;
		} else {
			storage.add(task);
			LOGGER.info("Task added.");
			return Constants.MESSAGE_CLASHING_TIMESLOTS;
		}
	}
	
	// delete a particular task.
	public String delete(Task task) {
		if (isExistingTask(task)) { 
			storage.delete(task);
			LOGGER.info("Task deleted.");
			return Constants.MESSAGE_TASK_DELETED;
		} else {
			LOGGER.info("No such task");
			return Constants.MESSAGE_TASK_DOES_NOT_EXIST;
		}
	}

	public String edit(Task taskToBeEdited, Task editedTask) throws Exception {
		if (isExistingTask(taskToBeEdited)) {
			if (isFreeTimeslots(editedTask)) {
				storage.edit(taskToBeEdited, editedTask);
				LOGGER.info("Task edited");
				return Constants.MESSAGE_TASK_EDITED;
			} else {
				storage.edit(taskToBeEdited, editedTask);
				return Constants.MESSAGE_EDIT_CLASHING_TIMESLOTS;
			}
		} else {
			return Constants.MESSAGE_TASK_DOES_NOT_EXIST;
		}
	}
	
	/*
	 * for listing, only mention the time, the priority and the state.
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
		LOGGER.info("Tasks acquired to be displayed.");
		return res;
	}

	public String undo() {
		LOGGER.info("Last command undone.");
		return storage.undo();
	}
	
	public String redo() {
		LOGGER.info("Next command redone.");
		return storage.redo();
	}
	
	public String clear() {
		storage.clear();
		LOGGER.info("Tasks cleared from TasKoord.");
		return Constants.MESSAGE_TASK_CLEARED;
	}

	/*
	 * searching a task by keyword
	 * only consider the description
	 */
	public ArrayList<Task> search(String keyword) {
		ArrayList<Task> allTasks = storage.load();
		ArrayList<Task> searchResults = new ArrayList<Task>();
		
		for(Task item : allTasks){
			if (item.getDescription().toLowerCase().contains(keyword.toLowerCase())){
				searchResults.add(item);
			}
		}
		searchResults = sort(searchResults);
		LOGGER.info("Tasks with keyword found.");
		return searchResults;
	}

	private ArrayList<Task> sort(ArrayList<Task> list) {
		Collections.sort(list, new Comparator<Task>() {
	        @Override
	        public int compare(Task  task1, Task  task2) {
	        	String s1 = convertCalendarToString(task1.getStartTime(), Constants.FORMAT_DATE_CMP);
	        	String s2 = convertCalendarToString(task2.getStartTime(), Constants.FORMAT_DATE_CMP);
	        	String e1 = convertCalendarToString(task1.getEndTime(), Constants.FORMAT_DATE_CMP);
	        	String e2 = convertCalendarToString(task2.getEndTime(), Constants.FORMAT_DATE_CMP);
	        	String n1 = task1.getDescription().toLowerCase();
	        	String n2 = task2.getDescription().toLowerCase();
	        			
	            if (s1 == null && s2 != null ) {
	            	return 1;
	            } else if (s1 != null && s2 == null ) {
	            	return -1;
	            } else if (s1 == null && s2 == null ) {
	            	return n1.compareTo(n2);
	            }
	            
	            if (s1.compareTo(s2) != 0) {
	            	return s1.compareTo(s2);
	            } 
	            
	            if (e1 == null && e2 != null ) {
	            	return 1;
	            } else if (e1 != null && e2 == null ) {
	            	return -1;
	            } else if (e1 == null && e2 == null ) {
	            	return n1.compareTo(n2);
	            }
	            
	            if (e1.compareTo(e2) != 0) {
	            	return e1.compareTo(e2);
	            }
	            return n1.compareTo(n2);
	        }
	    });
		LOGGER.info("Tasks sorted.");
		return list;
	}
	
	public String set(Task newTask) {
		if (isExistingTask(newTask)) {
			storage.set(newTask);
			return Constants.MESSAGE_TASK_EDITED;
		} else {
			return Constants.MESSAGE_TASK_DOES_NOT_EXIST;
		}
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
	private boolean isExistingTask(Task task) {
		ArrayList<Task> queryList = storage.load();
		for (Task item : queryList) {
			if (item.equals(task)) {
				return true;
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
		
		if (queryList.isEmpty()){
			return true;
		}
		
		// for deadline tasks, the time when it is due is start time instead of deadline for efficiency in sorting
		if(isDeadlineTask(task)){
			for(Task queriedTask: queryList){
				if(isSameStartTime(task, queriedTask)){
					return true;
				}
				if(isBeforeQueriedTaskStartTime(task, queriedTask)){
					return true;
				}
			}
			return false;
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
	
	private boolean isDeadlineTask(Task task){
		return (task.getEndTime() == null);
	}
	
	private boolean isTimedTask(Task task){
		return (task.getStartTime() != null && task.getEndTime() != null);
	}
	
	private boolean isSameStartTime(Task task, Task queriedTask){
		return (task.getStartTime().compareTo(queriedTask.getStartTime()) == 0);
	}
	
	private boolean isBeforeQueriedTaskStartTime(Task task, Task queriedTask){
		return (task.getStartTime().compareTo(queriedTask.getStartTime()) < 0);
	}
	
	private boolean isBetweenStartAndEndTimeForTaskEndTime(Task task, Task queriedTask){
		return (task.getEndTime().compareTo(queriedTask.getEndTime()) < 0) && (task.getEndTime().compareTo(queriedTask.getStartTime()) > 0);
	}
		
	private boolean isBetweenStartAndEndTimeForTaskStartTime(Task task, Task queriedTask){
		return (queriedTask.getStartTime().compareTo(task.getStartTime()) > 0) && (queriedTask.getStartTime().compareTo(task.getEndTime()) < 0);
	}

	public String setPriorityLevel(Task task){
		if(isExistingTask(task)){
			task.setPriority(task.getPriorityLevel());
			LOGGER.info("Task priority changed.");
			return Constants.MESSAGE_PRIORITY_SET;
		}
		LOGGER.info("Task does not exist.");
		return Constants.MESSAGE_PRIORITY_TASK_DOES_NOT_EXIST;
	}
	
	public String setState(Task task){
		if(isExistingTask(task)) {
			task.setState(task.getState());
			LOGGER.info("Task state changed.");
			switch (task.getState()){
				case COMPLETED:
					LOGGER.info("State changed to completed");
					return Constants.MESSAGE_STATE_COMPLETED;	
				case DISCARDED:
					LOGGER.info("State changed to discarded");
					return Constants.MESSAGE_STATE_DISCARDED;
				case PENDING:
					LOGGER.info("State changed to pending");
					return Constants.MESSAGE_STATE_PENDING;
			}
		}
		LOGGER.info("Task does not exist.");
		return Constants.MESSAGE_PRIORITY_TASK_DOES_NOT_EXIST;
	}
	
	public String getURL(){
		return gcal.getURL();
	}
	
	public boolean WithExistingToken(){
		return gcal.withExistingToken();
	}

	public boolean generateNewToken(String code) throws IOException{
		return gcal.generateNewToken(code);
	}
	
	public String syncWithGoogle() throws IOException{
		return gcal.syncGcal();
	}
}