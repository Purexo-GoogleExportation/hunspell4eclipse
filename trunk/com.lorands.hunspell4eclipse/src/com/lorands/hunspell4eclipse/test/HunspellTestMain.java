package com.lorands.hunspell4eclipse.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import com.lorands.hunspell4eclipse.HunspellCheckIterator;
import com.stibocatalog.hunspell.CLog;
import com.stibocatalog.hunspell.CPlatform;
import com.stibocatalog.hunspell.CTools;

/**
 * Simple testing and native build utility class, not useful in applications.
 * 
 * The Hunspell java bindings are licensed under LGPL, see the file COPYING.txt
 * in the root of the distribution for the exact terms.
 * 
 * @author Flemming Frandsen (flfr at stibo dot com)
 * @author Olivier Gattaz - isandlaTech (olivier dot gattaz at isandlatech dot
 *         com)
 */

public class HunspellTestMain {

	private final static String LIB_RUN = "run";

	/**
	 * @param aFileIn
	 * @return
	 * @throws Exception
	 */
	public static byte[] getFileData(File aFileIn) throws Exception {
		FileInputStream wFis = new FileInputStream(aFileIn);
		byte[] wData = new byte[wFis.available()];
		wFis.read(wData);
		wFis.close();
		return wData;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HunspellTestMain wApp = null;
		try {

			wApp = new HunspellTestMain();

			wApp.run();

		} catch (Exception e) {
			CLog.logErr(HunspellTestMain.class, "main", e, "Failed !");
		} finally {
			if (wApp != null)
				wApp.destroy();
		}
	}

	/**
	 * 
	 */
	HunspellTestMain() {
		super();
	}

	/**
	 * 
	 */
	void destroy() {
		if (CLog.on())
			CLog.logOut(this, "destroy", "Destroyed");
	}

	private String getText() throws Exception {
		File wFile = new File(CPlatform.getUserDir(), "test/test.rst");

		if (!wFile.exists())
			throw new Exception(String.format("File doesn't exist. %s",
					wFile.getAbsolutePath()));

		return new String(getFileData(wFile), "UTF-8");

	}

	/**
	 * @param msg
	 */
	private void println(String aFormat, Object... aObjects) {
		if (CLog.on()) {
			aObjects = CTools.insertFirstOneObject(aObjects, aFormat);
			CLog.logOut(this, LIB_RUN, aObjects);
		}
	}

	/**
	 * @throws Exception
	 */
	void run() throws Exception {
		// to get log lines
		CLog.forceLogOn();

		if (CLog.on())
			CLog.logOut(this, LIB_RUN, "Begin");

		println("UserDir=[%s]", CPlatform.getUserDir());

		String wText = getText();

		println("Text.length=[%s]", wText.length());

		HunspellCheckIterator wHCI = new HunspellCheckIterator(wText, 0,
				Locale.ENGLISH);

		while (wHCI.hasNext()) {

			println("word=[%s]", wHCI.next());

		}

		if (CLog.on())
			CLog.logOut(this, LIB_RUN, "End");

	}
}
