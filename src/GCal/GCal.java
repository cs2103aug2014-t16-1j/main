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
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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

	public GCal() {
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
			
			Storage store  = new Storage("TasKoord.txt");
			for(Task i : store.loadFromFile()){
				if(!i.isSync()){
					createEvent(i,calendar.getId());
				}
			}
			return Constants.MESSAGE_SYNC_COMPLETE;
	}

	public String createEvent(Task task,String calId) throws IOException {
		Event event = new Event();
		try{
			event.setSummary(null);
			event.setDescription(task.getDescription());
			event.setLocation(task.getLocation());
			
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.FORMAT_DATE_HOUR);
			Date start_date = sdf.parse(sdf.format(task.getStartTime()));
			event.setStart(new EventDateTime().setDateTime(new DateTime(start_date)));
			
			SimpleDateFormat sdf1 = new SimpleDateFormat(Constants.FORMAT_DATE_HOUR);
			Date end_date = sdf1.parse(sdf1.format(task.getEndTime()));
			event.setStart(new EventDateTime().setDateTime(new DateTime(end_date)));
			
			client.events().insert(calId, event).execute();
			return event.getId();
		} catch(ParseException e){
			return null;
		}
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
