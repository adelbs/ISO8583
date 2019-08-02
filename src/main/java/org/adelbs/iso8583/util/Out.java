package org.adelbs.iso8583.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.adelbs.iso8583.clientserver.CallbackAction;

public class Out {

	public static void log(String title, String msg) {
		log(title, msg, null);
	}
	
	public static void log(String title, String msg, CallbackAction callback) {
		init();
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:S");
        String time = sdf.format(cal.getTime());
        String logMsg = "ISO8583 :: ("+ title +" - "+ time +") :: "+ msg;
        
        if (verbose) 
        	System.out.println(logMsg);
        
        if (callback != null)
        	callback.log(logMsg);
	}

	private static boolean verbose = false;
	private static boolean hasInit = false;
	private static void init() {
		if (!hasInit) {
			String isoOut = System.getenv("ISO8583OUT");
			if (isoOut != null && isoOut.equalsIgnoreCase("verbose")) {
				verbose = true;
				System.out.println("ISO8583 :: VERBOSE MODE");
			}
			else {
				System.out.println("ISO8583 :: NORMAL MODE");
			}
		}
		
		hasInit = true;
	}
}
