package tkLibrary;

// This is for constants that can be used for > 1 classes.
public final class Constants {
	public static final String FAILED = "failed";
	
	// date format
	public static final String FORMAT_DATE_HOUR = "MMM dd yyyy HH:mm:ss";
	public static final String FORMAT_DATE = "yyyyMMdd";
	public static final String FORMAT_DATE_CMP = "yyyyMMddHHmmss";
	public static final String FORMAT_EEE = "EEE, dd MMM yyyy";
	public static final String FORMAT_HOUR = "HH:mm:ss";
	
	// logic constants
	public static final String MESSAGE_CLASHING_TIMESLOTS = "Warning: timeslot is taken, task still added to TasKoord.";
	public static final String MESSAGE_TASK_ADDED = "Task added to TasKoord!";
	public static final String MESSAGE_TASK_DOES_NOT_EXIST = "Task does not exist!";
	public static final String MESSAGE_TASK_DELETED = "Task deleted from TasKoord!";
	public static final String MESSAGE_TASK_EDITED = "Task edited!";
	public static final String MESSAGE_EDIT_TASK_DOES_NOT_EXIST = "Cannot edit because task does not exist.";
	public static final String MESSAGE_EDIT_TASK_CLASHES = "Cannot edit because task clashes with other tasks.";
	public static final String MESSAGE_TASK_CLEARED = "All tasks was deleted.";
	public static final String MESSAGE_UNDO_DONE = "The command was undone.";
	public static final String MESSAGE_PRIORITY_SET = "Priority level set for task.";
	public static final String MESSAGE_PRIORITY_TASK_DOES_NOT_EXIST = "Cannot set priority because task does not exist";
	
	// logic exceptions constants
	public static final String EXCEPTIONS_ADD_FAIL = "Unable to add to TasKoord.";
	public static final String EXCEPTIONS_DELETE_FAIL = "Unable to delete from TasKoord.";
	public static final String EXCEPTIONS_EDIT_FAIL_BECAUSE_OF_ADD = "Edit failed because of add.";
	public static final String EXCEPTIONS_EDIT_FAIL_BECAUSE_OF_DELETE = "Edit failed because of delete.";
	public static final String EXCEPTIONS_EDIT_FAIL_BECAUSE_OF_OTHERS = "Edit failed because of other reason.";
	
	// storage constants
	public static final String STARTTIME = "STARTTIME";
	public static final String ENDTIME = "ENDTIME";
	public static final String LOCATION = "LOCATION";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String FREQUENCY = "FREQUENCY";
	public static final String FREQUENCY_TYPE = "FREQUENCY_TYPE";
	public static final String STATE_TYPE = "STATE_TYPE";
	public static final String END_OBJECT_SIGNAL = "END";
	
	// constants for frequency type
	public static final String FREQUENCY_DAY = "DAY";
	public static final String FREQUENCY_WEEK = "WEEK";
	public static final String FREQUENCY_MONTH = "MONTH";
	public static final String FREQUENCY_YEAR = "YEAR";
	
	// constants for state type
	public static final String STATE_DONE = "DONE";
	public static final String STATE_PENDING = "PENDING";
	public static final String STATE_GIVEUP = "GIVEUP";
	
	// constants for state type
    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_HIGH = "HIGH";
}