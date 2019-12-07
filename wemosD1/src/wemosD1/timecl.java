package wemosD1;

import java.util.Calendar;

public class timecl extends Thread {
	static String timeString;
	public void run() {
		while(true) {
			Calendar cal  = Calendar.getInstance();
			timeString = String.format("%2d h %2d m %2d s",
					                    cal.get(Calendar.HOUR_OF_DAY),
					                    cal.get(Calendar.MINUTE),
					                    cal.get(Calendar.SECOND));
			System.out.println(timeString);
			try {
				sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
