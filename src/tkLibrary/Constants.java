package tkLibrary;

// This is for constants that can be used for > 1 classes.
public final class Constants {
	public static final String FAILED = "failed";
	
	// date format
	public static final String FORMAT_DATE_HOUR = "MMM dd yyyy HH:mm";
	public static final String FORMAT_DATE_DATE_AND_HOUR= "dd.MM.yyyy HH:mm";
	public static final String FORMAT_DATE = "yyyyMMdd";
	public static final String FORMAT_DATE_CMP = "yyyyMMddHHmm";
	public static final String FORMAT_EEE = "EEE, dd MMM yyyy";
	public static final String FORMAT_HOUR = "HH:mm";
	
	//@author A0111705W
	// logic constants
	public static final String MESSAGE_CLASHING_TIMESLOTS = "Warning: timeslot is taken, task still added to TasKoord.";
	public static final String MESSAGE_EDIT_CLASHING_TIMESLOTS = "Warning: timeslot of edited task is taken, task still added to TasKoord.";
	public static final String MESSAGE_TASK_ADDED = "Task added to TasKoord!";
	public static final String MESSAGE_TASK_DOES_NOT_EXIST = "Task does not exist!";
	public static final String MESSAGE_TASK_DELETED = "Tasks were deleted from TasKoord:";
	public static final String MESSAGE_TASK_EDITED = "Task edited!";
	public static final String MESSAGE_EDIT_TASK_DOES_NOT_EXIST = "Cannot edit because task does not exist.";
	public static final String MESSAGE_EDIT_TASK_CLASHES = "Cannot edit because task clashes with other tasks.";
	public static final String MESSAGE_TASK_CLEARED = "All tasks cleared from TasKoord.";
	public static final String MESSAGE_DUPLICATED_TASK = "This task has been already in the list.";
	public static final String MESSAGE_UNDO_DONE = "The command was undone.";
	public static final String MESSAGE_REDO_DONE = "The command was redone.";
	public static final String MESSAGE_PRIORITY_SET = "Priority level set for task.";
	public static final String MESSAGE_PRIORITY_TASK_DOES_NOT_EXIST = "Cannot set priority because task does not exist";
	public static final String MESSAGE_STATE_COMPLETED = "State changed to 'completed'.";
	public static final String MESSAGE_STATE_DISCARDED = "State changed to 'discarded'.";
	public static final String MESSAGE_STATE_PENDING = "State changed to 'pending'.";
	public static final String MESSAGE_MORE_THAN_ONE_TASK_FOUND = "There are more than 1 task found. Specify the task.";
	public static final String MESSAGE_NO_START_TIME = "Please indicate a start time for your task.";
	
	//Access Token
	public static final String CODE_REJECTED = "You have denied the request, unable to sync.";
	public static final String CODE_NO_CODE = "You have not accepted the request, unable to sync.";
	
	// storage constants
	public static final String STARTTIME = "STARTTIME";
	public static final String ENDTIME = "ENDTIME";
	public static final String LOCATION = "LOCATION";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String FREQUENCY = "FREQUENCY";
	public static final String FREQUENCY_TYPE = "FREQUENCY_TYPE";
	public static final String STATE_TYPE = "STATE_TYPE";
	public static final String PRIORITY_TYPE = "PRIORITY";
	public static final String SYNC_STATUS = "SYNC";
	public static final String END_OBJECT_SIGNAL = "END";
	
	// constants for frequency type
	public static final String FREQUENCY_DAY = "DAY";
	public static final String FREQUENCY_WEEK = "WEEK";
	public static final String FREQUENCY_MONTH = "MONTH";
	public static final String FREQUENCY_YEAR = "YEAR";
	
	// constants for state type
	public static final String STATE_COMPLETED = "COMPLETED";
	public static final String STATE_PENDING = "PENDING";
	public static final String STATE_DISCARDED = "DISCARDED";
	public static final String STATE_NULL = "NULL";
	
	// constants for state type
    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_NULL = "NULL";
    
    //@author A0111705W
    // constants for UI
    public static final int LISTUPCOMINGTIME_DEFAULT = 5;
    public static final String MESSAGE_NO_RESULT = "There is no such task.";
    public static final String MESSAGE_NO_SEARCH_INFO = "Please specify a keyword to search for!";
    public static final String MESSAGE_NO_ADD_INFO = "Please specify the task you want to add!";
    public static final String MESSAGE_NO_DELETE_INFO = "Please specify the task you want to delete!";
    public static final String MESSAGE_NO_RESULT_LIST = "There is nothing to list.";
    public static final String MESSAGE_UPDATE_STATUS_AND_PRIORITY = "Task status changed to: %s and priority changed to: %s";
    public static final String MESSAGE_ENDTIME_BEFORE_STARTTIME = "The end time cannot be before the start time.";
    
	//@author A0112068N
    // more constants for UI
	public static final int HIGHLIGH = 1;
	public static final int DELETED = 2; 
	public static final int NO_EFFECT = 0;
	
	public static final String EXCEPTIONS_INVALID_USERCOMMAND = "Your input is invalid: %1$s <br>%2$s";
	
	//@author A0110493N

    // constants for Task and Parser
    public static final String EXCEPTIONS_INVALID_STATE = "The indicated status is invalid: %1$s";
    public static final String EXCEPTIONS_INVALID_PRIORITY = "The indicated priority is invalid: %1$s";
    
	// constants for Parser
	public static final String EXCEPTIONS_INVALID_COMMAND = "The command is invalid: %1$s";
	public static final String EXCEPTIONS_INVALID_TIME = "The time format is invalid: %1$s";
	public static final String EXCEPTIONS_INVALID_DATE = "The date format is invalid: %1$s";
	public static final String EXCEPTIONS_INVALID_MONTH = "The month format is invalid: %1$s";
	public static final String EXCEPTIONS_INVALID_DAY = "The month format is invalid: %1$s";
	public static final String EXCEPTIONS_INVALID_YEAR = "The month format is invalid: %1$s";
}
