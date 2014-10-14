package theUI;

import tkLibrary.Constants;
import tkLibrary.Task;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Gui {
    private final Color COLOR_BACKGROUND = new Color(0x272822);
    private final Color COLOR_FOREGROUND = new Color(0xF8F8F0);
    private final String COLOR_DONE = "#A6E22E";
    private final String COLOR_WARNING = "#F92672";
    private final String COLOR_HOUR = "#E6DB74";
    private final String COLOR_DESCRIPTION = "#66D9EF";
    private final String COLOR_LOCATION = "#F8F8F0";
    private final String COLOR_DATE = "#FD971F";
    
    private final int SPACE_BEFORE_LOCATION = 20;
    
    private final String STYLE = "<head><style>"
							+ "p  { font-family:consolas; font-size:100%; }"
							+ "</style></head>";
    private final Font COMMAND_FONT = new Font("consolas", Font.TRUETYPE_FONT, 19);
	
	
    private String NO_COMMAND = "";
    private JFrame frame = new JFrame("TasKoord");
    private JTextField commandBox = new JTextField();
    private JTextArea historyBox = new JTextArea();
    private JTextPane displayBox = new JTextPane();
    private String displayText = "";
    private String userCommand = NO_COMMAND; 
    
    public Gui() {
        initialize();
        listenToUserCommand();
        addToFrame();
    }
    
    public void initialize() {
        historyBox.setEditable(true);
        commandBox.setEditable(true);
        displayBox.setEditable(false);

        commandBox.setPreferredSize( new Dimension(450, 30 ) );
        displayBox.setPreferredSize( new Dimension(450, 600) );
        
        displayBox.setContentType("text/html");
        commandBox.setFont(COMMAND_FONT);
        commandBox.setCaretColor(Color.white);
        
        commandBox.setBackground(COLOR_BACKGROUND);
        displayBox.setBackground(COLOR_BACKGROUND);
        commandBox.setForeground(COLOR_FOREGROUND);
        displayBox.setForeground(COLOR_FOREGROUND);
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addToFrame() {
    	Container content = frame.getContentPane(); 
    	BorderLayout border = new BorderLayout();
        content.setLayout(border);
        
        content.add(commandBox, BorderLayout.SOUTH);
        frame.add(new JScrollPane(displayBox));
        
        frame.pack();
        frame.setVisible(true);
    }

    public void listenToUserCommand() {
        commandBox.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {
                System.out.println("Text=" + commandBox.getText());
                setUserCommand(commandBox.getText());
                clearCommandBox();
            }
        }
        );
    }

    public void displayWarning(String text, boolean isAppended) {
    	if (isAppended) {
    		displayText += formatWithNewLine(text, COLOR_WARNING);
    	} else {
    		displayText = formatWithNewLine(text, COLOR_WARNING);
    	}
    	setDisplayBox();
    }
    
    public void displayDone(String text, boolean isAppended) {
    	if (isAppended) {
    		displayText += formatWithNewLine(text, COLOR_DONE);
    	} else {
    		displayText = formatWithNewLine(text, COLOR_DONE);
    	}
    	setDisplayBox();
    }
    
    private void display(Task task) {
    	String res = "";
    	if (task.getStartTime() != null) {
    		res += 	format("[" + convertCalendarToString(task.getStartTime(), Constants.FORMAT_HOUR), COLOR_HOUR);
    	}
    	if (task.getEndTime() != null) {
    		res += format(" - " + convertCalendarToString(task.getEndTime(), Constants.FORMAT_HOUR) + "] ", COLOR_HOUR);
    	} else {
    		res += format("           ] ", COLOR_HOUR);
    	}
    	
    	if (task.getDescription() != null) {
    		res += formatWithNewLine(task.getDescription(), COLOR_DESCRIPTION);
    	}
    	
    	if (task.getLocation() != null) {
    		String rr = "";
    		for(int i = 1; i <= SPACE_BEFORE_LOCATION; i ++) {
    			rr += "&nbsp";
    		}
    		res += formatWithNewLine(rr + "@ " + task.getLocation(), COLOR_LOCATION);
    	}
    	
    	displayText += res;
    }
    
    public void display(ArrayList<Task> lists, boolean isAppended) {
    	if (!isAppended) {
    		displayText = "";
    	}
    	
    	Task preTask = null, curTask;
    	for (int i = 0; i < lists.size(); i ++) {
    		if (!checkFloatingTask(lists.get(i))) {
    			curTask = lists.get(i);
    			if (preTask == null || !convertCalendarToString(preTask.getStartTime(), Constants.FORMAT_EEE)
    					 .equals(convertCalendarToString(curTask.getStartTime(), Constants.FORMAT_EEE))) {
    				displayText += "<br>" 
    					+ format("======", COLOR_DATE)
    					+ format("["+convertCalendarToString(curTask.getStartTime(), Constants.FORMAT_EEE)+"]", COLOR_DATE)
    					+ format("======", COLOR_DATE)
    					+ "<br><br>";
    			}
    			display(curTask);
    			preTask = curTask;
    		}
    	}
    	
    	boolean flag = true;
    	for (int i = 0; i < lists.size(); i ++) {
    		if (checkFloatingTask(lists.get(i))) {
    			if (flag) {
    				displayText += "<br>" + formatWithNewLine("===GOOD-TO-DO TASKS===", COLOR_DATE) + "<br>";
    				flag = false;
    			}
    			displayText += format(lists.get(i).getDescription(), COLOR_DESCRIPTION);
    			if (lists.get(i).getLocation() != null) {
    				displayText += format(" @ " + lists.get(i).getDescription(), COLOR_LOCATION);
    			} else {
    				displayText += "<br>";
    			}
    		}
    	}
    
    	setDisplayBox();
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
	
	private String format(String text, String color) {
		return "<font color = " + color + ">" + text + "</font>";
	}
	
	private String formatWithNewLine(String text, String color) {
		return "<font color = " + color + ">" + text + "</font><br>";
	}
}
