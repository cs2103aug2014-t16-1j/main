import static org.junit.Assert.*;

import org.junit.Test;

import storage.StorageTest;
import theUI.UITest;
import tkLogic.LogicTest;
import tkLogic.ParserTest;


public class TasKoordIntegrationTest {

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
