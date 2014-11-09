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

//@author A0110493N
public class TasKoordIntegrationTest {
    private final String fileName = "IntegrationTest.txt";
    private Scanner scanner;
    private PrintWriter printWriter;

    @Test
    public void IntegrationTest() throws IOException {
        UserInterface ui = new UserInterface(fileName);
        testCommandClear(ui);
        testSimpleAdd(ui);
        testDelete(ui);
        testEdit(ui);
        testUndo(ui);
    }

    private void testUndo(UserInterface ui) throws FileNotFoundException {
        ui.executeCommands("undo");
        readFile(fileName);
        String nextLine = scanner.nextLine();
        assertEquals(
        		"{\"LOCATION\":\"Boardroom\",\"STARTTIME\":\"Oct 25 2015 10:00\",\"FREQUENCY_TYPE\":null,"
                		+ "\"STATE_TYPE\":\"PENDING\",\"DESCRIPTION\":\"Lunch\",\"ENDTIME\":\"Oct 25 2015 11:00\","
                		+ "\"FREQUENCY\":0,\"PRIORITY\":\"MEDIUM\","
                		+ "\"SYNC\":0}", nextLine);
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }

    private void testEdit(UserInterface ui) throws FileNotFoundException {
        ui.executeCommands("Edit Lunch correct dinner from 6pm to 7pm on 26 Nov 2015 at Restaurant");
        readFile(fileName);
        String nextLine = scanner.nextLine();
        assertEquals(
        		"{\"LOCATION\":\"Restaurant\",\"STARTTIME\":\"Nov 26 2015 18:00\",\"FREQUENCY_TYPE\":null,"
                		+ "\"STATE_TYPE\":\"PENDING\",\"DESCRIPTION\":\"dinner\",\"ENDTIME\":\"Nov 26 2015 19:00\","
                		+ "\"FREQUENCY\":0,\"PRIORITY\":\"MEDIUM\","
                		+ "\"SYNC\":0}", nextLine);
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }

    private void testDelete(UserInterface ui) throws FileNotFoundException {
        ui.executeCommands("add Lunch from 10am to 11am on 25 Oct 2015 at Boardroom");
        ui.executeCommands("delete Meeting");
        readFile(fileName);
        String nextLine = scanner.nextLine();
        assertEquals(
        		"{\"LOCATION\":\"Boardroom\",\"STARTTIME\":\"Oct 25 2015 10:00\",\"FREQUENCY_TYPE\":null,"
                		+ "\"STATE_TYPE\":\"PENDING\",\"DESCRIPTION\":\"Lunch\",\"ENDTIME\":\"Oct 25 2015 11:00\","
                		+ "\"FREQUENCY\":0,\"PRIORITY\":\"MEDIUM\","
                		+ "\"SYNC\":0}", nextLine);
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }

    private void testSimpleAdd(UserInterface ui) throws FileNotFoundException {
        ui.executeCommands("add Meeting from 9am to 10am on 24 Oct 2015 at Boardroom");
        readFile(fileName);
        String nextLine = scanner.nextLine();
        assertEquals(
        		"{\"LOCATION\":\"Boardroom\",\"STARTTIME\":\"Oct 24 2015 09:00\",\"FREQUENCY_TYPE\":null,"
                		+ "\"STATE_TYPE\":\"PENDING\",\"DESCRIPTION\":\"Meeting\",\"ENDTIME\":\"Oct 24 2015 10:00\","
                		+ "\"FREQUENCY\":0,\"PRIORITY\":\"MEDIUM\","
                		+ "\"SYNC\":0}", nextLine);
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }

    private void testCommandClear(UserInterface ui) throws IOException,
            FileNotFoundException {
        writeToFile(fileName);
        printWriter.println("A randome message!");
        closeWrittenFile();
        ui.executeCommands("clear");
        readFile(fileName);
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
        parserTest.testParserAddSpecialDateFormat();
        parserTest.testParserAddIncorrectTimeFormat20pm();
        parserTest.testParserAddDescriptionWithKeys();
        parserTest.testParserAddWithDateNotTime();
        parserTest.testParserDelete();
        parserTest.testParserEdit();
        parserTest.testParserSetPriority();
        parserTest.testParserSetState();
        parserTest.testParserUndo();
        parserTest.testParserClear();
        parserTest.testParserSearchNull();
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
        storageTest.testStorageAdd();
        storageTest.testStorageDeleteandUndo();
        storageTest.testStorageEdit();
        storageTest.testStorageSet();
        storageTest.clear();
    }

}
