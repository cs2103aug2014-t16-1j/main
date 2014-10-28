package storage;

import static org.junit.Assert.*;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import tkLibrary.CommandType;
import tkLibrary.Constants;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Parser;

public class StorageTest {
	public Storage store  = new Storage("test.txt");
	
	/* This is a boundary case for the optimal partition */
	@Test
	public void testStorageAdd(){
		Parser parser = Parser.getInstance();
        String input = "add Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
        UserInput userInput;
		try{
			userInput = parser.format(input);
			Task temp = userInput.getTask();
			store.add(temp);
			ArrayList<Task> list = store.loadFromFile();
			assertEquals("Test if task description was aded successfully", "Meeting", list.get(0).getDescription());
			assertEquals("Test if task description was aded successfully", "Boardroom", list.get(0).getLocation());
			assertEquals("Test that the Start Time is correctly recorded",
                    "Fri Sep 12 09:00:00 SGT 2014", list.get(0).getStartTime().getTime().toString());
			assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", list.get(0).getEndTime().getTime().toString());
			assertEquals("Test that the State is correctly set", StateType.PENDING,list.get(0).getState());
			assertEquals("Test that the frequency is as default", 0,list.get(0).getFrequency());
			assertEquals("Test that the frequency is as default", null,list.get(0).getFrequencyType());
		}
		catch(Exception e){
			System.out.println("testStorageAdd: ");
            e.printStackTrace();
            assert (false);
		}
	}
	
	/* This is a boundary case for the partition with additional date */
    @Test
    public void testStorageAddTwoDates() {
        Parser parser = Parser.getInstance();
        String input = "add Meeting from 9am on 11 Sep 2014 to 10am on 12 Sep 2014 at Boardroom";
        UserInput userInput;
        try {
            userInput = parser.format(input);
            Task temp = userInput.getTask();
            store.add(temp);
			ArrayList<Task> list = store.loadFromFile();
            assertEquals("Test that the Start Time is correctly recorded",
                    "Thu Sep 11 09:00:00 SGT 2014", list.get(1).getStartTime().getTime().toString());
            assertEquals("Test that the End Time is correctly recorded",
                    "Fri Sep 12 10:00:00 SGT 2014", list.get(1).getEndTime().getTime().toString());
        } catch (Exception e) {
            System.out.println("testParserAddTwoDates: ");
            e.printStackTrace();
            assert (false);
        }
    }
}
