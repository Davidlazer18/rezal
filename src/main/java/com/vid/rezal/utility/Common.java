package com.vid.rezal.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Common {

	public static String getLoggableStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}
