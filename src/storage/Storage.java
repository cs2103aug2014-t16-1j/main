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
	
	private Logger logger;
	
	public Storage(String fileName) {
		this.fileName = fileName;
		logger = Logger.getLogger("log" + fileName);
		openFileToWrite();
		closeFileToWrite();
		this.listOfTasks = loadFromFile();
		this.oldTasks = this.listOfTasks;
	}
	
	public ArrayList<Task> load() {
		return listOfTasks;
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
			}
		}
		
		closeFileToRead();
		return list;
	}
	
	public void add(Task task) {
		oldTasks = new ArrayList<Task> (listOfTasks);
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
				case DONE:
					out.println(Constants.STATE_DONE);
					break;
				case PENDING:
					out.println(Constants.STATE_PENDING);
					break;
				case GIVEUP:
					out.println(Constants.STATE_GIVEUP);
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
	
	public void delete(Task feature) {
		ArrayList<Task> list = new ArrayList<Task>();
		
		for (Task task : listOfTasks) {
			if (!isIncluded(feature, task)) {
				list.add(task);
			}
		}
		
		deleteFile();
		oldTasks = new ArrayList<Task> (listOfTasks);
		listOfTasks.clear();
		store(list);
	}
	
	public void clear() {
		oldTasks = new ArrayList<Task> (listOfTasks);
		listOfTasks.clear();
		deleteFile();
		openFileToWrite();
		closeFileToWrite();
	}
	
	public void undo() {
		deleteFile();
		listOfTasks.clear();
		store(oldTasks);
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
			
		}
	}
	
	private void closeFileToRead() {
		in.close();
	}
	
	private void deleteFile() {
		File f = new File(fileName);
		f.delete();
	}

	public boolean queryFreeSlot(Calendar startTime) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean queryTask(Task task) {
		// TODO Auto-generated method stub
		return true;
	}
}
