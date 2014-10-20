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
    private String category;
    private PriorityType priorityLevel;

    public Task() {
        this.startTime = null;
        this.endTime = null;
        this.location = null;
        this.description = null;
        this.frequencyType = null;
        this.state = null;
    }

    public void setStartTime(Calendar time) {
        this.startTime = time;
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

    public void setState(String state) {
        if (state.equalsIgnoreCase(Constants.STATE_DONE)) {
            this.state = StateType.DONE;
        } else if (state.equalsIgnoreCase(Constants.STATE_PENDING)) {
            this.state = StateType.PENDING;
        } else if (state.equalsIgnoreCase(Constants.STATE_GIVEUP)) {
            this.state = StateType.GIVEUP;
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

    public void setPriority(String priorityLevel) {
        if (priorityLevel != null) {
            if (priorityLevel.equalsIgnoreCase(Constants.PRIORITY_LOW)) {
                this.priorityLevel = PriorityType.LOW;
            } else if (priorityLevel.equalsIgnoreCase(Constants.PRIORITY_MEDIUM)) {
                this.priorityLevel = PriorityType.MEDIUM;
            } else if (priorityLevel.equalsIgnoreCase(Constants.PRIORITY_HIGH)) {
                this.priorityLevel = PriorityType.HIGH;
            }
        }
    }
    
    public void setCategory(String category) {
        this.category = category;
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
    
    public String getCategory() {
        return this.category;
    }
}
