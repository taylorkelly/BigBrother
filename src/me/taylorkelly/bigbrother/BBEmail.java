package me.taylorkelly.bigbrother;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class BBEmail extends hMessage {
	public static String directory = "bigbrother";

	
	public BBEmail() {
        super("hEmail@taylorkelly.me", "BigBrother");
        for(String email: BBSettings.emails) {
        	this.addTo(email);
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        Calendar now = Calendar.getInstance();
        Calendar before = Calendar.getInstance();
        before.add(Calendar.HOUR, -1 * BBSettings.hours);
        
        this.setSubject("BigBrother: Logs changed from" + dateFormat.format(before.getTime()) + " to " + dateFormat.format(now.getTime()));
    }
	
	public static void createEmail() {
		BBEmail email = new BBEmail();
		
		File folder = new File(directory);
		File[] files = folder.listFiles(new LogFilter());
        Calendar before = Calendar.getInstance();
        before.add(Calendar.HOUR, -1 * BBSettings.hours);
		
		for(File file: files) {
			if(file.lastModified() > before.getTimeInMillis()) {
				email.addAttachment(file.getAbsolutePath());
			}
		}
		
		email.send();		
	}
	
	public static ArrayList<String> processEmails(String string) {
		ArrayList<String> emails = new ArrayList<String>();
		String[] split = string.split(",");
		for(int i = 0; i < split.length; i++) {
			if(split[i].equals("")) continue;
			emails.add(split[i].trim());
		}
		return emails;
	}
}
