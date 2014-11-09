package theUI;

import tkLibrary.Constants;
import tkLibrary.PriorityType;
import tkLibrary.StateType;
import tkLibrary.Task;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import GCal.SwingBrowser;

import java.awt.*;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

//@author A0112068N
/**
 *         This class used to communicate with user.
 */
public class Gui {

	private static final String HEADER_GOOD_TO_DO_TASKS = "===GOOD-TO-DO TASKS===";
	private static final String NEW_LINE = "<br>";
	private static final String SPACE = "&nbsp";
	private static final String HTML_TYPE = "text/html";
	private static final String NO_COMMAND = "";

	// constants for color, size.
	private static final int HEIGHT = 800;
	private static final int WIDTH = 500;
	private static final Color COLOR_CARET = Color.white;
	private static final Color COLOR_BACKGROUND = new Color(0x272822);
	private static final Color COLOR_FOREGROUND = new Color(0xF8F8F0);
	private static final String COLOR_DONE = "#A6E22E";
	private static final String COLOR_WARNING = "#F92672";
	private static final String COLOR_HOUR = "#E6DB74";
	private static final String COLOR_DESCRIPTION = "#66D9EF";
	private static final String COLOR_LOCATION = "#CECEF6";
	private static final String COLOR_DATE = "#FD971F";
	private static final String SIZE_NORMAL = "4";
	private static final String SIZE_NUMBER = "3";
	private static final String COLOR_DESCRIPTION_HIGH = "#FE2E2E";
	private static final String COLOR_DESCRIPTION_LOW = "#A4A4A4";
	private static final String COLOR_STATE = "#A6E22E";
	private static final String helpFile = "help.html";
	private static final String STYLE = "<head><style>"
			+ "p  { font-family:consolas; font-size:100%; }"
			+ "</style></head>";
	private static final Font COMMAND_FONT = new Font("consolas",
			Font.TRUETYPE_FONT, 19);

	// graphic components.
	private JFrame frame = new JFrame("TasKoord");
	private JTextField commandBox = new JTextField();
	private JTextPane displayBox = new JTextPane();
	private SwingBrowser browser = new SwingBrowser();

	// stack and variables for calling previous command.
	private ArrayList<String> commandStack = new ArrayList<String>();
	private int currentPos = 0;
	private String currentCommand = "";
	private boolean upDownTyped = false;

	public String displayText = "";
	private String userCommand = NO_COMMAND;

	public Gui() {
		initialize();
		listenToUserCommand();
		addToFrame();
	}

	// initialize the color, size, .. of components.
	public void initialize() {
		commandBox.setEditable(true);
		displayBox.setEditable(false);

		displayBox.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		displayBox.setContentType(HTML_TYPE);
		commandBox.setFont(COMMAND_FONT);
		commandBox.setCaretColor(COLOR_CARET);

		commandBox.setBackground(COLOR_BACKGROUND);
		displayBox.setBackground(COLOR_BACKGROUND);
		commandBox.setForeground(COLOR_FOREGROUND);
		displayBox.setForeground(COLOR_FOREGROUND);

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// add components to the frame and show the GUI.
	public void addToFrame() {
		Container content = frame.getContentPane();
		BorderLayout border = new BorderLayout();
		content.setLayout(border);

		content.add(commandBox, BorderLayout.SOUTH);
		frame.add(new JScrollPane(displayBox));

		frame.pack();
		frame.setVisible(true);
	}

	// listen to user action.
	public void listenToUserCommand() {
		commandBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Text=" + commandBox.getText());
				commandStack.add(commandBox.getText());
				currentPos = commandStack.size();
				upDownTyped = false;
				setUserCommand(commandBox.getText());
				clearCommandBox();
			}
		});

		commandBox.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
					if (!upDownTyped) {
						currentCommand = commandBox.getText();
						upDownTyped = true;
					}
					if (currentPos - 1 >= 0) {
						currentPos--;
						commandBox.setText(commandStack.get(currentPos));
					}
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
					if (!upDownTyped) {
						currentCommand = commandBox.getText();
						upDownTyped = true;
					}
					if (currentPos + 1 < commandStack.size()) {
						currentPos++;
						commandBox.setText(commandStack.get(currentPos));
					} else {
						currentPos = commandStack.size();
						commandBox.setText(currentCommand);
					}
				} else if (currentPos == commandStack.size()) {
					currentCommand = commandBox.getText();
				}
			}

			public void keyReleased(KeyEvent keyEvent) {
			}

			public void keyTyped(KeyEvent keyEvent) {

			}
		});
	}
	
	public void runBrowser(String url) {
		browser.runBrowser(url);
	}
	
	public void setBrowserCode(String code) {
		browser.setCode(code);
	}
	
	public String getBrowserCode() {
		return browser.code;
	}

	public void displayWarning(String text, boolean isAppended) {
		if (isAppended) {
			displayText += formatWithNewLine(text, SIZE_NORMAL, COLOR_WARNING);
		} else {
			displayText = formatWithNewLine(text, SIZE_NORMAL, COLOR_WARNING);
		}
		setDisplayBox();
	}

	public void displayDone(String text, boolean isAppended) {
		if (isAppended) {
			displayText += formatWithNewLine(text, SIZE_NORMAL, COLOR_DONE);
		} else {
			displayText = formatWithNewLine(text, SIZE_NORMAL, COLOR_DONE);
		}
		setDisplayBox();
	}

	private void display(int no, Task task, int effect, boolean isIndexed) {
		String res = format(intToString(no, isIndexed), SIZE_NUMBER,
				COLOR_DONE, effect);

		if (task.getStartTime() != null) {
			if (task.getEndTime() == null) {
				res += format("[", SIZE_NORMAL, COLOR_HOUR, effect);
				res += format(
						convertCalendarToString(task.getStartTime(),
								Constants.FORMAT_HOUR) + "]", SIZE_NORMAL,
						COLOR_HOUR, effect);
			} else {
				res += format(
						"["
								+ convertCalendarToString(task.getStartTime(),
										Constants.FORMAT_HOUR), SIZE_NORMAL,
						COLOR_HOUR, effect);
			}
		}
		if (task.getEndTime() != null) {
			String startTimeString = convertCalendarToString(
					task.getStartTime(), Constants.FORMAT_DATE);
			String endTimeString = convertCalendarToString(task.getEndTime(),
					Constants.FORMAT_DATE);
			if (startTimeString.equals(endTimeString)) {
				res += format(
						" - "
								+ convertCalendarToString(task.getEndTime(),
										Constants.FORMAT_HOUR) + "]",
						SIZE_NORMAL, COLOR_HOUR, effect);
			} else {
				res += format(
						" - "
								+ convertCalendarToString(task.getEndTime(),
										Constants.FORMAT_DATE_DATE_AND_HOUR)
								+ "]", SIZE_NORMAL, COLOR_HOUR, effect);
			}
		}

		if (task.getState() != StateType.PENDING && task.getState() != null) {
			res += format("  [" + task.getState() + "]", SIZE_NORMAL,
					COLOR_STATE, effect);
		}
		if (task.getStartTime() != null) {
			res += NEW_LINE;
		}

		for (int i = 1; i <= 10; i++) {
			res += SPACE;
		}

		if (task.getDescription() != null) {
			String color = COLOR_DESCRIPTION;
			if (task.getPriorityLevel() == PriorityType.HIGH) {
				color = COLOR_DESCRIPTION_HIGH;
			} else if (task.getPriorityLevel() == PriorityType.LOW) {
				color = COLOR_DESCRIPTION_LOW;
			}
			res += format(task.getDescription(), SIZE_NORMAL, color, effect);
		}

		if (task.getLocation() != null) {
			res += format(" @ " + task.getLocation(), SIZE_NORMAL,
					COLOR_LOCATION, effect);
		}

		displayText += res + NEW_LINE;
	}

	public void display(ArrayList<Task> lists, int pos, int effect,
			boolean isAppended, boolean isIndexed) {
		if (!isAppended) {
			displayText = "";
		}

		Task preTask = null, curTask;
		for (int i = 0; i < lists.size(); i++) {
			if (!checkFloatingTask(lists.get(i))) {
				curTask = lists.get(i);
				if (preTask == null
						|| !convertCalendarToString(preTask.getStartTime(),
								Constants.FORMAT_EEE).equals(
								convertCalendarToString(curTask.getStartTime(),
										Constants.FORMAT_EEE))) {
					displayText += NEW_LINE
							+ format("======", SIZE_NORMAL, COLOR_DATE)
							+ format(
									"["
											+ convertCalendarToString(
													curTask.getStartTime(),
													Constants.FORMAT_EEE) + "]",
									SIZE_NORMAL, COLOR_DATE)
							+ format("======", SIZE_NORMAL, COLOR_DATE)
							+ NEW_LINE + NEW_LINE;
				}
				if (i == pos) {
					display(i + 1, curTask, effect, isIndexed);
				} else {
					display(i + 1, curTask, Constants.NO_EFFECT, isIndexed);
				}
				preTask = curTask;
			}
		}

		boolean flag = true;
		for (int i = 0; i < lists.size(); i++) {
			if (checkFloatingTask(lists.get(i))) {
				if (flag) {
					displayText += NEW_LINE
							+ formatWithNewLine(HEADER_GOOD_TO_DO_TASKS,
									SIZE_NORMAL, COLOR_DATE) + NEW_LINE;
					flag = false;
				}

				int eff = Constants.NO_EFFECT;
				if (i == pos) {
					eff = effect;
				}
				String color = COLOR_DESCRIPTION;
				if (lists.get(i).getPriorityLevel() == PriorityType.HIGH) {
					color = COLOR_DESCRIPTION_HIGH;
				} else if (lists.get(i).getPriorityLevel() == PriorityType.LOW) {
					color = COLOR_DESCRIPTION_LOW;
				}
				displayText += format(intToString(i + 1, isIndexed),
						SIZE_NUMBER, COLOR_DONE);
				displayText += format(lists.get(i).getDescription(),
						SIZE_NORMAL, color, eff);

				if (lists.get(i).getLocation() != null)
					displayText += format(" @ " + lists.get(i).getLocation(),
							SIZE_NORMAL, COLOR_LOCATION, eff);

				if (lists.get(i).getState() != StateType.PENDING) {
					displayText += format(
							" [" + lists.get(i).getState() + "] ", SIZE_NORMAL,
							COLOR_STATE, eff);
				}

				displayText += NEW_LINE;
			}
		}

		setDisplayBox();
	}

	private String intToString(int no, boolean isIndexed) {
		if (!isIndexed) {
			return "";
		}
		if (no < 10) {
			return "0" + no + ". ";
		} else {
			return Integer.toString(no) + ". ";
		}
	}

	private boolean checkFloatingTask(Task task) {
		return (task.getStartTime() == null);
	}

	private void setDisplayBox() {
		displayBox.setText(STYLE + "<p>" + displayText);
	}

	public void clearDisplayBox() {
		displayText = "";
		setDisplayBox();
	}

	private void clearCommandBox() {
		commandBox.setText(NO_COMMAND);
	}

	public String getUserCommand() {
		return userCommand;
	}

	public void setUserCommand(String command) {
		userCommand = command;
	}

	private String convertCalendarToString(Calendar time, String FORMAT) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);
		return formatter.format(time.getTime());
	}

	private String format(String text, String size, String color) {
		return "<font size = " + size + " color = " + color + ">" + text
				+ "</font>";
	}

	private String formatWithNewLine(String text, String size, String color) {
		return "<font size = " + size + " color = " + color + ">" + text
				+ "</font><br>";
	}

	private String format(String text, String size, String color, int effect) {
		String res = "<font size = " + size + " color = " + color + ">" + text
				+ "</font>";
		if (effect == Constants.HIGHLIGH) {
			return "<b><u>" + res + "</u></b>";
		} else if (effect == Constants.DELETED) {
			return "<strike>" + res + "</strike>";
		} else {
			return res;
		}
	}

	public void displayFile() {
		try {
			InputStream in = Gui.class.getResourceAsStream(helpFile);
			String text = convertStreamToString(in);
			displayBox.setText(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String convertStreamToString(java.io.InputStream is) {
		@SuppressWarnings("resource")
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
