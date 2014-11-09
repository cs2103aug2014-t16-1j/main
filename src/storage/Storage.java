package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import tkLibrary.Constants;
import tkLibrary.LogFile;
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
	private ArrayList<ArrayList<Task>> stackForUndo;
	private int currentPos;
	private int availablePos;
	
	private static Logger LOGGER = Logger.getLogger(".TasKoordLogFile.log");
	
	//@author A0118919U
	public Storage(String fileName) {
		this.fileName = fileName;
		LogFile.newLogger();
		openFileToWrite(true);
		closeFileToWrite();
		this.listOfTasks = loadFromFile();
		this.oldTasks = copyList(this.listOfTasks);
		this.stackForUndo = new ArrayList<ArrayList<Task>> ();
		this.stackForUndo.add(copyList(this.listOfTasks));
		this.currentPos = this.availablePos = 0;
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
		openFileToWrite(true);
		store(task);
		push(copyList(listOfTasks));
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
	
	//@author A0112068N
	public void delete(Task taskToBeDeleted) {
		ArrayList<Task> newList = new ArrayList<Task>();
		
		for (Task item : listOfTasks) {
			if (!item.equals(taskToBeDeleted)) {
				newList.add(item);
			}
		} 
		
		listOfTasks.clear();
		store(newList);
		push(copyList(listOfTasks));
	}
	
	public void set(Task newTask) {
		ArrayList<Task> newList = new ArrayList<Task>();
		
		for (Task item : listOfTasks) {
			if (item.equals(newTask)) {
				item.setPriority(newTask.getPriorityLevel());
				item.setState(newTask.getState());
			}
			newList.add(item);
		}
		
		listOfTasks.clear();
		store(newList);
		push(copyList(listOfTasks));
	}
	
	//@author A0118919U
	public void edit(Task oldTask, Task newTask) {
		ArrayList<Task> newList = new ArrayList<Task>();
		
		for (Task item : listOfTasks) {
			if (item.equals(oldTask)) {
				item.update(newTask);
				item.setSyncedValue(0);
			}
			newList.add(item);
		}
		
		listOfTasks.clear();
		store(newList);
		push(copyList(listOfTasks));
	}
	
	public void clear() {
		oldTasks = new ArrayList<Task> (listOfTasks);
		listOfTasks.clear();
		deleteFile();
		openFileToWrite(false);
		closeFileToWrite();
	}
	
	//@author A0112068N
	public String undo() {
		if (currentPos > 0) {
			currentPos --;
			listOfTasks.clear();
			store(stackForUndo.get(currentPos));
			return Constants.MESSAGE_UNDO_DONE;
		}
		
		return "Stack is empty";
	}
	
	public String redo() {
		if (currentPos < availablePos) {
			currentPos ++;
			listOfTasks.clear();
			store(stackForUndo.get(currentPos));
			return Constants.MESSAGE_REDO_DONE;
		}
		return "No command to redo.";
	}
	
	private void push(ArrayList<Task> list) {
		currentPos ++; availablePos = currentPos;
    	if (currentPos >= stackForUndo.size()) {
    		stackForUndo.add(list);
    	} else {
    		stackForUndo.set(currentPos, list);
    	}
	}
	
	public void setSynced() {
		ArrayList<Task> newList = copyList(listOfTasks);
		for(Task item : newList) {
			item.setSynced();
		}
		listOfTasks.clear();
		store(newList);
	}
	
	//@author A0118919U
	private ArrayList<Task> copyList(ArrayList<Task> list) {
		ArrayList<Task> result = new ArrayList<Task> ();
		for (Task item : list) {
			result.add(new Task(item));
		}
		return result;
	}
	
	private void openFileToWrite(boolean append) {
		try {
			out = new PrintWriter(new FileWriter(fileName, append));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			LOGGER.log(Level.WARNING, "File Not Found", e);
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
			LOGGER.log(Level.WARNING, "File Not Found", e);
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
