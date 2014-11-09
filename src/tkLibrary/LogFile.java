package tkLibrary;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

//@author A0111705W
public class LogFile {
	private static FileHandler fh;
	private static SimpleFormatter formatter;

	public static void newLogger(){
		try {  
			Logger logger = Logger.getLogger(".TasKoordLogFile.log");

			fh = new FileHandler(".TasKoordLogFile.log");
			logger.addHandler(fh);

			formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();  
		}
	}
}