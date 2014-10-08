package storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import tkLibrary.Task;

public class Storage {
	// everything that goes to the main storage has to pass the cache
	// this will help "undo" command.
	// I will tell Savin about this cache later. Now just ignore the cache.
	private String fileName;
	private String cacheName;
	private static final int[] DAYS_IN_MONTHS = { 31, 28, 31, 30, 31, 30, 31,
			31, 30, 31, 30, 31 };
	private static ArrayList<Task[]> days = new ArrayList<Task[]>(365);

	public Storage(String fileName) {
		this.fileName = fileName;
		this.cacheName = "." + fileName;
		initialize_arraylist();
		try {
			initialize_fromFile();
		} catch (Exception e) {
			System.out.println("File Cannot be Initialized - Not Found");
		}
	}

	private void initialize_arraylist() {
		for (int i = 0; i < 365; i++) {
			Task[] temp = new Task[24];
			days.add(temp);
		}
	}

	public void delete(Task task) {
		int start_date = task.getStartTime().get(Calendar.DAY_OF_YEAR);
		int end_date = task.getEndTime().get(Calendar.DAY_OF_YEAR);
		if (start_date == end_date) {
			Task[] tp = days.get(start_date - 1);
			for (int i = task.getStartTime().get(Calendar.HOUR_OF_DAY) - 1; i < task
					.getEndTime().get(Calendar.HOUR_OF_DAY); i++) {
				tp[i] = null;
			}
		} else {
			for (int i = start_date - 1; i < end_date; i++) {
				Task[] tp = days.get(i);
				if (i == end_date - 1) {
					for (int j = 0; j < task.getEndTime().get(Calendar.HOUR_OF_DAY); j++) {
						tp[j] = null;
					}
				} else {
					for (int j = task.getStartTime().get(Calendar.HOUR_OF_DAY); j < 24; j++) {
						tp[j] = null;
					}
				}
			}
		}
		try {
			write();
		} catch (Exception e) {
			System.out.println("File Not Found");
		}
	}

	private void initialize_fromFile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		String[] temp;
		while ((line = br.readLine()) != null) {
			temp = line.trim().split("\\s+");
			Calendar start = null;
			Calendar end = null;
			start.set(Calendar.DAY_OF_MONTH,Integer.parseInt(temp[3]));
			start.set(Calendar.MONTH,Integer.parseInt(temp[4]));
			start.set(Calendar.HOUR_OF_DAY,Integer.parseInt(temp[5]));
			end.set(Calendar.DAY_OF_MONTH,Integer.parseInt(temp[6]));
			end.set(Calendar.MONTH,Integer.parseInt(temp[7]));
			end.set(Calendar.HOUR_OF_DAY,Integer.parseInt(temp[8]));
			Task task = null;
			task.setDescription(temp[1]);
			task.setLocation(temp[2]);
			task.setStartTime(start);
			task.setEndTime(end);
			store(task);
		}
		br.close();
	}

	private int calculate_days(int month, int day) {
		int sum = 0;
		for (int i = 0; i < month - 1; i++) {
			sum = sum + DAYS_IN_MONTHS[i];
		}
		sum = sum + day;
		return sum;
	}

	// load everything from the cache.
	public ArrayList<Task> load() {
		return null;
	}

	// add a new task or the whole list of tasks.
	// first copy everything from cache -> main storage.
	// then edit the cache.
	public void store(Task task) {
		int start_date = task.getStartTime().get(Calendar.DAY_OF_YEAR);
		int end_date = task.getEndTime().get(Calendar.DAY_OF_YEAR);
		if (start_date == end_date) {
			Task[] tp = days.get(start_date - 1);
			for (int i = task.getStartTime().get(Calendar.HOUR_OF_DAY) - 1; i < task
					.getEndTime().get(Calendar.HOUR_OF_DAY); i++) {
				tp[i] = task;
			}
		} else {
			for (int i = start_date - 1; i < end_date; i++) {
				Task[] tp = days.get(i);
				if (i == end_date - 1) {
					for (int j = 0; j < task.getEndTime().get(Calendar.HOUR_OF_DAY); j++) {
						tp[j] = task;
					}
				} else {
					for (int j = task.getStartTime().get(Calendar.HOUR_OF_DAY); j < 24; j++) {
						tp[j] = task;
					}
				}
			}
		}
		try {
			write();
		} catch (Exception e) {
			System.out.println("File Not Found");
		}
	}

	
	// query - returns if a task is there or not in calendar
	public boolean queryTask(Task task) {
		int start_date = task.getStartTime().get(Calendar.DAY_OF_YEAR);
		int end_date = task.getEndTime().get(Calendar.DAY_OF_YEAR);
		if (start_date == end_date) {
			Task[] tp = days.get(start_date - 1);
			for (int i = task.getStartTime().get(Calendar.HOUR_OF_DAY) - 1; i < task
					.getEndTime().get(Calendar.HOUR_OF_DAY); i++) {
				if (!tp[i].equals(null)) {
					return false;
				}
			}
		} else {
			for (int i = start_date - 1; i < end_date; i++) {
				Task[] tp = days.get(i);
				if (i == end_date - 1) {
					for (int j = 0; j < task.getEndTime().get(Calendar.HOUR_OF_DAY); j++) {
						if (!tp[j].equals(null)) {
							return false;
						}
					}
				} else {
					for (int j = task.getStartTime().get(Calendar.HOUR_OF_DAY); j < 24; j++) {
						if (!tp[j].equals(null)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	// check out - why??
	public String store(ArrayList<Task> tasks) {
		return null;
	}

	// this if for undo command, just copy everything from the main storage ->
	// cache;
	public String removeCache() {
		return null;
	}
	
	//writing data from days to file
	private void write() throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fileName, false)));
		int count = 1;
		Task last = null;
		for (int i = 0; i < days.size(); i++) {
			Task[] temp = days.get(i);
			for (int j = 0; j < temp.length; j++) {
				Task tp = temp[j];
				if ((!tp.equals(last)) && (!tp.equals(null))) {
					Calendar start = tp.getStartTime();
					Calendar end = tp.getEndTime();
					out.println((count) + " " + tp.getDescription() + " "
							+ tp.getLocation() + " "
							+ start.get(Calendar.DAY_OF_MONTH) + " "
							+ start.get(Calendar.MONTH) + " "
							+ start.get(Calendar.HOUR_OF_DAY) + " "
							+ end.get(Calendar.DAY_OF_MONTH) + " "
							+ end.get(Calendar.MONTH) + " "
							+ end.get(Calendar.HOUR_OF_DAY));
					count++;
					last = tp;
				}
			}
		}
		out.close();
	}
}
