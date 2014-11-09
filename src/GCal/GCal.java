package GCal;

import tkLibrary.GcPacket;
import tkLibrary.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;

public class GCal {
	final String MESSAGE_ORIGINAL_CREATOR = "[Task synced with TasKoord]";
	final String START_OF_DAY = "T00:00:00.000+08:00";
	
	String redirectUrl = "urn:ietf:wg:oauth:2.0:oob:auto";
	String clientName = "TasKoord";
	String clientEmail = "914087031259-compute@developer.gserviceaccount.com";
	String clientId = "914087031259-fbuolislmpf6i5vt4tcp0humaln9pdm4.apps.googleusercontent.com";
	String clientSecret = "mtsV4fkXY3wz8O2LKlWkOywi";
	String fileToStoreSyncedTime = "GSyncedTime";
	Event.Creator creator = new Event.Creator();
	
	GoogleAuthorizationCodeFlow codeFlow;
	HttpTransport httpTransport;
	JacksonFactory jsonFactory;
	private static Calendar client;

	//@author A0118919U
	public static boolean isOnline() {
		Socket socket = new Socket();
		InetSocketAddress adderess = new InetSocketAddress("www.google.com", 80);
		try {
			socket.connect(adderess);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
	
	public GCal() {
		initFlow();
	}

	private void initFlow() {
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
		codeFlow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
				jsonFactory, clientId, clientSecret,
				Arrays.asList(CalendarScopes.CALENDAR)).build();
		
	}
	
	public String getURL() {
		return codeFlow.newAuthorizationUrl().setRedirectUri(redirectUrl).build();
	}

	public boolean connectByNewToken(String code) throws IOException {
		try {
			TokenResponse tokenRes = new TokenResponse();
			AuthorizationCodeTokenRequest tokenRequest = codeFlow.newTokenRequest(code)
					.setRedirectUri(redirectUrl);
			tokenRes = tokenRequest.execute();
			writeFile(tokenRes.getAccessToken());
			Credential credential = codeFlow
					.createAndStoreCredential(tokenRes, clientName);
			HttpRequestInitializer initializer = credential;
			Calendar.Builder builder = new Calendar.Builder(httpTransport,
					jsonFactory, initializer);
			builder.setApplicationName(clientName);
			client = builder.build();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean connectUsingExistingToken() {
		TokenResponse tokenRes = new TokenResponse();
		if (validFile()) {
			System.out.println(readFile());
			tokenRes.setAccessToken(readFile());
			try {
				Credential credential = codeFlow.createAndStoreCredential(tokenRes,
						clientName);
				HttpRequestInitializer initializer = credential;
				Calendar.Builder builder = new Calendar.Builder(httpTransport,
						jsonFactory, initializer);
				builder.setApplicationName(clientName);
				client = builder.build();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}
	
	//@author A0112068N
	public GcPacket sync(ArrayList<Task> tkList) throws IOException {
		com.google.api.services.calendar.model.Calendar calendar = client
				.calendars().get("primary").execute();
		ArrayList<Event> gcEvents = listEvent(calendar.getId());
		ArrayList<Task> gcList = new ArrayList<Task> ();
		GcPacket packet = new GcPacket();
		
		for(Event event : gcEvents) {
			gcList.add(convertToTkTask(event));
		}
	
		deleteTaskFromTk(tkList, gcList, packet);
		deleteTaskFromGc(tkList, calendar, gcEvents, gcList, packet);
		addTaskFromTkToGc(tkList, calendar, gcList, packet);
		addTaskFromGcToTk(tkList, gcEvents, gcList, packet, calendar);
		
		java.util.Calendar time = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		DateTime newTime = new DateTime(time.getTime(), TimeZone.getTimeZone("UTC"));
		updateLastSyncedTime(newTime.getValue());
		
		return packet;
	}

	private void deleteTaskFromGc(ArrayList<Task> tkList,
			com.google.api.services.calendar.model.Calendar calendar,
			ArrayList<Event> gcEvents, ArrayList<Task> gcList, GcPacket packet)
			throws IOException {
		long lastSyncedTime = getLastSyncedTime();
		for (int i = 0; i < gcList.size(); i ++) {
			Task task = gcList.get(i);
			Event event = gcEvents.get(i);
			if (event.getDescription() != null
					&& event.getDescription().contains(MESSAGE_ORIGINAL_CREATOR)
					&& event.getUpdated().getValue() <= lastSyncedTime) {
				boolean existing = false;
				for (Task item : tkList) {
					if (item.equals(task)) {
						existing = true;
						break;
					}
				}
				if (!existing) {
					packet.taskDeletedFromGC.add(task);
					client.events().delete(calendar.getId(), gcEvents.get(i).getId()).execute();
				}
			}
		}
	}

	private void deleteTaskFromTk(ArrayList<Task> tkList,
			ArrayList<Task> gcList, GcPacket packet) {
		for (int i = 0; i < tkList.size(); i ++) {
			Task task = tkList.get(i);
			if (task.getStartTime() != null && task.isSynced()) {
				boolean existing = false;
				for (Task item : gcList) {
					if (item.equals(task)) {
						existing = true;
						break;
					}
				}
				if (!existing) {
					packet.taskDeletedFromTK.add(task);
				}
			}
		}
	}

	private void addTaskFromGcToTk(ArrayList<Task> tkList,
			ArrayList<Event> gcEvents, ArrayList<Task> gcList, GcPacket packet,
			com.google.api.services.calendar.model.Calendar calendar) throws IOException {
		long lastSyncedTime = getLastSyncedTime();
		for (int i = 0; i < gcList.size(); i++)
			if (gcEvents.get(i).getSummary() != null) {
				Event event = gcEvents.get(i);
				if (event.getDescription() == null
						|| !event.getDescription().contains(MESSAGE_ORIGINAL_CREATOR)
						|| event.getUpdated().getValue() > lastSyncedTime) {
					Task task = gcList.get(i);
					boolean existing = false;
					for (Task item : tkList) {
						if (item.equals(task)) {
							existing = true;
							break;
						}
					}
					if (!existing) {
						packet.taskAddedToTK.add(task);
					}
					if (event.getDescription() == null) {
						event.setDescription(MESSAGE_ORIGINAL_CREATOR);
					} else if (!event.getDescription().contains(MESSAGE_ORIGINAL_CREATOR)) {
						event.setDescription(event.getDescription() + " "
								+ MESSAGE_ORIGINAL_CREATOR);
					}
					client.events()
							.update(calendar.getId(), event.getId(), event)
							.execute();
				}
			}
	}

	private void addTaskFromTkToGc(ArrayList<Task> tkList,
			com.google.api.services.calendar.model.Calendar calendar,
			ArrayList<Task> gcList, GcPacket packet) throws IOException {
		
		for (int i = 0; i < tkList.size(); i ++) {
			Task task = tkList.get(i);
			
			if (task.getStartTime() != null && !task.isSynced()) {
				boolean existing = false;
				for (Task item : gcList) {
					if (item.equals(task)) {
						existing = true;
						break;
					}
				}
				packet.taskAddedToGC.add(task);
				if (!existing) {
					insertEvent(task, calendar.getId());
				}
			}
		}
	}
	
	private long getLastSyncedTime() {
		try {
			long result = 0;
			Scanner in = new Scanner(new File (fileToStoreSyncedTime));
			if (in.hasNextLong()) {
				result = in.nextLong();
			}
			in.close();
			return result;
		} catch (Exception e) {
			return 0;
		}
	}
	
	private void updateLastSyncedTime(long time) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(fileToStoreSyncedTime, false));
			out.println(time);
			out.close();
		} catch (IOException e) {
		}
	}
	
	public Task convertToTkTask(Event event) {
		Task task = new Task();
		task.setDescription(event.getSummary());
		task.setLocation(event.getLocation());
		
		DateTime startDateTime = event.getStart().getDateTime();
		DateTime endDateTime = event.getEnd().getDateTime();
		
		if (startDateTime == null) {
			startDateTime = event.getStart().getDate();
			startDateTime = new DateTime(startDateTime.toStringRfc3339() + START_OF_DAY);
		}
		if (endDateTime == null) {
			endDateTime = event.getEnd().getDate();
			endDateTime = new DateTime(endDateTime.toStringRfc3339() + START_OF_DAY);
		}
		
		java.util.Calendar start = DateTimeConverter(startDateTime);
		java.util.Calendar end = DateTimeConverter(endDateTime);
		
		task.setStartTime(start);
		if (start.compareTo(end) < 0) {
			task.setEndTime(end);
		}
		
		return task;
	}
	
    public java.util.Calendar DateTimeConverter (DateTime originalTime) {
        Date time = new Date(originalTime.getValue());
        java.util.Calendar result = java.util.Calendar.getInstance();
        result.setTime(time);
        return result;
    }

	public ArrayList<Event> listEvent(String calId) throws IOException {
		ArrayList<Event> result = new ArrayList<Event>();
		String pageToken = null;
		
		do {
		  Events events = client.events().list(calId).setPageToken(pageToken).execute();
		  List<Event> items = events.getItems();
		  for (Event event : items) {
		    result.add(event);
		  }
		  pageToken = events.getNextPageToken();
		} while (pageToken != null);
		
		return result;
	}
	
	//@author A0118919U
	public String insertEvent(Task task, String calId) throws IOException {
		Event event = new Event();
		event.setSummary(task.getDescription());
		event.setLocation(task.getLocation());
		
		Date startDate = task.getStartTime().getTime();
		DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
		event.setStart(new EventDateTime().setDateTime(start));
		
		if (task.getEndTime() != null) {
			Date endDate = task.getEndTime().getTime();
			DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
			event.setEnd(new EventDateTime().setDateTime(end));
		} else {
			event.setEnd(new EventDateTime().setDateTime(start));
		}
		
		event.setDescription(MESSAGE_ORIGINAL_CREATOR);
		
		client.events().insert(calId, event).execute();
		return event.getId();
	}
	
	public static boolean validFile() {
		File f = new File("GToken");
		if (f.exists()) {
			if (readFile().equals("")) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	public static String readFile() {
		String token = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader("GToken"));
			token = in.readLine();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Please generate the token again");
		}
		return token;
	}

	public static boolean writeFile(String token) {
		try {
			FileWriter fstream = new FileWriter("GToken", false);
			BufferedWriter bufferedWriter = new BufferedWriter(fstream);
			bufferedWriter.write(token);
			bufferedWriter.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void clearFile() {
		try {
			FileWriter fstream = new FileWriter("GToken", false);
			BufferedWriter bufferedWriter = new BufferedWriter(fstream);
			bufferedWriter.write("");
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
