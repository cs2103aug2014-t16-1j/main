package tkLibrary;

// This is for constants that can be used for > 1 classes.
public final class Constants {
	public static final String FAILED = "failed";
	
	
	// logic constants
	public static final String MESSAGE_CLASHING_TIMESLOTS = "Warning: timeslot is taken, unable to add to specified timeslot.";
	public static final String MESSAGE_TASK_ADDED = "Task added to TasKoord!";
	public static final String MESSAGE_TASK_DOES_NOT_EXIST = "Task does not exist!";
	public static final String MESSAGE_TASK_DELETED = "Task deleted from TasKoord!";
	public static final String MESSAGE_TASK_EDITED = "Task edited!";
	public static final String MESSAGE_EDIT_TASK_DOES_NOT_EXIST = "Cannot edit because task does not exist.";
	public static final String MESSAGE_EDIT_TASK_CLASHES = "Cannot edit because task clashes with other tasks.";
	
	// logic exceptions constants
	public static final String EXCEPTIONS_ADD_FAIL = "Unable to add to TasKoord.";
	public static final String EXCEPTIONS_DELETE_FAIL = "Unable to delete from TasKoord.";
	public static final String EXCEPTIONS_EDIT_FAIL_BECAUSE_OF_ADD = "Edit failed because of add.";
	public static final String EXCEPTIONS_EDIT_FAIL_BECAUSE_OF_DELETE = "Edit failed because of delete.";
	public static final String EXCEPTIONS_EDIT_FAIL_BECAUSE_OF_OTHERS = "Edit failed because of other reason.";
	
}