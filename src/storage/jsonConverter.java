package storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tkLibrary.Task;
import tkLibrary.Constants;

import org.json.simple.JSONObject;

public class jsonConverter {
	
	@SuppressWarnings("unchecked")
	public static JSONObject taskToJSON(Task task)
	{
		JSONObject jTask=new JSONObject();
		jTask.put(Constants.DESCRIPTION, task.getDescription());
		jTask.put(Constants.LOCATION, task.getLocation());
		jTask.put(Constants.STARTTIME, convertCalendarToString(task.getStartTime()));
		jTask.put(Constants.ENDTIME, convertCalendarToString(task.getEndTime()));
		jTask.put(Constants.FREQUENCY, new Integer(task.getFrequency()));
		jTask.put(Constants.FREQUENCY_TYPE, task.getFrequencyType().toString());
		jTask.put(Constants.PRIORITY_TYPE, task.getPriorityLevel().toString());
		jTask.put(Constants.STATE_TYPE, task.getState().toString());
		return jTask;
	}		
	
	public static Task jsonToTask(JSONObject obj){
		Task temp = null;
		try{
			temp = new Task();
			temp.setDescription(obj.get(Constants.DESCRIPTION).toString());
			temp.setLocation(obj.get(Constants.LOCATION).toString());
			temp.setStartTime(obj.get(Constants.STARTTIME).toString());
			temp.setEndTime(obj.get(Constants.ENDTIME).toString());
			temp.setFrequency((int) obj.get(Constants.FREQUENCY));
			temp.setFrequencyType(obj.get(Constants.FREQUENCY_TYPE).toString());
			temp.setPriority(obj.get(Constants.PRIORITY_TYPE).toString());
			temp.setState(obj.get(Constants.STATE_TYPE).toString());
		}catch(Exception e){
			//e.printStackTrace();
		}
		return temp;	
	}
	
	private static String convertCalendarToString(Calendar time) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATE_HOUR);     
		return formatter.format(time.getTime());
	}
}