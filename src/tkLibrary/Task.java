package tkLibrary;

import java.util.Date;

public class Task {
	private Date startTime;
	private Date endTime;
	private int frequency;
	private FrequencyType frequencyType;
	private StateType state;
	private String location;
	private String description;
	
	public void setStartTime(Date time) {
		this.startTime = time;
	}
	
	public void setEndTime(Date time) {
		this.endTime = time;
	}
	
	public void setFrequency(int frequency, FrequencyType frequencyType) {
	    this.frequency = frequency;
		this.frequencyType = frequencyType;
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
	
	public Date getStartTime() {
		return this.startTime;
	}
	
	public Date getEndTime() {
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
}
