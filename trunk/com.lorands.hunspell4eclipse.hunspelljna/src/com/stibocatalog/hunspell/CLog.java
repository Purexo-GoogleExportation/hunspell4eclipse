package com.stibocatalog.hunspell;

import java.util.logging.Level;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * The Hunspell java bindings are licensed under LGPL, see the file COPYING.txt
 * in the root of the distribution for the exact terms.
 * 
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 28/04/2011 (dd/mm/yy)
 */
public class CLog {

	public final static String LIB_CONSTRUCTOR = "<init>";

	public final static String LIB_INSTANCIATED = "instanciated";

	private final static String PROPERTY_LOG_ON = "hunspell.log.on";

	private static boolean sLogOn = getLogOn();

	/**
	 * to be able to activate the logger
	 */
	public static void forceLogOn() {
		sLogOn = true;
	}

	/**
	 * @return true in the "hunspell.log.on" system property exists.
	 */
	private static boolean getLogOn() {
		return System.getProperty(PROPERTY_LOG_ON) != null;
	}

	/**
	 * @param who
	 * @param what
	 * @param aObjects
	 */
	public static void logErr(Object who, String what, Object... aObjects) {
		logErr(who, what, (Throwable) null, aObjects);
	}

	/**
	 * @param aErr
	 * @param aFormat
	 * @param aObjects
	 */
	public static void logErr(Object who, String what, Throwable aErr,
			Object... aObjects) {

		String wLogLine = CLogFormater.formatLogLine(who, Level.SEVERE, what,
				aObjects);

		// if not explit log on
		if (!on() && Activator.getDefault() != null)
			Activator
					.getDefault()
					.getLog()
					.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							wLogLine, aErr));
		else {
			System.err.println(wLogLine);
			if (aErr != null)
				aErr.printStackTrace();
		}

	}

	/**
	 * to display the initial state of the hunspell logging tool.
	 * 
	 * This method is called by the Activator of the
	 * "com.lorands.hunspell4eclipse.hunspelljna" bundle.
	 * 
	 */
	public static void logLoggerInfo() {
		if (!on())
			logOut(CLog.class,
					"logLoggerInfo",
					"The system property \"%s\" must be defined for explicitly activate the Hunspell logging tool.",
					PROPERTY_LOG_ON);

		else
			logOut(CLog.class, "logLoggerInfo",
					"The Hunspell logging tool is activated.");
	}

	/**
	 * @param aFormat
	 * @param aObjects
	 */
	public static void logOut(Object who, String what, Object... aObjects) {

		// build the log line
		String wLogLine = CLogFormater.formatLogLine(who, Level.FINE, what,
				aObjects);

		// if not explicit log on
		if (!on() && Activator.getDefault() != null)
			Activator
					.getDefault()
					.getLog()
					.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, wLogLine));
		else
			System.out.println(wLogLine);

	}

	/**
	 * @return true if the Hunspell logging tool is explicitly activated.
	 */
	public static boolean on() {
		return sLogOn;
	}

}
