package com.stibocatalog.hunspell.test;

import java.io.File;
import java.util.Iterator;

import com.stibocatalog.hunspell.CLog;
import com.stibocatalog.hunspell.CPlatform;
import com.stibocatalog.hunspell.Hunspell;

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

	/**
	 * 
	 * baseFileName the base name of the dictionary, passing /dict/da_DK means
	 * that the files /dict/da_DK.dic and /dict/da_DK.aff get loaded
	 * 
	 * @param aParentDir
	 * @return
	 */
	private String getDicBaseFileName(String aParentDir) {
		return aParentDir + '/' + CPlatform.getUserLanguage();
	}

	/**
	 * The state of Open Source spell checking on Mac OS X
	 * 
	 * Starting with OS X 10.6 (Snow Leopard) Apple has implemented a near
	 * identical service to OpenXSpell under the name OpenSpell. Due to the
	 * confusion resulting from such similar naming, OpenXSpell will now be
	 * referred to as just Xspell.
	 * 
	 * If you want to do so just drag a Hunspell dictionary .dic & .aff file
	 * pair for your language to ~/Library/Spelling or /Library/Spelling. The
	 * latter location will make it available to all system users.
	 * 
	 * @return
	 */
	private String getDicDir() {
		String wDir = null;

		if (System.getProperties().containsKey("root")) {
			wDir = System.getProperty("root");
		}

		if ((wDir == null || wDir.isEmpty()) && CPlatform.isMacOs())
			wDir = new File(CPlatform.getUserHome(), "Library/Spelling")
					.getAbsolutePath();

		if (wDir == null || wDir.isEmpty())
			wDir = "/home/ff/projects/hunspell";
		return wDir;
	}

	/**
	 * @param msg
	 */
	private void println(String aFormat, Object... aObjects) {
		if (CLog.on())
			CLog.logOut(this, LIB_RUN, aFormat, aObjects);
	}

	/**
	 * @throws Exception
	 */
	void run() throws Exception {

		CLog.forceLogOn();

		if (CLog.on())
			CLog.logOut(this, LIB_RUN, "Starting");

		// if mac os => dir = /Users/xxx/Library/Spelling
		String wDicDir = getDicDir();

		// if french platform => BaseFileName = /Users/xxx/Library/Spelling/fr
		String wDicBaseFileName = getDicBaseFileName(wDicDir);

		if (CLog.on())
			CLog.logOut(this, LIB_RUN,
					"Loading Hunspell dictionary BaseFileName=[%s]",
					wDicBaseFileName);

		Hunspell.Dictionary wDictionary = Hunspell.getInstance().getDictionary(
				wDicBaseFileName);

		if (CLog.on())
			CLog.logOut(this, LIB_RUN, "Opened dictionary=[%s]",
					wDictionary.toString());

		// french, english, danish words...
		String words[] = { "Test", "Hest", "guest", "ombudsmandshat",
				"ombudsman", "ymerfest", "Issue", "Tracking", "garageport",
				"postbil", "huskop", "arne", "pladderballe", "Doctor", "Leo",
				"Lummerkrog", "Barnevognsbrand", "comme", "son", "nom",
				"indique", "un", "dictionnaire", "linguistique" };

		for (int i = 0; i < words.length; i++) {

			String wTestedWord = words[i];
			if (wDictionary.misspelled(wTestedWord)) {
				Iterator<String> wSuggestions = wDictionary
						.suggest(wTestedWord).iterator();
				StringBuilder wSuggestedWords = new StringBuilder();
				String wSuggestedWord;
				while (wSuggestions.hasNext()) {
					wSuggestedWord = wSuggestions.next();
					wSuggestedWords.append(',').append(wSuggestedWord);
				}
				println("KO > %s \tTry: %s", wTestedWord,
						wSuggestedWords.toString());
			} else {
				println("OK > %s ", wTestedWord);
			}

		}

	}
}
