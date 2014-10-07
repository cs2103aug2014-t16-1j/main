package theUI;

import tkLibrary.Task;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.awt.*;

public class Gui {
    final String NO_COMMAND = "";
    private JFrame frame = new JFrame();
    private JTextField commandBox = new JTextField();//new HintTextField("Enter Command here");
    private JTextArea historyBox = new JTextArea();
    private JEditorPane displayBox = new JEditorPane();
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

    public void display(String text) {
        displayBox.setText(text);
    }

    public void display(Task task) {
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
