package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import tkLibrary.Constants;
import tkLibrary.Task;

public class Storage {
	private String fileName;
	private PrintWriter out;
	private Scanner in;
	
	private ArrayList<Task> oldTasks;
	private ArrayList<Task> listOfTasks;
	private Stack<ArrayList<Task>> stackForUndo;
	
	private Logger logger;
	
	public Storage(String fileName) {
		this.fileName = fileName;
		logger = Logger.getLogger("log" + fileName);
		openFileToWrite();
		closeFileToWrite();
		this.listOfTasks = loadFromFile();
		this.oldTasks = copyList(this.listOfTasks);
		this.stackForUndo = new Stack<ArrayList<Task>> ();
	}
	
	public ArrayList<Task> load() {
		return copyList(listOfTasks);
	}
	
	public ArrayList<Task> loadFromFile() {
		ArrayList<Task> list = new ArrayList<Task> ();
		Task task = new Task();
		
		openFileToRead();
		
		while (in.hasNextLine()) {
			String s = in.nextLine();
			if (s.equals(Constants.END_OBJECT_SIGNAL)) {
				list.add(task);
				task = new Task();
			} else if (s.equals(Constants.STARTTIME)) {
				task.setStartTime(in.nextLine());
			} else if (s.equals(Constants.ENDTIME)) {
				task.setEndTime(in.nextLine());
			} else if (s.equals(Constants.LOCATION)) {
				task.setLocation(in.nextLine());
			} else if (s.equals(Constants.DESCRIPTION)) {
				task.setDescription(in.nextLine());
			} else if (s.equals(Constants.FREQUENCY_TYPE)) {
				task.setFrequencyType(in.nextLine());
			} else if (s.equals(Constants.FREQUENCY)) {
				task.setFrequency(in.nextInt());
			} else if (s.equals(Constants.STATE_TYPE)) {
				task.setState(in.nextLine());
			} else if (s.equals(Constants.PRIORITY_TYPE)) {
				task.setPriority(in.nextLine());
			}
		}
		
		closeFileToRead();
		return list;
	}
	
	public void add(Task task) {
		oldTasks = copyList(listOfTasks);
		stackForUndo.push(oldTasks);
		store(task);
	}
	
	public void store(Task task) {
		listOfTasks.add(task);
		openFileToWrite();
		
		if (task.getStartTime() != null) {
			out.println(Constants.STARTTIME);     
			out.println(convertCalendarToString(task.getStartTime()));
		}
		
		if (task.getEndTime() != null) {
			out.println(Constants.ENDTIME);
			out.println(convertCalendarToString(task.getEndTime()));
		}
		
		if (task.getLocation() != null) {
			out.println(Constants.LOCATION);
			out.println(task.getLocation());
		}
		
		if (task.getDescription() != null) {
			out.println(Constants.DESCRIPTION);
			out.println(task.getDescription());
		}
		
		if (task.getFrequencyType() != null) {
			out.println(Constants.FREQUENCY_TYPE);
			switch (task.getFrequencyType()) {
				case DAY:
					out.println(Constants.FREQUENCY_DAY);
					break;
				case WEEK:
					out.println(Constants.FREQUENCY_WEEK);
					break;
				case MONTH:
					out.println(Constants.FREQUENCY_MONTH);
					break;
				case YEAR:
					out.println(Constants.FREQUENCY_YEAR);
					break;
				default:
					break;
			}
		}
		
		if (task.getState() != null) {
			out.println(Constants.STATE_TYPE);
			switch (task.getState()) {
				case COMPLETED:
					out.println(Constants.STATE_COMPLETED);
					break;
				case PENDING:
					out.println(Constants.STATE_PENDING);
					break;
				case DISCARDED:
					out.println(Constants.STATE_DISCARDED);
					break;
				default:
					break;
			}
		}
		
		if (task.getPriorityLevel() != null) {
			out.println(Constants.PRIORITY_TYPE);
			switch (task.getPriorityLevel()) {
				case HIGH:
					out.println(Constants.PRIORITY_HIGH);
					break;
				case MEDIUM:
					out.println(Constants.PRIORITY_MEDIUM);
					break;
				case LOW:
					out.println(Constants.PRIORITY_LOW);
					break;
				default:
					break;
			}
		}
		
		out.println(Constants.END_OBJECT_SIGNAL);
		closeFileToWrite();
	}
	
	public void store(ArrayList<Task> list) {
		for (Task task : list) {
			store(task);
		}
	}
	
	// delete only 1 task with its name and its location(description).
	public void delete(Task taskToBeDeleted) {
		ArrayList<Task> newList = new ArrayList<Task>();
		
		for (Task item : listOfTasks) {
			if (!item.equals(taskToBeDeleted)) {
				newList.add(item);
			}
		} 
		
		oldTasks = copyList(listOfTasks);
		stackForUndo.push(oldTasks);
		deleteFile();
		listOfTasks.clear();
		store(newList);
	}
	
	public void set(Task newTask) {
		ArrayList<Task> newList = new ArrayList<Task>();
		oldTasks = copyList(listOfTasks);
		stackForUndo.push(oldTasks);
		
		for (Task item : listOfTasks) {
			if (item.equals(newTask)) {
				item.setPriority(newTask.getPriorityLevel());
				item.setState(newTask.getState());
			}
			newList.add(item);
		}
		
		deleteFile();
		listOfTasks.clear();
		store(newList);
	}

	public void clear() {
		oldTasks = new ArrayList<Task> (listOfTasks);
		listOfTasks.clear();
		deleteFile();
		openFileToWrite();
		closeFileToWrite();
	}
	
	public void undo() {
		if (!stackForUndo.empty()) {
			deleteFile();
			listOfTasks.clear();
			store(stackForUndo.pop());
		}
	}
	
	private ArrayList<Task> copyList(ArrayList<Task> list) {
		ArrayList<Task> result = new ArrayList<Task> ();
		for (Task item : list) {
			result.add(new Task(item));
		}
		return result;
	}
	
	private String convertCalendarToString(Calendar time) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATE_HOUR);     
		return formatter.format(time.getTime());
	}

	private void openFileToWrite() {
		try {
			out = new PrintWriter(new FileWriter(fileName, true));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			logger.log(Level.WARNING, "File Not Found", e);
		}
	}

	private void closeFileToWrite() {
		out.close();
	}

	private void openFileToRead() {
		try {
			in = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			logger.log(Level.WARNING, "File Not Found", e);
		}
	}

	private void closeFileToRead() {
		in.close();
	}
	
	private void deleteFile() {
		File f = new File(fileName);
		f.delete();
	}
}
