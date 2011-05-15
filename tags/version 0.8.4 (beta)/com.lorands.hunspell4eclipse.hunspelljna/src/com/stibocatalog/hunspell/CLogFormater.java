package com.stibocatalog.hunspell;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

/**
 * The Hunspell java bindings are licensed under LGPL, see the file COPYING.txt
 * in the root of the distribution for the exact terms.
 * 
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 28/04/2011 (dd/mm/yy)
 */
public class CLogFormater {

	private static String DUMMY_SHORT_HASHCODE = "0000";

	private final static char LOG_SEPARATOR = ',';
	private final static char LOG_TEXTS_SEPARATOR = ':';
	private final static char LOG_WHO_SEPARATOR = ',';

	public final static int SIZE_LOG_LEVEL = 4;
	public final static int SIZE_LOG_THREADNAME = 18;
	public final static int SIZE_LOG_WHAT = 20;
	public final static int SIZE_LOG_WHO = 22;

	public final static String STR_EMPTY = "";
	public final static String STR_NULL = "null";

	/**
	 * @param aSB
	 * @return
	 */
	private static StringBuffer addFormatedTimeStampInSB(StringBuffer aSB) {
		addHourFullInSB(aSB, System.currentTimeMillis());
		return aSB;
	}

	/**
	 * format hh:mm:ss.sss
	 * 
	 * @param aTime
	 * @param aSep
	 * @param aWithMillisecs
	 * @return
	 */
	private static StringBuffer addHourFullInSB(StringBuffer aSB, long aTime) {

		if (aTime <= 0)
			aTime = System.currentTimeMillis();

		Calendar wRightNow = Calendar.getInstance();
		wRightNow.setTime(new Date(aTime));
		aSB.append(CTools.strAdjustRight(wRightNow.get(Calendar.HOUR_OF_DAY), 2));
		aSB.append(':');
		aSB.append(CTools.strAdjustRight(wRightNow.get(Calendar.MINUTE), 2));
		aSB.append(':');
		aSB.append(CTools.strAdjustRight(wRightNow.get(Calendar.SECOND), 2));
		aSB.append('.');
		aSB.append(CTools.strAdjustRight(wRightNow.get(Calendar.MILLISECOND), 3));
		return aSB;
	}

	private static StringBuffer addLevelInLogLine(StringBuffer aSB, Level aLevel) {
		aSB.append(CTools
				.strAdjustLeft(levelToStr(aLevel), SIZE_LOG_LEVEL, ' '));
		return aSB;
	}

	/**
	 * @param aSB
	 *            a stringbuffer to be appended
	 * @param aObjects
	 *            a table of object
	 * @return the given StringBuffer
	 */
	private static StringBuffer addTextsInLogLine(StringBuffer aSB,
			Object... aObjects) {

		if (aObjects == null || aObjects.length == 0)
			return aSB;

		if (aObjects.length == 1)
			return aSB.append(aObjects[0].toString());

		// if the first object is a format
		if (aObjects[0].toString().indexOf('%') > -1) {
			return aSB.append(String.format(aObjects[0].toString(),
					CTools.removeOneObject(aObjects, 0)));
		}

		boolean wIsId = false;
		boolean wIsValue = false;
		String wStr;
		int wMax = aObjects.length;
		for (int wI = 0; wI < wMax; wI++) {
			wIsValue = wIsId;
			wStr = aObjects[wI].toString();
			if (wStr != null) {
				wIsId = wStr.endsWith("=");

				if (wIsValue)
					aSB.append('[');

				aSB.append(wStr);

				if (wIsValue)
					aSB.append(']');
				if (!wIsId)
					aSB.append(' ');
			}
		}

		return aSB;
	}

	/**
	 * @param aSB
	 * @param aThreadName
	 * @return
	 */
	private static StringBuffer addThreadNameInLogLine(StringBuffer aSB,
			String aThreadName) {
		aSB.append(CTools.strAdjustRight(aThreadName, SIZE_LOG_THREADNAME, ' '));
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aWhat
	 * @return
	 */
	private static StringBuffer addWhatInLogLine(StringBuffer aSB, String aWhat) {
		aSB.append(CTools.strAdjustRight(aWhat, SIZE_LOG_WHAT, ' '));
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aWho
	 * @return
	 */
	private static StringBuffer addWhoInLogLine(StringBuffer aSB, Object aWho) {
		aSB.append(CTools.strAdjustRight(getWhoObjectId(aWho), SIZE_LOG_WHO,
				' '));
		return aSB;
	}

	/**
	 * @param aWho
	 * @param aLevel
	 * @param aWhat
	 * @param aObjects
	 * @return
	 */
	public static String formatLogLine(Object aWho, Level aLevel, String aWhat,
			Object... aObjects) {
		StringBuffer wSB = new StringBuffer(128);
		addFormatedTimeStampInSB(wSB);
		wSB.append(LOG_SEPARATOR);
		addLevelInLogLine(wSB, aLevel);
		wSB.append(LOG_SEPARATOR);
		addThreadNameInLogLine(wSB, Thread.currentThread().getName());
		wSB.append(LOG_SEPARATOR);
		addWhoInLogLine(wSB, aWho);
		wSB.append(LOG_WHO_SEPARATOR);
		addWhatInLogLine(wSB, aWhat);
		wSB.append(LOG_TEXTS_SEPARATOR);
		addTextsInLogLine(wSB, aObjects);
		return wSB.toString();
	}

	/**
	 * @param aWho
	 * @return
	 */
	private static String getWhoObjectId(Object aWho) {
		if (aWho == null)
			return STR_NULL;

		if (aWho instanceof Class)
			return ((Class<?>) aWho).getSimpleName() + '_'
					+ DUMMY_SHORT_HASHCODE;

		return new StringBuffer().append(aWho.getClass().getSimpleName())
				.append('_').append(CTools.strAdjustRight(aWho.hashCode(), 4))
				.toString();
	}

	/**
	 * @param aLevel
	 * @return
	 */
	private static String levelToStr(Level aLevel) {
		if (aLevel == Level.ALL)
			return "ALL";
		if (aLevel == Level.CONFIG)
			return "CONFIG";
		if (aLevel == Level.FINE)
			return "FINE";
		if (aLevel == Level.FINER)
			return "FINER";
		if (aLevel == Level.FINEST)
			return "FINEST";
		if (aLevel == Level.INFO)
			return "INFO";
		if (aLevel == Level.OFF)
			return "SEVERE";
		if (aLevel == Level.SEVERE)
			return "SEVERE";
		if (aLevel == Level.WARNING)
			return "WARNING";
		return "INFO ";
	}

}
