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

import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
		openFileToWrite(true);
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
		JSONParser parser = new JSONParser();
		try{
			while (in.hasNextLine()) {
				String line = in.nextLine();
				Object obj = parser.parse(line.trim());
				JSONObject jsonObject = (JSONObject) obj;
				task = jsonConverter.jsonToTask(jsonObject);
				list.add(task);
				task = new Task();
			}
		} catch(ParseException e){
			System.out.println("Parse Exception:");
			e.printStackTrace();
		}
		closeFileToRead();
		return list;
	}
	
	public void add(Task task) {
		oldTasks = copyList(listOfTasks);
		if(!oldTasks.isEmpty()){
			stackForUndo.push(oldTasks);
		}
		openFileToWrite(true);
		store(task);
		closeFileToWrite();
	}
	
	private void store(Task task) {
		listOfTasks.add(task);
		JSONObject jTask= jsonConverter.taskToJSON(task);
		out.print(jTask.toString() + "\r\n");
		
	}
	
	public void store(ArrayList<Task> list) {
		openFileToWrite(false);
		for (Task task : list) {
			store(task);
		}
		closeFileToWrite();
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
		//deleteFile();
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
		
		//deleteFile();
		listOfTasks.clear();
		store(newList);
	}
	
	public void edit(Task oldTask, Task newTask) {
		ArrayList<Task> newList = new ArrayList<Task>();
		oldTasks = copyList(listOfTasks);
		stackForUndo.push(oldTasks);
		
		for (Task item : listOfTasks) {
			if (item.equals(oldTask)) {
				item.update(newTask);
			}
			newList.add(item);
		}
		
		//deleteFile();
		listOfTasks.clear();
		store(newList);
	}
	
	public void clear() {
		oldTasks = new ArrayList<Task> (listOfTasks);
		listOfTasks.clear();
		deleteFile();
		openFileToWrite(false);
		closeFileToWrite();
	}
	
	public String undo() {
		if (!stackForUndo.empty()) {
			//deleteFile();
			listOfTasks.clear();
			store(stackForUndo.pop());
			return Constants.MESSAGE_UNDO_DONE;
		}
		return "Stack is empty";
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

	private void openFileToWrite(boolean append) {
		try {
			out = new PrintWriter(new FileWriter(fileName, append));
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
