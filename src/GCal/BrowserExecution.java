package GCal;

public class BrowserExecution extends Thread {

	private String url;
	
	//@author A0118919U
	public BrowserExecution(){
		this.url = "";
	}
	
	public void init(String url){
		this.url = url;
	}
	
	@Override
	public void run() {
	   SwingBrowser browser = new SwingBrowser();
	   browser.runBrowser(url);
    }
}
