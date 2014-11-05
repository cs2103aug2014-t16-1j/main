//@author A0111705W
package tkLogic;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLibrary.Constants;

public class LogicTest {

	@Test
	public void test() {
		Logic testLogic = new Logic("logicTest.txt");
		testLogic.clear();

		Parser parser = Parser.getInstance();

		try{
			String userCommand = "add study from 9am to 11am on 12 Sep 2014 at home";
			UserInput userInput = parser.format(userCommand);
			Task task = userInput.getTask();
			
			//test for first add
			String message = testLogic.add(task);
			assertEquals(Constants.MESSAGE_TASK_ADDED, message);
			
			//test for duplicated task
			message = testLogic.add(task);
			assertEquals(Constants.MESSAGE_DUPLICATED_TASK, message);
			
			//test for clashing task
			userCommand = "add play games from 9am to 10am on 12 Sep 2014 at home";
			userInput = parser.format(userCommand);
			task = userInput.getTask();
			message = testLogic.add(task);
			assertEquals(Constants.MESSAGE_CLASHING_TIMESLOTS, message);
			
			//test for clear
			message = testLogic.clear();
			assertEquals(Constants.MESSAGE_TASK_CLEARED, message);
			
			//test for editing time
			userCommand = "add play games from 9am to 10am on 12 Sep 2014 at home";
			userInput = parser.format(userCommand);
			task = userInput.getTask();
			testLogic.add(task);
			String userCommandEdited = "edit play games correct from 10am to 11am on 12 Sep 2014 at home";
			UserInput userInputEdited = parser.format(userCommandEdited);
			Task editedTask = userInputEdited.getTask();
			message = testLogic.edit(task, editedTask);
			assertEquals(Constants.MESSAGE_TASK_EDITED, message);
			
			//test for editing location
			userCommandEdited = "edit play games correct from 10am to 11am on 12 Sep 2014 at school";
			userInputEdited = parser.format(userCommandEdited);
			editedTask = userInputEdited.getTask();
			message = testLogic.edit(task, editedTask);
			assertEquals(Constants.MESSAGE_TASK_EDITED, message);
			
			//test for editing date
			userCommandEdited = "edit play games correct from 10am to 11am on 13 Sep 2014 at school";
			userInputEdited = parser.format(userCommandEdited);
			editedTask = userInputEdited.getTask();
			message = testLogic.edit(task, editedTask);
			assertEquals(Constants.MESSAGE_TASK_EDITED, message);
			
			//test for editing description
			userCommandEdited = "edit play games from 10am to 11am on 13 Sep 2014 at school";
			userInputEdited = parser.format(userCommandEdited);
			editedTask = userInputEdited.getTask();
			message = testLogic.edit(task, editedTask);
			assertEquals(Constants.MESSAGE_TASK_EDITED, message);
			
			//test for searching an existing task
			userCommand = "search play";
			userInput = parser.format(userCommand);
			task = userInput.getTask();
			ArrayList<Task> expectedResults = testLogic.search(task.getDescription());
			Task actualResult = editedTask;
			assertTrue(expectedResults.get(0).equals(actualResult));
			
			//test for searching a task that does not exist
			userCommand = "search ball";
			userInput = parser.format(userCommand);
			task = userInput.getTask();
			expectedResults = testLogic.search(task.getDescription());
			assertTrue(expectedResults.isEmpty());
			
		}catch (Exception e) {
			System.out.println("test: ");
			e.printStackTrace();
			assert (false);
		}
	}
}
