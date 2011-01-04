package me.taylorkelly.bigbrother;
import java.util.TimerTask;


public class EmailTask extends TimerTask {

	@Override
	public void run() {
		BBSettings.hourCount += 0.25;
		if(BBSettings.hourCount >= BBSettings.hours) {
			BBEmail.createEmail();
			BBSettings.hourCount = 0;
		}
		BBSettings.saveHourCount();
	}

}
