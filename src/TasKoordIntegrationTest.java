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
import tkLibrary.Constants;
import tkLogic.LogicTest;
import tkLogic.ParserTest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TasKoordIntegrationTest {
    private Scanner scanner;
    private PrintWriter printWriter;

    @Test
    public void IntegrationTest() throws IOException {
        UserInterface ui = new UserInterface("IntegrationTest.txt");
        testCommandClear(ui);
        testSimpleAdd(ui);
        testDelete(ui);
    }

    @SuppressWarnings("unchecked")
    private void testDelete(UserInterface ui) throws FileNotFoundException {
        ui.executeCommands("add Lunch from 10am to 11am on 25 Oct 2015 at Boardroom");
        ui.executeCommands("delete Meeting");
        readFile("IntegrationTest.txt");
        JSONObject expData = new JSONObject();
        expData.put(Constants.STARTTIME, "Oct 25 2015 10:00:00");
        expData.put(Constants.ENDTIME, "Oct 25 2015 11:00:00");
        expData.put(Constants.LOCATION, "Boardroom");
        expData.put(Constants.DESCRIPTION, "Lunch");
        expData.put(Constants.STATE_TYPE, "PENDING");
        expData.put(Constants.PRIORITY_TYPE, "MEDIUM");
        JSONObject actualData = readObject();
        assertEquals(expData.get(Constants.STARTTIME).toString(),actualData.get(Constants.STARTTIME).toString());
        assertEquals(expData.get(Constants.ENDTIME).toString(),actualData.get(Constants.ENDTIME).toString());
        assertEquals(expData.get(Constants.LOCATION).toString(),actualData.get(Constants.LOCATION).toString());
        assertEquals(expData.get(Constants.DESCRIPTION).toString(),actualData.get(Constants.DESCRIPTION).toString());
        assertEquals(expData.get(Constants.STATE_TYPE).toString(),actualData.get(Constants.STATE_TYPE).toString());
        assertEquals(expData.get(Constants.PRIORITY_TYPE).toString(),actualData.get(Constants.PRIORITY_TYPE).toString());
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }

    @SuppressWarnings("unchecked")
    private void testSimpleAdd(UserInterface ui) throws FileNotFoundException {
        ui.executeCommands("add Meeting from 9am to 10am on 24 Oct 2015 at Boardroom");
        readFile("IntegrationTest.txt");
        JSONObject expData = new JSONObject();
        expData.put(Constants.STARTTIME, "Oct 24 2015 09:00:00");
        expData.put(Constants.ENDTIME, "Oct 24 2015 10:00:00");
        expData.put(Constants.LOCATION, "Boardroom");
        expData.put(Constants.DESCRIPTION, "Meeting");
        expData.put(Constants.STATE_TYPE, "PENDING");
        expData.put(Constants.PRIORITY_TYPE, "MEDIUM");
        JSONObject actualData = readObject();
        assertEquals(expData.get(Constants.STARTTIME).toString(),actualData.get(Constants.STARTTIME).toString());
        assertEquals(expData.get(Constants.ENDTIME).toString(),actualData.get(Constants.ENDTIME).toString());
        assertEquals(expData.get(Constants.LOCATION).toString(),actualData.get(Constants.LOCATION).toString());
        assertEquals(expData.get(Constants.DESCRIPTION).toString(),actualData.get(Constants.DESCRIPTION).toString());
        assertEquals(expData.get(Constants.STATE_TYPE).toString(),actualData.get(Constants.STATE_TYPE).toString());
        assertEquals(expData.get(Constants.PRIORITY_TYPE).toString(),actualData.get(Constants.PRIORITY_TYPE).toString());
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }

    private void testCommandClear(UserInterface ui) throws IOException, FileNotFoundException {
        writeToFile("IntegrationTest.txt");
        printWriter.println("A randome message!");
        closeWrittenFile();
        ui.executeCommands("clear");
        readFile("IntegrationTest.txt");
        assertEquals(false, scanner.hasNext());
        closeReadFile();
    }
    
    private JSONObject readObject(){
    	JSONParser parser = new JSONParser();
		try{
			String line = scanner.nextLine();
			Object obj = parser.parse(line.trim());
			JSONObject jsonObject = (JSONObject) obj;
			return jsonObject;
		} catch(ParseException e){
			System.out.println("Parse Exception:");
			e.printStackTrace();
			return null;
		}
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
        storageTest.testStorageAdd();
        storageTest.testStorageDeleteandUndo();
        storageTest.testStorageEdit();
        storageTest.testStorageSet();
        storageTest.clear();
    }

}
