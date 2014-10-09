package theUI;

import tkLibrary.Task;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

public class Gui {
    private String NO_COMMAND = "";
    private JFrame frame = new JFrame();
    private JTextField commandBox = new JTextField();//new HintTextField("Enter Command here");
    private JTextArea historyBox = new JTextArea();
    private JTextArea displayBox = new JTextArea();
    private String userCommand = NO_COMMAND;

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
        //history.setPreferredSize( new Dimension(100, 100) );
        
        //displayBox.setContentType("text/html");
        
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
    	displayBox.append(text+"\n");
    }
    
    public void displayDone(String text, boolean isAppended) {
    	displayBox.append(text+"\n");
    }

    public void display(Task task, boolean isAppended) {
    	String res = "";
    	if (task.getStartTime() != null) {
    		res += task.getStartTime().getTime();
    	}
    	if (task.getEndTime() != null) {
    		res += " - " + task.getEndTime().getTime();
    	}
    	
    	if (!res.equals("")) {
    		res += "\n";
    	}
    	
    	if (task.getDescription() != null) {
    		res += "Description: " + task.getDescription() + "\n";
    	}
    	
    	if (task.getLocation() != null) {
    		res += "Location: " + task.getLocation() + "\n";
    	}
    	
    	displayBox.append("\n" + res);
    }
    
    public void display(ArrayList<Task> lists, boolean isAppended) {
    	displayBox.setText("");
    	for (Task task : lists) {
    		display(task, true);
    	}
    }
 
    public void clearDisplayBox() {
        displayBox.setText("");
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
}
