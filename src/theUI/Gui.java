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
    private String NO_COMMAND = "";
    private JFrame frame = new JFrame();
    private JTextField commandBox = new JTextField();//new HintTextField("Enter Command here");
    private JTextArea historyBox = new JTextArea();
    private JTextPane displayBox = new JTextPane();
    private String displayText = "";
    private String userCommand = NO_COMMAND;
    
    private final Color COLOR_BACKGROUND = Color.darkGray;
    private final Color COLOR_FOREGROUND = Color.white;
    private final String COLOR_DONE = "green";
    private final String COLOR_WARNING = "red";
    
    
    public Gui() {
        initilize();
        listenToUserCommand();
        addToFrame();
    }

    public void initilize() {
        historyBox.setEditable(true);
        commandBox.setEditable(true);
        displayBox.setEditable(false);

        commandBox.setPreferredSize( new Dimension(400, 30 ) );
        displayBox.setPreferredSize( new Dimension(400, 400) );
        
        displayBox.setContentType("text/html");
        
        commandBox.setBackground(COLOR_BACKGROUND);
        displayBox.setBackground(COLOR_BACKGROUND);
        commandBox.setForeground(COLOR_FOREGROUND);
        displayBox.setForeground(COLOR_FOREGROUND);
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        BoxLayout boxLayout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS);
        frame.setLayout(boxLayout);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addToFrame() {
        frame.add(displayBox);
        frame.add(commandBox);
        frame.pack();
        frame.setVisible(true);
        displayBox.setText("haha + hehe");
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
    		displayText += format(text, COLOR_WARNING);
    	} else {
    		displayText = format(text, COLOR_WARNING);
    	}
    	displayBox.setText(displayText);
    }
    
    public void displayDone(String text, boolean isAppended) {
    	if (isAppended) {
    		displayText += format(text, COLOR_DONE);
    	} else {
    		displayText = format(text, COLOR_DONE);
    	}
    	displayBox.setText(displayText);
    }

    public void display(Task task, boolean isAppended) {
    	String res = "";
    	if (task.getStartTime() != null) {
    		res += "Time: " + convertCalendarToString(task.getStartTime(), Constants.FORMAT_EEE);
    	}
    	if (task.getEndTime() != null) {
    		res += " - " + convertCalendarToString(task.getEndTime(), Constants.FORMAT_EEE);
    	}
    	
    	if (!res.equals("")) {
    		res += "<br>";
    	}
    	
    	if (task.getDescription() != null) {
    		res += "Description: " + task.getDescription() + "<br>";
    	}
    	
    	if (task.getLocation() != null) {
    		res += "Location: " + task.getLocation() + "<br>";
    	}
    	
    	if (isAppended) {
    		displayText += res;
    	} else {
    		displayText = res;
    	}
    	displayBox.setText(displayText);
    }
    
    public void display(ArrayList<Task> lists, boolean isAppended) {
    	clearDisplayBox();
    	for (Task task : lists) {
    		display(task, true);
    	}
    }
 
    public void clearDisplayBox() {
    	displayText = "";
        displayBox.setText(displayText);
    }


    public String getUserCommand() {
        return userCommand;
    }

    public void setUserCommand(String command) {
        userCommand = command;
    }

    private void clearCommandBox() {
        commandBox.setText(NO_COMMAND);
    }
    
	private String convertCalendarToString(Calendar time, String FORMAT) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);     
		return formatter.format(time.getTime());
	}
	
	private String format(String text, String color) {
		return "<font color = " + color + ">" + text + "</font><br>";
	}
}
