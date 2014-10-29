import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.junit.Test;

import storage.StorageTest;
import theUI.UITest;
import theUI.UserInterface;
import tkLogic.LogicTest;
import tkLogic.ParserTest;

public class TasKoordIntegrationTest {
    private Scanner scanner;
    private PrintWriter printWriter;

    @Test
    public void IntegrationTest() throws IOException {
        UserInterface ui = new UserInterface("IntegrationTest.txt");
        testCommandClear(ui);
        testSimpleAdd(ui);
    }

    private void testSimpleAdd(UserInterface ui) throws FileNotFoundException {
        ui.executeCommands("add Meeting from 9am to 10am on 24 Oct 2015 at Boardroom");
        readFile("IntegrationTest.txt");
        String[] expectedData =
                { "STARTTIME", "Oct 24 2015 09:00:00", "ENDTIME",
                        "Oct 24 2015 10:00:00", "LOCATION", "Boardroom",
                        "DESCRIPTION", "Meeting", "STATE_TYPE", "PENDING",
                        "PRIORITY", "MEDIUM", "END" };
        for (int i = 0; i < expectedData.length; i++) {
            assertEquals(expectedData[i], scanner.nextLine());
        }
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }

    private void testCommandClear(UserInterface ui) throws IOException,
            FileNotFoundException {
        writeToFile("IntegrationTest.txt");
        printWriter.println("A randome message!");
        closeWrittenFile();
        ui.executeCommands("clear");
        readFile("IntegrationTest.txt");
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }

    private void writeToFile(String fileName) throws IOException {
        printWriter = new PrintWriter(new FileWriter(fileName, true));
    }

    private void closeWrittenFile() {
        printWriter.close();
    }

    private void readFile(String fileName) throws FileNotFoundException {
        scanner = new Scanner(new File(fileName));
    }

    private void closeReadFile() {
        scanner.close();
    }

    @Test
    public void individualTest() {
        testStorage();
        testUI();
        testLogic();
        testParser();
    }

    private void testParser() {
        ParserTest parserTest = new ParserTest();
        parserTest.testParserAdd();
        parserTest.testParserAddDeadline();
        parserTest.testParserAddGoodToDo();
        parserTest.testParserAddTwoDates();
        parserTest.testParserDelete();
        parserTest.testParserEdit();
        parserTest.testParserSetPriority();
        parserTest.testParserSetState();
        parserTest.testParserUndo();
    }

    private void testLogic() {
        LogicTest logicTest = new LogicTest();
        logicTest.test();
    }

    private void testUI() {
        UITest uiTest = new UITest();
        uiTest.initObj();
        uiTest.testAddCommand();
    }

    private void testStorage() {
        StorageTest storageTest = new StorageTest();
        storageTest.testAdd();
    }

}
