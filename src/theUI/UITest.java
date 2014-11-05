package theUI;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UITest {

	private UserInterface ui;

	@Before
	public void initObj() {
		ui = new UserInterface("testing.txt");
	}

	@Test
	public void testAddCommand() {
		ui.executeCommands("clear");
		assertEquals(ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>All tasks cleared from TasKoord.</font><br>");

		/*
		 * test for "adding" partition
		 */

		// boundary case
		ui.executeCommands("add");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #F92672>Please specify the task you want to add!</font><br>");

		// add an event
		ui.executeCommands("add Meeting from 9am to 10am on 24 Oct 2015 at Boardroom");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Task added to TasKoord!</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Sat, 24 Oct 2015]</font><font size = 4 color = #FD971F>======</font><br><br><b><u><font size = 3 color = #A6E22E>01. </font></u></b><b><u><font size = 4 color = #E6DB74>[09:00</font></u></b><b><u><font size = 4 color = #E6DB74> - 10:00]</font></u></b><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b><u><font size = 4 color = #66D9EF>Meeting</font></u></b><b><u><font size = 4 color = #CECEF6> @ Boardroom</font></u></b><br>");

		// add a floating task
		ui.executeCommands("add read Harry Potter");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Task added to TasKoord!</font><br><br><font size = 4 color = #FD971F>===GOOD-TO-DO TASKS===</font><br><br><font size = 3 color = #A6E22E>01. </font><b><u><font size = 4 color = #66D9EF>read Harry Potter</font></u></b><br>");

		// add a deadline
		ui.executeCommands("  ");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #F92672>Your input is invalid:    <br>The command is invalid: </font><br>");

		/*
		 * tests for "delete" partition
		 */
		// boundary case
		ui.executeCommands("delete");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #F92672>Please specify the task you want to delete!</font><br>");

		// case: no task matches
		ui.executeCommands("delete eat ice cream");
		assertEquals(ui.getDisplayedMessage(),
				"<font size = 4 color = #F92672>Task does not exist!</font><br>");

		// case: task matches
		ui.executeCommands("delete Harry");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Tasks were deleted from TasKoord:</font><br><br><font size = 4 color = #FD971F>===GOOD-TO-DO TASKS===</font><br><br><font size = 3 color = #A6E22E>01. </font><strike><font size = 4 color = #66D9EF>read Harry Potter</font></strike><br>");

		/*
		 * tests for "list" partition
		 */
		// boundary case
		ui.executeCommands("list");
		assertEquals(
				ui.getDisplayedMessage(),
				"<br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Sat, 24 Oct 2015]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>01. </font><font size = 4 color = #E6DB74>[09:00</font><font size = 4 color = #E6DB74> - 10:00]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>Meeting</font><font size = 4 color = #CECEF6> @ Boardroom</font><br>");
		
		// list upcoming task
		ui.executeCommands("list upcoming tasks for next 365 days");
		assertEquals(
				ui.getDisplayedMessage(),
				"<br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Sat, 24 Oct 2015]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>01. </font><font size = 4 color = #E6DB74>[09:00</font><font size = 4 color = #E6DB74> - 10:00]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>Meeting</font><font size = 4 color = #CECEF6> @ Boardroom</font><br>");

	}
}
