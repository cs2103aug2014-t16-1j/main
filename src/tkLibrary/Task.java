package tkLibrary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Task {
    private Calendar startTime;
    private Calendar endTime;
    private int frequency;
    private FrequencyType frequencyType;
    private StateType state;
    private String location;
    private String description;
    private PriorityType priorityLevel;
    private boolean sync;
    private boolean blocked;

    public Task() {
        this.startTime = null;
        this.endTime = null;
        this.location = null;
        this.description = null;
        this.state = null;
        this.priorityLevel = null;
        this.sync = false;
        this.blocked = false;
    }
    
    public Task(Task task) {
    	if (task.getStartTime() == null) {
    		this.startTime = null;
    	} else {
    		this.startTime = (Calendar) task.getStartTime().clone();
    	}
    	
    	if (task.getEndTime() == null) {
    		this.endTime = null;
    	} else {
    		this.endTime = (Calendar) task.getEndTime().clone();
    	}
    	
        this.location = task.getLocation();
        this.description = task.getDescription();
        this.priorityLevel = task.getPriorityLevel();
        this.state = task.getState();
        this.sync = task.isSync();
        this.blocked = task.isBlocked();
    }
    
    public void block() {
    	this.blocked = true;
    }
    
    public void free() {
    	this.blocked = false;
    }
    
    public boolean isBlocked() {
    	return this.blocked;
    }
    
    public boolean isSync(){
    	return this.sync;
    }
    
    public void update(Task task) {
    	if (task.getStartTime() != null) {
    		this.startTime = (Calendar) task.getStartTime().clone();
    	}
    	if (task.getEndTime() != null) {
    		this.endTime = (Calendar) task.getEndTime().clone();
    	}
    	
    	if (task.getStartTime() != null && task.getEndTime() == null) {
    		this.startTime = (Calendar) task.getStartTime().clone();
    		this.endTime = null;
    	}
    	
        if (task.getLocation() != null && task.getLocation() != "") {
        	this.location = task.getLocation();
        }
        if (task.getDescription() != null && task.getDescription() != "") {
        	this.description = task.getDescription();
        }
        if (task.getPriorityLevel() != null) {
        	this.priorityLevel = task.getPriorityLevel();
        }
        if (task.getState() != null) {
        	this.state = task.getState();
        }
    }

    public void setStartTime(Calendar time) {
        this.startTime = time;
    }
    
    public boolean equals(Task task) {
    	if (task == null) {
    		return false;
    	}
    	if (!convertCalendarToString(this.startTime, Constants.FORMAT_DATE_CMP).equals( 
    			 convertCalendarToString(task.getStartTime(), Constants.FORMAT_DATE_CMP))) {
    		return false;
    	}
    	
    	if (!convertCalendarToString(this.endTime, Constants.FORMAT_DATE_CMP).equals( 
   			 convertCalendarToString(task.getEndTime(), Constants.FORMAT_DATE_CMP))) {
    		return false;
    	}
    	
    	if (this.description == null) {
    		if (task.getDescription() != null) {
    			return false;
    		}
    	} else if (!this.description.equalsIgnoreCase(task.getDescription())) {
    		return false;
    	}
    	
    	if (this.location == null) {
    		if (task.getLocation() != null) {
    			return false;
    		}
    	} else if (!this.location.equalsIgnoreCase(task.getLocation())) {
    		return false;
    	}
    	
    	return true;
    }

    public void setStartTime(String time) {
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.FORMAT_DATE_HOUR);
            try {
                cal.setTime(sdf.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.startTime = cal;
        }
    }

    public void setEndTime(Calendar time) {
        this.endTime = time;
    }

    public void setEndTime(String time) {
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.FORMAT_DATE_HOUR);
            try {
                cal.setTime(sdf.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.endTime = cal;
        }
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setFrequencyType(String frequencyType) {
        if (frequencyType == null) {
        } else if (frequencyType.equalsIgnoreCase(Constants.FREQUENCY_DAY)) {
            this.frequencyType = FrequencyType.DAY;
        } else if (frequencyType.equalsIgnoreCase(Constants.FREQUENCY_WEEK)) {
            this.frequencyType = FrequencyType.WEEK;
        } else if (frequencyType.equalsIgnoreCase(Constants.FREQUENCY_MONTH)) {
            this.frequencyType = FrequencyType.MONTH;
        } else if (frequencyType.equalsIgnoreCase(Constants.FREQUENCY_YEAR)) {
            this.frequencyType = FrequencyType.YEAR;
        }
    }

    public void setState(String state) throws Exception {
        if (state.equalsIgnoreCase(Constants.STATE_COMPLETED)) {
            this.state = StateType.COMPLETED;
        } else if (state.equalsIgnoreCase(Constants.STATE_PENDING)) {
            this.state = StateType.PENDING;
        } else if (state.equalsIgnoreCase(Constants.STATE_DISCARDED)) {
            this.state = StateType.DISCARDED;
        } else if (state.equalsIgnoreCase(Constants.STATE_NULL)) {
        	this.state = null;
        } else {
            throw new Exception("The indicate status: \"" + state + "\" is not recognised.");
        }
    }

    public void setState(StateType state) {
        this.state = state;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(PriorityType priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public void setPriority(String priorityLevel) throws Exception {
            if (priorityLevel.equalsIgnoreCase(Constants.PRIORITY_LOW)) {
                this.priorityLevel = PriorityType.LOW;
            } else if (priorityLevel.equalsIgnoreCase(Constants.PRIORITY_MEDIUM)) {
                this.priorityLevel = PriorityType.MEDIUM;
            } else if (priorityLevel.equalsIgnoreCase(Constants.PRIORITY_HIGH)) {
                this.priorityLevel = PriorityType.HIGH;
            }else if (priorityLevel.equalsIgnoreCase(Constants.PRIORITY_NULL)) {
                this.priorityLevel = null;
            } else {
                throw new Exception("The indicate priority: \"" + state + "\" is not recognised.");
            }
        
    }
    
    public void setSync(boolean sync_status){
    	this.sync = sync_status;
    }

    public Calendar getStartTime() {
        return this.startTime;
    }

    public Calendar getEndTime() {
        return this.endTime;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public FrequencyType getFrequencyType() {
        return this.frequencyType;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDescription() {
        return this.description;
    }

    public StateType getState() {
        return this.state;
    }

    public PriorityType getPriorityLevel() {
        return this.priorityLevel;
    }
    
	private String convertCalendarToString(Calendar time, String FORMAT) {
		if (time == null) {
			return "null";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);     
		return formatter.format(time.getTime());
	}
}
