package GCal;

import tkLibrary.Task;
import tkLibrary.Constants;
import storage.Storage;
//import storage.jsonConverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

//import org.json.simple.JSONObject;

import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.Calendar.CalendarList;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
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
	GoogleAuthorizationCodeFlow codeFlow;
	String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
	String appName = "TasKoord";
	HttpTransport httpTransport;
	JacksonFactory jsonFactory;
	private static Calendar client;
	String fileName ;

	public static boolean isOnline() {
		Socket socket = new Socket();
		InetSocketAddress adderess = new InetSocketAddress("www.google.com", 80);
		try {
			socket.connect(adderess);
			return true;
		} catch (IOException e) {
			System.err.println(Constants.MESSAGE_USER_OFFLINE);
			e.printStackTrace();
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	public GCal(String filename) {
		this.fileName = filename;
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
		String clientId = "635832951373-quldjs5vlr7h2s7jdsfgc8u4nct863jd.apps.googleusercontent.com";
		String clientSecret = "efCVVM5s04_Grob_V9dIvtd-";

		codeFlow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
				jsonFactory, clientId, clientSecret,
				Arrays.asList(CalendarScopes.CALENDAR)).build();
	}

	public String getURL() {
		String url = codeFlow.newAuthorizationUrl().setRedirectUri(redirectUrl)
				.build();
		
		return url;
	}

	public boolean generateNewToken(String code) throws IOException {
		try {
			TokenResponse tokenRes = new TokenResponse();
			AuthorizationCodeTokenRequest tokenRequest = codeFlow.newTokenRequest(code)
					.setRedirectUri(redirectUrl);
			tokenRes = tokenRequest.execute();
			writeFile(tokenRes.getAccessToken());
			Credential credential = codeFlow
					.createAndStoreCredential(tokenRes, appName);
			HttpRequestInitializer initializer = credential;
			Calendar.Builder builder = new Calendar.Builder(httpTransport,
					jsonFactory, initializer);
			builder.setApplicationName(appName);
			client = builder.build();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean withExistingToken() {
		TokenResponse tokenRes = new TokenResponse();
		if (validFile()) {
			System.out.println(readFile());
			tokenRes.setAccessToken(readFile());
			try {
				Credential credential = codeFlow.createAndStoreCredential(tokenRes,
						appName);
				HttpRequestInitializer initializer = credential;
				Calendar.Builder builder = new Calendar.Builder(httpTransport,
						jsonFactory, initializer);
				builder.setApplicationName(appName);
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
	
	public String syncGcal() throws IOException {
			com.google.api.services.calendar.model.Calendar calendar = client
					.calendars().get("primary").execute();
			
			ArrayList<Task> newList = new ArrayList<Task>();
			Storage store  = new Storage(fileName);
			for(Task i : store.loadFromFile()) {
				if(!i.isSync() && isTimedTask(i)){
					createEvent(i,calendar.getId());
					i.setSync(true);
				}
				newList.add(i);
			}
			store.store(newList);
			return Constants.MESSAGE_SYNC_COMPLETE;
	}

	public String createEvent(Task task,String calId) throws IOException {
		Event event = new Event();
		event.setSummary(task.getDescription());
		event.setLocation(task.getLocation());
		
		Date startDate = task.getStartTime().getTime();
		DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
		event.setStart(new EventDateTime().setDateTime(start));
		
		Date endDate = task.getEndTime().getTime();
		DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
		event.setEnd(new EventDateTime().setDateTime(end));
		
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
	
	private boolean isTimedTask(Task task){
		return (task.getStartTime() != null && task.getEndTime() != null);
	}
}
