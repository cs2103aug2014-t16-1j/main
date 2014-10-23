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
				"<font size = 4 color = #A6E22E>All tasks was deleted.</font><br>");

		/*
		 * test for "adding" partition
		 */

		// boundary case
		ui.executeCommands("add");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #F92672>Please specify the task you want to add!</font><br>");

		// add an event
		ui.executeCommands("add Meeting from 9am to 10am on 24 Oct 2014 at Boardroom");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Task added to TasKoord!</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Fri, 24 Oct 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 4 color = #E6DB74>[09:00:00</font><font size = 4 color = #E6DB74> - 10:00:00] </font><font size = 4 color = #66D9EF>Meeting</font><br><font size = 4 color = #CECEF6>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp@ Boardroom</font><br>");

		// add a floating task
		ui.executeCommands("add read Harry Potter");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Task added to TasKoord!</font><br><br><font size = 4 color = #FD971F>===GOOD-TO-DO TASKS===</font><br><br><font size = 4 color = #66D9EF>read Harry Potter</font><br>");

		// add a deadline
		ui.executeCommands("add submit report by 10am on 26 Oct 2014 at school");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Task added to TasKoord!</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Sun, 26 Oct 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 4 color = #E6DB74>[</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>10:00:00] </font><font size = 4 color = #66D9EF>submit report</font><br><font size = 4 color = #CECEF6>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp@ school</font><br>");

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
				"<font size = 4 color = #A6E22E>Tasks were deleted from TasKoord:</font><br><br><font size = 4 color = #FD971F>===GOOD-TO-DO TASKS===</font><br><br><font size = 4 color = #66D9EF>read Harry Potter</font><br>");

		/*
		 * tests for "list" partition
		 */
		// boundary case
		ui.executeCommands("list");
		assertEquals(
				ui.getDisplayedMessage(),
				"<br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Fri, 24 Oct 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 4 color = #E6DB74>[09:00:00</font><font size = 4 color = #E6DB74> - 10:00:00] </font><font size = 4 color = #66D9EF>Meeting</font><br><font size = 4 color = #CECEF6>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp@ Boardroom</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Sun, 26 Oct 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 4 color = #E6DB74>[</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>&nbsp</font><font size = 4 color = #E6DB74>10:00:00] </font><font size = 4 color = #66D9EF>submit report</font><br><font size = 4 color = #CECEF6>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp@ school</font><br>");

		// list upcoming task
		ui.executeCommands("list upcoming tasks for next 2 days");
		assertEquals(
				ui.getDisplayedMessage(),
				"<br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Fri, 24 Oct 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 4 color = #E6DB74>[09:00:00</font><font size = 4 color = #E6DB74> - 10:00:00] </font><font size = 4 color = #66D9EF>Meeting</font><br><font size = 4 color = #CECEF6>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp@ Boardroom</font><br>");

	}
}
