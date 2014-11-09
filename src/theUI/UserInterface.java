package theUI;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import tkLibrary.CommandType;
import tkLibrary.Constants;
import tkLibrary.GcPacket;
import tkLibrary.LogFile;
import tkLibrary.PriorityType;
import tkLibrary.StateType;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLogic.Logic;
import tkLogic.Parser;

//@author A0112068N
/**
 *         This is UI Controller. That takes commands from GUI, implements
 *         commands using Parser and Logic, then sends GUI the feedbacks
 *
 */
public class UserInterface {
	
	// Constants for google calendar integration
	private static final String TASKS_ADDED_TO_GC = "<br>Tasks added to Google Calendar: ";
	private static final String TASKS_DELETED_FROM_GC = "<br>Tasks deleted from Google Calendar: ";
	private static final String TASKS_ADDED_TO_TK = "<br>Tasks added to TasKoord: ";
	private static final String TASKS_DELETED_FROM_TK = "<br>Tasks deleted from TasKoord: ";
	private static final String MESSAGE_SYNCING = "Syncing is in progress, it may take a few minutes to complete...";
	private static final String MESSAGE_NO_INTERNET = "No internet connection available!";
	private static final String MESSAGE_SYNC_ERROR = "Unable to sync, please try again!";
	private static final String MESSAGE_WAITING = "Please follow the link and accept it!";
	private static final String MESSAGE_SYNC_COMPLETE = "Synchronization is complete.";
	private static final String clientEmail = "914087031259-compute@developer.gserviceaccount.com";
	private static final String MESSAGE_SYNC_REMINDER = "Please ensure that you have shared your calendar with: "
			+ clientEmail;

	// Local constants
	private static final boolean APPENDED = true;
	private static final boolean INSERTED = false;
	private static final boolean INDEXED = true;
	private static final boolean NO_INDEX = false;
	private static final int COLOR_DONE = 1;
	private static final int COLOR_WARNING = 2;
	private static final int NO_TASK = -1;
	private static final int ALL_TASK = -2;
	

	// Constants for keywords
	private static final String KEYWORD_UPCOMING_TASK = "upcoming";
	private static final String KEYWORD_FLOATING_TASK = "floating";
	private static final String KEYWORD_INVALID_CMD = "invalid";

	// Some local messages that will be shown to user.
	private static final String MESSAGE_TASK_EDITED_FOR_UNDO = "Task edited.";
	private static final String MESSAGE_TASK_RESTORED_FOR_UNDO = "Task restored.";
	private static final String MESSAGE_TASK_DELETED_FOR_UNDO = "Task deleted.";
	private static final String MESSAGE_NO_UNDO = "No command to undo";
	private static final String MESSAGE_NO_REDO = "No command to redo";
	private static final String MESSAGE_TASK_RESTORED = "All tasks restored.";
	private static final String MESSAGE_INFORMAT_CMD = "Informat command";
	private static final String MESSAGE_WRONG_CMD = "Cannot understand the command!";
	private static final String MESSAGE_GOODBYE = "Good Bye !!!";
	private static final String MESSAGE_WELCOME = "Welcome to TasKoord!! <br> If this is your first time using TasKoord, "
			+ "please type 'help' for more information.";
	private static final String NO_COMMAND = "";

	// Components that interact with UI
	private Logic logic;
	private Parser parser;
	private Gui gui;
	
	// logger
	private static Logger LOGGER = Logger.getLogger(".TasKoordUILogFile.log");

	// The special packet for implementing undo and redo.
	private static class UndoAndRedoPack {
		public String statusForUndo, statusForRedo;
		public ArrayList<Task> tasksForUndo, tasksForRedo;
		public Integer effectForUndo, effectForRedo;
		public Integer posToDoEffectForUndo, posToDoEffectForRedo;
		public Integer colorForUndo, colorForRedo;

		public UndoAndRedoPack(int posForUndo, ArrayList<Task> listForUndo,
				int undoEffect, String messageForUndo, int ucolor,
				int posForRedo, ArrayList<Task> listForRedo, int redoEffect,
				String messageForRedo, int rcolor) {
			this.posToDoEffectForUndo = posForUndo;
			this.tasksForUndo = listForUndo;
			this.effectForUndo = undoEffect;
			this.statusForUndo = messageForUndo;
			this.colorForUndo = ucolor;

			this.posToDoEffectForRedo = posForRedo;
			this.tasksForRedo = listForRedo;
			this.effectForRedo = redoEffect;
			this.statusForRedo = messageForRedo;
			this.colorForRedo = rcolor;
		}
	}

	// Stack used for undo and redo
	private ArrayList<UndoAndRedoPack> stack;
	private ArrayList<Task> tasksOnScreen;
	private int currentPos;
	private int availablePos;

	public UserInterface(String fileName) {
		parser = Parser.getInstance();
		logic = new Logic(fileName);
		gui = new Gui();
		
		LogFile.newLogger();

		tasksOnScreen = new ArrayList<Task>();
		stack = new ArrayList<UndoAndRedoPack>();
		currentPos = availablePos = -1;
	}

	public void run() {
		showBeginScreen();
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, e.toString());
				Thread.currentThread().interrupt();
			}

			String userCommand = gui.getUserCommand();
			if (!userCommand.equals(NO_COMMAND)) {
				try {
					gui.setUserCommand(NO_COMMAND);
					executeCommands(userCommand);
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, e.toString());
				}
			}
		}
	}

	// Show welcome message and all tasks at the beginning
	private void showBeginScreen() {
		gui.displayDone(MESSAGE_WELCOME, INSERTED);
		ArrayList<Task> list = logic.load();
		if (!list.isEmpty()) {
			gui.display(list, NO_TASK, Constants.NO_EFFECT, APPENDED, INDEXED);
		}
	}

	// execute command received from GUI.
	public void executeCommands(String userCommand) {
		UserInput userInput;
		CommandType command;
		Task task;

		try {
			userInput = parser.format(userCommand);
			command = userInput.getCommand();
			task = userInput.getTask();
		} catch (Exception e) {
			if (e.getMessage().contains(KEYWORD_INVALID_CMD)) {
				gui.displayWarning(String.format(
						Constants.EXCEPTIONS_INVALID_USERCOMMAND, userCommand,
						e.getMessage()), INSERTED);
			} else {
				gui.displayWarning(MESSAGE_WRONG_CMD, INSERTED);
				e.printStackTrace();
			}
			return;
		}

		if (command == CommandType.ADD) {
			add(task);
		} else if (command == CommandType.DELETE) {
			delete(task);
		} else if (command == CommandType.UNDO) {
			undo();
		} else if (command == CommandType.EDIT) {
			Task newTask = userInput.getEditedTask();
			edit(task, newTask);
		} else if (command == CommandType.CLEAR) {
			clear();
		} else if (command == CommandType.LIST) {
			list(task);
		} else if (command == CommandType.SEARCH) {
			search(task);
		} else if (command == CommandType.SET) {
			set(task);
		} else if (command == CommandType.REDO) {
			redo();
		} else if (command == CommandType.SYNC) {
			sync();
		} else if (command == CommandType.HELP) {
			help();
		} else if (command == CommandType.EXIT) {
			gui.displayDone(MESSAGE_GOODBYE, INSERTED);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, e.toString());
				Thread.currentThread().interrupt();
			}
			System.exit(0);
		} else {
			gui.displayWarning(MESSAGE_INFORMAT_CMD, INSERTED);
		}
	}

	// sync with google calendar
	private void sync() {
		if (!logic.isOnline()) {
			gui.displayWarning(MESSAGE_NO_INTERNET, INSERTED);
		} else {
			gui.displayDone(MESSAGE_SYNCING, INSERTED);
			if (logic.connectUsingExistingToken()) {
				try {
					GcPacket packet = logic.sync(logic.load());
					gui.displayDone(MESSAGE_SYNC_COMPLETE, INSERTED);
					displayPacket(packet);
					return;
				} catch (IOException e) {
					syncWithNewToken(logic.getURL());
					e.printStackTrace();
				}
			} else {
				syncWithNewToken(logic.getURL());
			}
		}
	}

	private void syncWithNewToken(String url) {
		try {
			String code = getTokenPopup(url);
			if (code == Constants.CODE_NO_CODE) {
				gui.displayWarning(Constants.CODE_NO_CODE, INSERTED);
			} else if (code == Constants.CODE_REJECTED) {
				gui.displayWarning(Constants.CODE_REJECTED, INSERTED);
			} else {
				gui.displayDone(MESSAGE_SYNCING, INSERTED);

				logic.connectByNewToken(code);
				GcPacket packet = logic.sync(logic.load());

				gui.displayDone(MESSAGE_SYNC_COMPLETE, INSERTED);
				displayPacket(packet);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
			gui.displayWarning(MESSAGE_SYNC_ERROR, INSERTED);
			gui.displayWarning(MESSAGE_SYNC_REMINDER, APPENDED);
		}
	}

	// open browser to get authority from user.
	public String getTokenPopup(String url) {
		gui.displayDone(MESSAGE_WAITING, INSERTED);
		gui.displayDone(MESSAGE_SYNC_REMINDER, APPENDED);
		gui.runBrowser(url);
		while (true) {
			try {
				Thread.sleep(100);
				String code = gui.getBrowserCode();
				if (!code.equals(NO_COMMAND)) {
					gui.setBrowserCode(NO_COMMAND);
					return code;
				}
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, e.toString());
				Thread.currentThread().interrupt();
			}
		}
	}

	// display what were changed after syncing.
	private void displayPacket(GcPacket packet) {
		ArrayList<Task> list = new ArrayList<Task>();

		for (Task item : packet.taskDeletedFromTK) {
			if (logic.delete(item).equals(Constants.MESSAGE_TASK_DELETED)) {
				list.add(item);
			}
		}

		if (list.size() != 0) {
			gui.displayDone(TASKS_DELETED_FROM_TK, APPENDED);
			gui.display(list, NO_TASK, Constants.NO_EFFECT, APPENDED, NO_INDEX);
		}

		list.clear();
		for (Task item : packet.taskAddedToTK) {
			try {
				item.setPriority(Constants.PRIORITY_MEDIUM);
				item.setState(Constants.STATE_PENDING);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, e.toString());
			}
			if (!logic.add(item).equals(Constants.MESSAGE_DUPLICATED_TASK)) {
				list.add(item);
			}
		}
		if (list.size() != 0) {
			gui.displayDone(TASKS_ADDED_TO_TK, APPENDED);
			gui.display(list, NO_TASK, Constants.NO_EFFECT, APPENDED, NO_INDEX);
		}

		if (packet.taskDeletedFromGC.size() != 0) {
			gui.displayDone(TASKS_DELETED_FROM_GC, APPENDED);
			gui.display(packet.taskDeletedFromGC, NO_TASK, Constants.NO_EFFECT,
					APPENDED, NO_INDEX);
		}

		if (packet.taskAddedToGC.size() != 0) {
			gui.displayDone(TASKS_ADDED_TO_GC, APPENDED);
			gui.display(packet.taskAddedToGC, NO_TASK, Constants.NO_EFFECT,
					APPENDED, NO_INDEX);
		}

		logic.setSynced();
	}

	private void help() {
		gui.displayFile();
		tasksOnScreen.clear();
	}

	private void add(Task task) {
		if (task.getDescription() == null) {
			gui.displayWarning(Constants.MESSAGE_NO_ADD_INFO, INSERTED);
			tasksOnScreen.clear();
			return;
		}

		if (task.getStartTime() != null && task.getEndTime() != null) {
			if (task.getStartTime().compareTo(task.getEndTime()) >= 0) {
				gui.displayWarning(Constants.MESSAGE_ENDTIME_BEFORE_STARTTIME,
						INSERTED);
				gui.displayWarning("<br>", APPENDED);
				gui.displayWarning(
						"Start time: "
								+ convertCalendarToString(task.getStartTime(),
										Constants.FORMAT_DATE_DATE_AND_HOUR),
						APPENDED);
				gui.displayWarning(
						"End time  : "
								+ convertCalendarToString(task.getEndTime(),
										Constants.FORMAT_DATE_DATE_AND_HOUR),
						APPENDED);
				tasksOnScreen.clear();
				return;
			}
		}

		try {
			if (task.getState() == null) {
				task.setState(StateType.PENDING);
			}
			if (task.getPriorityLevel() == null) {
				task.setPriority(PriorityType.MEDIUM);
			}

			String feedback = logic.add(task);
			ArrayList<Task> list = searchTaskOfSameDay(task);

			if (feedback.equals(Constants.MESSAGE_TASK_ADDED)) {
				addToStackForUndoAndRedo(list, task, Constants.DELETED,
						MESSAGE_TASK_DELETED_FOR_UNDO, COLOR_DONE, list, task,
						Constants.HIGHLIGH, feedback, COLOR_DONE);
				gui.displayDone(feedback, INSERTED);
				showToUser(list, task, Constants.HIGHLIGH, APPENDED);
			} else if (feedback.equals(Constants.MESSAGE_CLASHING_TIMESLOTS)) {
				addToStackForUndoAndRedo(list, task, Constants.DELETED,
						MESSAGE_TASK_DELETED_FOR_UNDO, COLOR_DONE, list, task,
						Constants.HIGHLIGH, feedback, COLOR_WARNING);
				gui.displayWarning(feedback, INSERTED);
				showToUser(list, task, Constants.HIGHLIGH, APPENDED);
			} else if (feedback.equals(Constants.MESSAGE_NO_START_TIME)) {
				gui.displayWarning(feedback, INSERTED);
				tasksOnScreen.clear();
			} else {
				gui.displayWarning(feedback, INSERTED);
				showToUser(list, task, Constants.HIGHLIGH, APPENDED);
			}

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private ArrayList<Task> searchTaskOfSameDay(Task task) {
		Task taskToSearch = new Task();
		taskToSearch.setStartTime(task.getStartTime());
		taskToSearch.setEndTime(task.getEndTime());
		if (taskToSearch.getStartTime() == null) {
			taskToSearch.setDescription(KEYWORD_FLOATING_TASK);
		}
		ArrayList<Task> list = logic.list(taskToSearch);
		return list;
	}

	private void list(Task task) {
		try {
			String cmdInfo = task.getDescription();
			if (cmdInfo != null) {
				cmdInfo = cmdInfo.toLowerCase();
			}

			if (task.getState() == null && task.getStartTime() == null) {
				task.setState(StateType.PENDING);
			}

			if (cmdInfo != null && cmdInfo.contains(KEYWORD_UPCOMING_TASK)
					&& task.getStartTime() == null) {
				int time = Constants.LISTUPCOMINGTIME_DEFAULT;
				String s = getFirstInt(cmdInfo);
				if (!s.equals("")) {
					time = Integer.parseInt(s);
				}

				Calendar endTime = Calendar.getInstance();
				endTime.add(Calendar.DAY_OF_MONTH, time);
				task.setStartTime(Calendar.getInstance());
				task.setEndTime(endTime);
			}

			ArrayList<Task> result = logic.list(task);
			if (result.size() == 0) {
				gui.displayDone(Constants.MESSAGE_NO_RESULT_LIST, INSERTED);
				tasksOnScreen.clear();
			} else {
				showToUser(result, INSERTED);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private void delete(Task task) {
		if (task.getDescription() == null) {
			gui.displayWarning(Constants.MESSAGE_NO_DELETE_INFO, INSERTED);
			tasksOnScreen.clear();
			return;
		}
		try {
			ArrayList<Task> list = new ArrayList<Task>();

			if (isInteger(task.getDescription())) {
				int noOfTask = toInteger(task.getDescription());
				if (noOfTask <= tasksOnScreen.size()) {
					Task taskToBeDeleted = tasksOnScreen.get(noOfTask - 1);
					list = searchTaskOfSameDay(taskToBeDeleted);

					String feedback = logic.delete(taskToBeDeleted);
					if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
						addToStackForUndoAndRedo(list, taskToBeDeleted,
								Constants.HIGHLIGH,
								MESSAGE_TASK_RESTORED_FOR_UNDO, COLOR_DONE,
								list, taskToBeDeleted, Constants.DELETED,
								feedback, COLOR_DONE);
						gui.displayDone(feedback, INSERTED);
						showToUser(list, taskToBeDeleted, Constants.DELETED,
								APPENDED);
					} else {
						gui.displayWarning(feedback, INSERTED);
						tasksOnScreen.clear();
					}
					return;
				}
			}

			list = logic.search(task.getDescription());

			if (list.isEmpty()) {
				gui.displayWarning(Constants.MESSAGE_TASK_DOES_NOT_EXIST,
						INSERTED);
				tasksOnScreen.clear();
			} else if (list.size() == 1) {
				Task taskToBeDeleted = list.get(0);
				list = searchTaskOfSameDay(taskToBeDeleted);

				String feedback = logic.delete(taskToBeDeleted);
				if (feedback.equals(Constants.MESSAGE_TASK_DELETED)) {
					addToStackForUndoAndRedo(list, taskToBeDeleted,
							Constants.HIGHLIGH, MESSAGE_TASK_RESTORED_FOR_UNDO,
							COLOR_DONE, list, taskToBeDeleted,
							Constants.DELETED, feedback, COLOR_DONE);
					gui.displayDone(feedback, INSERTED);
					showToUser(list, taskToBeDeleted, Constants.DELETED,
							APPENDED);
				} else {
					gui.displayWarning(feedback, INSERTED);
					tasksOnScreen.clear();
				}
			} else {
				gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND,
						INSERTED);
				showToUser(list, APPENDED);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private void edit(Task task, Task updatedTask) {
		try {
			ArrayList<Task> list = new ArrayList<Task>();

			if (isInteger(task.getDescription())) {
				int noOfTask = toInteger(task.getDescription());
				if (noOfTask <= tasksOnScreen.size()) {
					Task taskToBeEdited = new Task(
							tasksOnScreen.get(noOfTask - 1));

					ArrayList<Task> oldList = searchTaskOfSameDay(taskToBeEdited);
					Task oldTask = new Task(taskToBeEdited);

					String feedback = logic.edit(taskToBeEdited, updatedTask);
					taskToBeEdited.update(updatedTask);

					list = searchTaskOfSameDay(taskToBeEdited);

					displayFeedbackForEditing(oldList, oldTask, list,
							taskToBeEdited, feedback);
					return;
				}
			}

			list = logic.search(task.getDescription());
			if (list.size() == 0) {
				gui.displayDone(Constants.MESSAGE_TASK_DOES_NOT_EXIST, INSERTED);
				tasksOnScreen.clear();
			} else if (list.size() == 1) {
				Task taskToBeEdited = new Task(list.get(0));

				ArrayList<Task> oldList = searchTaskOfSameDay(taskToBeEdited);
				Task oldTask = new Task(taskToBeEdited);

				String feedback = logic.edit(taskToBeEdited, updatedTask);
				taskToBeEdited.update(updatedTask);

				list = searchTaskOfSameDay(taskToBeEdited);

				displayFeedbackForEditing(oldList, oldTask, list,
						taskToBeEdited, feedback);
			} else {
				gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND,
						INSERTED);
				showToUser(list, APPENDED);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private void displayFeedbackForEditing(ArrayList<Task> oldList,
			Task oldTask, ArrayList<Task> list, Task task, String feedback) {
		if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
			addToStackForUndoAndRedo(oldList, oldTask, Constants.HIGHLIGH,
					MESSAGE_TASK_EDITED_FOR_UNDO, COLOR_DONE, list, task,
					Constants.HIGHLIGH, feedback, COLOR_DONE);
			gui.displayDone(feedback, INSERTED);
			showToUser(list, task, Constants.HIGHLIGH, APPENDED);
		} else if (feedback.equals(Constants.MESSAGE_EDIT_CLASHING_TIMESLOTS)) {
			addToStackForUndoAndRedo(oldList, oldTask, Constants.HIGHLIGH,
					MESSAGE_TASK_EDITED_FOR_UNDO, COLOR_WARNING, list, task,
					Constants.HIGHLIGH, feedback, COLOR_WARNING);
			gui.displayWarning(feedback, INSERTED);
			showToUser(list, task, Constants.HIGHLIGH, APPENDED);
		} else {
			gui.displayWarning(feedback, INSERTED);
			tasksOnScreen.clear();
		}
	}

	private void clear() {
		try {
			ArrayList<Task> list = logic.load();
			String feedback = logic.clear();
			if (feedback.equals(Constants.MESSAGE_TASK_CLEARED)) {
				gui.displayDone(feedback, INSERTED);
				showToUser(list, null, Constants.DELETED, APPENDED);
				tasksOnScreen.clear();
				addToStackForUndoAndRedo(list, null, Constants.NO_EFFECT,
						MESSAGE_TASK_RESTORED, COLOR_DONE, list, null,
						Constants.DELETED, feedback, COLOR_DONE);
			} else {
				gui.displayWarning(feedback, INSERTED);
				tasksOnScreen.clear();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private void undo() {
		try {
			String feedback = logic.undo();

			if (feedback.equals(Constants.MESSAGE_UNDO_DONE) && currentPos >= 0) {
				UndoAndRedoPack pack = stack.get(currentPos);
				if (pack.colorForUndo == COLOR_DONE) {
					gui.displayDone(pack.statusForUndo, INSERTED);
				} else {
					gui.displayWarning(pack.statusForUndo, INSERTED);
				}
				gui.display(pack.tasksForUndo, pack.posToDoEffectForUndo,
						pack.effectForUndo, APPENDED, INDEXED);
				currentPos--;
			} else {
				gui.displayWarning(MESSAGE_NO_UNDO, INSERTED);
				tasksOnScreen.clear();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private void redo() {
		try {
			String feedback = logic.redo();
			if (feedback.equals(Constants.MESSAGE_REDO_DONE)
					&& currentPos < availablePos) {
				currentPos++;
				UndoAndRedoPack pack = stack.get(currentPos);
				if (pack.colorForRedo == COLOR_DONE) {
					gui.displayDone(pack.statusForRedo, INSERTED);
				} else {
					gui.displayWarning(pack.statusForRedo, INSERTED);
				}
				gui.display(pack.tasksForRedo, pack.posToDoEffectForRedo,
						pack.effectForRedo, APPENDED, INDEXED);
			} else {
				gui.displayWarning(MESSAGE_NO_REDO, INSERTED);
				tasksOnScreen.clear();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private void addToStackForUndoAndRedo(ArrayList<Task> listForUndo,
			Task taskForUndo, int undoEffect, String messageForUndo,
			int ucolor, ArrayList<Task> listForRedo, Task taskForRedo,
			int redoEffect, String messageForRedo, int rcolor) {
		int posForUndo = -1, posForRedo = -1;
		
		if (taskForUndo == null) {
			posForUndo = ALL_TASK;
		} else if (listForUndo != null) {
			for (posForUndo = 0; posForUndo < listForUndo.size(); posForUndo++) {
				if (listForUndo.get(posForUndo).equals(taskForUndo)) {
					break;
				}
			}
		}
		
		if (taskForRedo == null) {
			posForRedo = ALL_TASK;
		} else if (listForRedo != null) {
			for (posForRedo = 0; posForRedo < listForRedo.size(); posForRedo++) {
				if (listForRedo.get(posForRedo).equals(taskForRedo)) {
					break;
				}
			}
		}

		UndoAndRedoPack pack = new UndoAndRedoPack(posForUndo, listForUndo,
				undoEffect, messageForUndo, ucolor, posForRedo, listForRedo,
				redoEffect, messageForRedo, rcolor);

		currentPos++;
		availablePos = currentPos;
		if (currentPos >= stack.size()) {
			stack.add(pack);
		} else {
			stack.set(currentPos, pack);
		}
	}

	private void search(Task task) {
		if (task.getDescription() == null) {
			gui.displayWarning(Constants.MESSAGE_NO_SEARCH_INFO, INSERTED);
			tasksOnScreen.clear();
			return;
		}
		try {
			ArrayList<Task> result = logic.search(task.getDescription());
			if (result.size() == 0) {
				gui.displayDone(Constants.MESSAGE_NO_RESULT, INSERTED);
				tasksOnScreen.clear();
			} else {
				showToUser(result, INSERTED);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private void set(Task task) {
		try {
			ArrayList<Task> list = new ArrayList<Task>();

			if (isInteger(task.getDescription())) {
				int noOfTask = toInteger(task.getDescription());
				if (noOfTask <= tasksOnScreen.size()) {
					Task taskToBeEdited = tasksOnScreen.get(noOfTask - 1);
					editTheTask(task, taskToBeEdited);
					return;
				}
			}

			list = logic.search(task.getDescription());
			if (list.size() == 0) {
				gui.displayDone(Constants.MESSAGE_NO_RESULT, INSERTED);
				tasksOnScreen.clear();
			} else if (list.size() == 1) {
				Task taskToBeEdited = list.get(0);
				editTheTask(task, taskToBeEdited);
			} else {
				gui.displayWarning(Constants.MESSAGE_MORE_THAN_ONE_TASK_FOUND,
						INSERTED);
				showToUser(list, APPENDED);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			e.printStackTrace();
		}
	}

	private void editTheTask(Task task, Task taskToBeEdited) {
		ArrayList<Task> originalList;
		originalList = new ArrayList<Task>(searchTaskOfSameDay(taskToBeEdited));
		Task originalTask = new Task(taskToBeEdited);

		updateNewPriorityAndState(task, taskToBeEdited);
		String feedback = logic.set(taskToBeEdited);

		if (feedback.equals(Constants.MESSAGE_TASK_EDITED)) {
			String message = String.format(
					Constants.MESSAGE_UPDATE_STATUS_AND_PRIORITY,
					taskToBeEdited.getState(),
					taskToBeEdited.getPriorityLevel());
			String messageForUndo = String.format(
					Constants.MESSAGE_UPDATE_STATUS_AND_PRIORITY,
					originalTask.getState(), originalTask.getPriorityLevel());

			ArrayList<Task> list = searchTaskOfSameDay(taskToBeEdited);
			addToStackForUndoAndRedo(originalList, originalTask,
					Constants.HIGHLIGH, messageForUndo, COLOR_DONE, list,
					taskToBeEdited, Constants.HIGHLIGH, messageForUndo,
					COLOR_DONE);

			gui.displayDone(message, INSERTED);
			showToUser(list, taskToBeEdited, Constants.HIGHLIGH, APPENDED);
		} else {
			gui.displayWarning(feedback, INSERTED);
			tasksOnScreen.clear();
		}
	}

	private void updateNewPriorityAndState(Task task, Task newTask) {
		if (task.getPriorityLevel() != null) {
			newTask.setPriority(task.getPriorityLevel());
		}
		if (task.getState() != null) {
			newTask.setState(task.getState());
		}
	}

	private void showToUser(ArrayList<Task> list, boolean isAppended) {
		tasksOnScreen = new ArrayList<Task>(list);
		gui.display(list, -1, Constants.NO_EFFECT, isAppended, INDEXED);
	}

	private void showToUser(ArrayList<Task> list, Task task, int effect,
			boolean isAppended) {
		tasksOnScreen = new ArrayList<Task>(list);

		int i;
		for (i = 0; i < list.size(); i++) {
			if (list.get(i).equals(task)) {
				break;
			}
		}
		if (task == null) {
			i = ALL_TASK;
		}
		gui.display(list, i, effect, isAppended, INDEXED);
	}

	public boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			return false;
		}
	}

	public int toInteger(String input) {
		try {
			return Integer.parseInt(input);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			return 0;
		}
	}

	private String convertCalendarToString(Calendar time, String FORMAT) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);
		return formatter.format(time.getTime());
	}

	private String getFirstInt(String s) {
		String result = "";
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				for (int j = i; j < s.length()
						&& Character.isDigit(s.charAt(j)); j++) {
					result = result + s.charAt(j);
				}
				break;
			}
		}
		return result;
	}

	// this is for unit testing.
	public String getDisplayedMessage() {
		return gui.displayText;
	}
}
