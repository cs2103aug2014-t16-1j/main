package theUI;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

//@author A0112068N
public class UITest {

	private UserInterface ui;

	@Before
	public void initObj() {
		ui = new UserInterface("testing.txt");
		ui.executeCommands("clear");
	}

	@Test
	public void testAddCommand() {
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
		ui.executeCommands("add submit V0.5 by 10/11/14");
		assertEquals(
				ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Task added to TasKoord!</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Mon, 10 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><b><u><font size = 3 color = #A6E22E>01. </font></u></b><b><u><font size = 4 color = #E6DB74>[</font></u></b><b><u><font size = 4 color = #E6DB74>23:59]</font></u></b><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b><u><font size = 4 color = #66D9EF>submit V0.5</font></u></b><br>");

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
				"<br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Mon, 10 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>01. </font><font size = 4 color = #E6DB74>[</font><font size = 4 color = #E6DB74>23:59]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>submit V0.5</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Sat, 24 Oct 2015]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>02. </font><font size = 4 color = #E6DB74>[09:00</font><font size = 4 color = #E6DB74> - 10:00]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>Meeting</font><font size = 4 color = #CECEF6> @ Boardroom</font><br>");
		
		// list upcoming task
		ui.executeCommands("list upcoming tasks for next 365 days");
		assertEquals(
				ui.getDisplayedMessage(),
				"<br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Mon, 10 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>01. </font><font size = 4 color = #E6DB74>[</font><font size = 4 color = #E6DB74>23:59]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>submit V0.5</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Sat, 24 Oct 2015]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>02. </font><font size = 4 color = #E6DB74>[09:00</font><font size = 4 color = #E6DB74> - 10:00]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>Meeting</font><font size = 4 color = #CECEF6> @ Boardroom</font><br>");
		
		// list on specific day
		ui.executeCommands("list on 10/11/14");
		assertEquals(ui.getDisplayedMessage(),
				"<br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Mon, 10 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>01. </font><font size = 4 color = #E6DB74>[</font><font size = 4 color = #E6DB74>23:59]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>submit V0.5</font><br>");
		
		/*
		 * tests for "search"
		 */
		ui.executeCommands("add go fishing from 5pm to 7pm on 14/11/14");
		ui.executeCommands("add homework from 9pm to 11pm on 13/11/14");
		ui.executeCommands("add housework from 9pm to 11pm on 12/11/14");
		ui.executeCommands("search work");
		assertEquals(ui.getDisplayedMessage(),
				"<br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Wed, 12 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>01. </font><font size = 4 color = #E6DB74>[21:00</font><font size = 4 color = #E6DB74> - 23:00]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>housework</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Thu, 13 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><font size = 3 color = #A6E22E>02. </font><font size = 4 color = #E6DB74>[21:00</font><font size = 4 color = #E6DB74> - 23:00]</font><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<font size = 4 color = #66D9EF>homework</font><br>");
		
		/*
		 * tests for "undo"
		 */
		ui.executeCommands("list");
		ui.executeCommands("delete 4");
		ui.executeCommands("undo");
		assertEquals(ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Task restored.</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Fri, 14 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><b><u><font size = 3 color = #A6E22E>01. </font></u></b><b><u><font size = 4 color = #E6DB74>[17:00</font></u></b><b><u><font size = 4 color = #E6DB74> - 19:00]</font></u></b><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b><u><font size = 4 color = #66D9EF>go fishing</font></u></b><br>");
		
		/*
		 * tests for "redo"
		 */
		ui.executeCommands("redo");
		assertEquals(ui.getDisplayedMessage(),
				"<font size = 4 color = #A6E22E>Tasks were deleted from TasKoord:</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Fri, 14 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><strike><font size = 3 color = #A6E22E>01. </font></strike><strike><font size = 4 color = #E6DB74>[17:00</font></strike><strike><font size = 4 color = #E6DB74> - 19:00]</font></strike><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<strike><font size = 4 color = #66D9EF>go fishing</font></strike><br>");
		
		// boundary case: no task to redo
		ui.executeCommands("redo");
		assertEquals(ui.getDisplayedMessage(),
				"<font size = 4 color = #F92672>No command to redo</font><br>");
		
		/*
		 * tests for "edit"
		 */
		ui.executeCommands("list");
		ui.executeCommands("edit 3 correct from 8pm to 11pm on 13/11/14");
		assertEquals(ui.getDisplayedMessage(), 
				"<font size = 4 color = #F92672>Warning: timeslot of edited task is taken, task still added to TasKoord.</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Thu, 13 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><b><u><font size = 3 color = #A6E22E>01. </font></u></b><b><u><font size = 4 color = #E6DB74>[20:00</font></u></b><b><u><font size = 4 color = #E6DB74> - 23:00]</font></u></b><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b><u><font size = 4 color = #66D9EF>homework</font></u></b><br>");
		ui.executeCommands("edit homework correct homework abt math");
		assertEquals(ui.getDisplayedMessage(), 
				"<font size = 4 color = #A6E22E>Task edited!</font><br><br><font size = 4 color = #FD971F>======</font><font size = 4 color = #FD971F>[Thu, 13 Nov 2014]</font><font size = 4 color = #FD971F>======</font><br><br><b><u><font size = 3 color = #A6E22E>01. </font></u></b><b><u><font size = 4 color = #E6DB74>[20:00</font></u></b><b><u><font size = 4 color = #E6DB74> - 23:00]</font></u></b><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b><u><font size = 4 color = #66D9EF>homework abt math</font></u></b><br>");
		
	}
}
