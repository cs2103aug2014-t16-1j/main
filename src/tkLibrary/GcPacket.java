package tkLibrary;

import java.util.ArrayList;

public class GcPacket {
	public ArrayList<Task> taskAddedToTK;
	public ArrayList<Task> taskDeletedFromTK;
	public ArrayList<Task> taskAddedToGC;
	public ArrayList<Task> taskDeletedFromGC;
	
	public GcPacket() {
		taskAddedToTK = new ArrayList<Task> ();
		taskAddedToGC = new ArrayList<Task> ();
		taskDeletedFromTK = new ArrayList<Task> ();
		taskDeletedFromGC = new ArrayList<Task> ();
	}
}
