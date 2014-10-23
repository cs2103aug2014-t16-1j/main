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
			
			//test for edit
			userCommand = "add play games from 9am to 10am on 12 Sep 2014 at home";
			userInput = parser.format(userCommand);
			task = userInput.getTask();
			testLogic.add(task);
			String userCommandEdited = "add Meeting from 9am to 10am on 12 Sep 2014 at Boardroom";
			UserInput userInputEdited = parser.format(userCommandEdited);
			Task editedTask = userInputEdited.getTask();
			message = testLogic.edit(task, editedTask);
			assertEquals(Constants.MESSAGE_TASK_EDITED, message);
			
			/**test for delete
			userCommand = "delete meeting";
			userInput = parser.format(userCommand);
			Task deletedTask = userInput.getTask();
			ArrayList<Task> actualDeletedTasks = testLogic.delete(deletedTask);
			ArrayList<Task> expectedDeletedTasks = new ArrayList<Task>();
			expectedDeletedTasks.add(deletedTask);
			assertEquals(expectedDeletedTasks, actualDeletedTasks);
			
			//test for search
			userCommand = "search meeting";
			userInput = parser.format(userCommand);
			task = userInput.getTask();
			ArrayList<Task> expectedResults = testLogic.search(task.getDescription());
			ArrayList<Task> actualResults = new ArrayList<Task>();
			actualResults.add(task);
			assertEquals(expectedResults, actualResults);
			**/
			
		}catch (Exception e) {
			System.out.println("test: ");
			e.printStackTrace();
			assert (false);
		}
	}
}
