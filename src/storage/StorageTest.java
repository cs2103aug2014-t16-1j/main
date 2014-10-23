package storage;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import tkLibrary.Constants;
import tkLibrary.Task;

public class StorageTest {
	@Test
	public void testAdd(){
		Storage store  = new Storage("test.txt");
		Task temp  =new Task();
		temp.setStartTime("010 23 2014 04:00:00");
		temp.setEndTime("010 23 2014 05:00:00");
		temp.setDescription("tutorial");
		temp.setLocation("SOC");
		temp.setFrequencyType(Constants.FREQUENCY_WEEK);
		temp.setPriority(Constants.PRIORITY_HIGH);
		store.add(temp);
		ArrayList<Task> list = store.loadFromFile();
		try{
			assertEquals("Test if task description was aded successfully", "tutorial" , list.get(0).getDescription());
		}
		catch(Exception e){
			System.out.println("testStorageAdd: ");
            e.printStackTrace();
            assert (false);
		}
	}
}
