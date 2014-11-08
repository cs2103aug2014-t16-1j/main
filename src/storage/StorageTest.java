package storage;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Scanner;


//import tkLibrary.CommandType;
//import tkLibrary.Constants;
import tkLibrary.PriorityType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Parser;

public class StorageTest {
	
	//@author A0118919U
	public Storage store  = new Storage("storeTest.txt");

	@Test
	public void testStorageAdd(){
		Parser parser = Parser.getInstance();
        String input1 = "add Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
        String input2 = "add playing from 6pm to 7pm on 29 Oct 2014 at Court";
        UserInput userInput;
		try{
			store.clear();
			userInput = parser.format(input1);
			Task task1 = userInput.getTask();
			store.add(task1);
			userInput = parser.format(input2);
			Task task2 = userInput.getTask();
			store.add(task2);
			ArrayList<Task> list = store.loadFromFile();
			assertEquals("Test if task description was added successfully", "Meeting", list.get(0).getDescription());
			assertEquals("Test if task description was added successfully", "Boardroom", list.get(0).getLocation());
			assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Sep 12 09:00:00 SGT 2014", list.get(0).getStartTime().getTime().toString());
			assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", list.get(0).getEndTime().getTime().toString());
			assertEquals("Test that the State is correctly set", null,list.get(0).getState());
			assertEquals("Test that the frequency is as default", 0,list.get(0).getFrequency());
			assertEquals("Test that the frequency is as default", null,list.get(0).getFrequencyType());
		}
		catch(Exception e){
			System.out.println("testStorageAdd: ");
            e.printStackTrace();
            assert (false);
		}
	}
	
	@Test
	public void testStorageDeleteandUndo() {
        Parser parser = Parser.getInstance();
        String input1 = "add Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
        String input2 = "add playing from 6pm to 7pm on 29 Oct 2014 at Court";
        String input = "delete Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
        UserInput userInput;
        try {
        	store.clear();
			userInput = parser.format(input1);
			Task task1 = userInput.getTask();
			store.add(task1);
			userInput = parser.format(input2);
			Task task2 = userInput.getTask();
			store.add(task2);
            userInput = parser.format(input);
            Task task = userInput.getTask();
            store.delete(task);
            store.undo();
            ArrayList<Task> list = store.loadFromFile();
            assertEquals("Test if task description was deleted successfully", "Meeting", list.get(0).getDescription());
			assertEquals("Test if task description was deleted successfully", "Boardroom", list.get(0).getLocation());
			assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Sep 12 09:00:00 SGT 2014", list.get(0).getStartTime().getTime().toString());
			assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", list.get(0).getEndTime().getTime().toString());
			assertEquals("Test that the State is correctly set", null,list.get(0).getState());
			assertEquals("Test that the frequency is as default", 0,list.get(0).getFrequency());
			assertEquals("Test that the frequency is as default", null,list.get(0).getFrequencyType());
        } catch (Exception e) {
            System.out.println("testStorageDeleteandUndo: ");
            e.printStackTrace();
            assert (false);
        }
    }
    
    @Test
    public void testStorageEdit() {
        Parser parser = Parser.getInstance();
        String input1 = "add Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
        String input2 = "add playing from 6pm to 7pm on 29 Oct 2014 at Court";
        UserInput userInput;
        String input =
                "edit Meeting from 9am to 10am on 12 Sep 2014 at Boardroom"
                        + " correct Board Meeting from 9pm to 11pm on 12 Dec 2014 at Home";
        try {
        	store.clear();
			userInput = parser.format(input1);
			Task task1 = userInput.getTask();
			store.add(task1);
			userInput = parser.format(input2);
			Task task2 = userInput.getTask();
			store.add(task2);
            userInput = parser.format(input);
            Task task = userInput.getTask();
            Task editedTask = userInput.getEditedTask();
            store.edit(task, editedTask);
            ArrayList<Task> list = store.loadFromFile();
            assertEquals("Test if task description was edited successfully", "Board Meeting", list.get(0).getDescription());
			assertEquals("Test if task description was edited successfully", "Home", list.get(0).getLocation());
			assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Dec 12 21:00:00 SGT 2014", list.get(0).getStartTime().getTime().toString());
			assertEquals("Test that the End Time is correctly recorded",
                    "Fri Dec 12 23:00:00 SGT 2014", list.get(0).getEndTime().getTime().toString());
			assertEquals("Test that the State is correctly set", null,list.get(0).getState());
			assertEquals("Test that the frequency is as default", 0,list.get(0).getFrequency());
			assertEquals("Test that the frequency is as default", null,list.get(0).getFrequencyType());
        } catch (Exception e) {
            System.out.println("testStorageEdit: ");
            e.printStackTrace();
            assert (false);
        }
    }
	
    @Test
    public void testStorageSet() {
        Parser parser = Parser.getInstance();
        String input1 = "add Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
        String input2 = "add playing from 6pm to 7pm on 29 Oct 2014 at Court";
        String input = "set Meeting from 9am to 10am on 12 Sep 2014 at Boardroom priority high status completed";
        UserInput userInput;
        try {
        	store.clear();
			userInput = parser.format(input1);
			Task task1 = userInput.getTask();
			store.add(task1);
			userInput = parser.format(input2);
			Task task2 = userInput.getTask();
			store.add(task2);
            userInput = parser.format(input);
            Task task = userInput.getTask();
            store.set(task);
            ArrayList<Task> list = store.loadFromFile();
            assertEquals("Test if task description was successfully set", "Meeting", list.get(0).getDescription());
			assertEquals("Test if task description was successfully set", "Boardroom", list.get(0).getLocation());
			assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Sep 12 09:00:00 SGT 2014", list.get(0).getStartTime().getTime().toString());
			assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", list.get(0).getEndTime().getTime().toString());
			assertEquals("Test that the State is correctly set", StateType.COMPLETED,list.get(0).getState());
			assertEquals("Test that the frequency is as default", 0,list.get(0).getFrequency());
			assertEquals("Test that the frequency is as default", null,list.get(0).getFrequencyType());
			assertEquals("Test that the priority is correctly recorded",PriorityType.HIGH, list.get(0).getPriorityLevel());
        } catch (Exception e) {
            System.out.println("testStorageSet: ");
            e.printStackTrace();
            assert (false);
        }
    }
    
    @Test
	public void testStorageUndoandRedo(){
		Parser parser = Parser.getInstance();
        String input1 = "add playing from 6pm to 7pm on 29 Oct 2014 at Court";
        String input2 = "add Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
        UserInput userInput;
		try{
			store.clear();
			userInput = parser.format(input1);
			Task task1 = userInput.getTask();
			store.add(task1);
			userInput = parser.format(input2);
			Task task2 = userInput.getTask();
			store.add(task2);
			store.undo();
			store.redo();
			ArrayList<Task> list = store.loadFromFile();
			assertEquals("Test if task description was added successfully", "Meeting", list.get(1).getDescription());
			assertEquals("Test if task description was added successfully", "Boardroom", list.get(1).getLocation());
			assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Sep 12 09:00:00 SGT 2014", list.get(1).getStartTime().getTime().toString());
			assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", list.get(1).getEndTime().getTime().toString());
			assertEquals("Test that the State is correctly set", null,list.get(1).getState());
			assertEquals("Test that the frequency is as default", 0,list.get(1).getFrequency());
			assertEquals("Test that the frequency is as default", null,list.get(1).getFrequencyType());
		}
		catch(Exception e){
			System.out.println("testStorageRedo: ");
            e.printStackTrace();
            assert (false);
		}
	}
	
    public void clear(){
    	store.clear();
    }
}
