package storage;

import java.util.ArrayList;

import tkLibrary.Task;

public class Storage {
	// everything that goes to the main storage has to pass the cache
	// this will help "undo" command.
	// I will tell Savin abt this cache later. Now just ignore the cache.
	private String fileName;
	private String cacheName;
	
	public Storage(String fileName) {
		this.fileName = fileName;
		this.cacheName = "." + fileName;
	}
	
	// load everything from the cache.
	public ArrayList<Task> load() {
		return null;
	}
	
	// add a new task or the whole list of tasks.
	// first copy everything from cache -> main storage.
	// then edit the cache.
	public String store(Task task) {
		return null;
	}
	public String store(ArrayList<Task> tasks) {
		return null;
	}
	
	// this if for undo command, just copy everything from the main storage -> cache;
	public String removeCache() {
		return null;
	}
}
