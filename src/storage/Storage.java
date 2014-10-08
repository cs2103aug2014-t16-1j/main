package storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tkLibrary.Task;

public class Storage {
	// everything that goes to the main storage has to pass the cache
	// this will help "undo" command.
	// I will tell Savin about this cache later. Now just ignore the cache.
	private String fileName;
	private String cacheName;
	private static ArrayList<Task[]> days= new ArrayList<Task[]>(365);
	
	public Storage(String fileName) {
		this.fileName = fileName;
		this.cacheName = "." + fileName;
	}
	
	private void initialize_arraylist(){
		for(int i=0;i<365;i++){
			Task[] temp = new Task[24];
			days.add(temp);
		}
	}
	
	private void add(Task temp){
		
	}

	private void query(){
		
	}
	
	private void initialize()throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		while((line = br.readLine()) != null){
			lines.add(line.substring(EXTRACT_LINES_BEGIN_INDEX));
		}
		br.close();
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
	
	private void write()throws IOException{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, false)));
		int count = 1;
		Task last = null;
		for(int i=0; i<days.size();i++){
			Task[] temp = days.get(i);
			for(int j=0; j<temp.length; j++){
				Task tp = temp[j];
				if((!tp.equals(last)) && (!tp.equals(null))){
					Date start = tp.getStartTime();
					Date end = tp.getEndTime();
					out.println((count) + ". " + tp.getDescription() + " " + tp.getLocation() + " " 
					+ start.getDate() + " " + start.getMonth() + " " + start.getHours() + 
					" " + end.getDate() + " " + end.getMonth() + " "+ end.getHours());
				}
			}
		}
		out.close();
	}
}
