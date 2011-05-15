package com.stibocatalog.hunspell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * The simple hunspell library frontend which takes care of creating and
 * singleton'ing the library instance (no need to load it more than once per
 * process) .
 * 
 * The Hunspell java bindings are licensed under LGPL, see the file COPYING.txt
 * in the root of the distribution for the exact terms.
 * 
 * @author Flemming Frandsen (flfr at stibo dot com)
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 28/04/2011 (dd/mm/yy)
 */

public class Hunspell {

	/**
	 * Class representing a single dictionary.
	 */
	public class Dictionary {

		/**
		 * The encoding used by this dictionary
		 */
		private final String encoding;

		/**
		 * The pointer to the hunspell object as returned by the hunspell
		 * constructor.
		 */
		private Pointer hunspellDict = null;

		/**
		 * The encoding of this dictionary
		 */
		private final Locale pLocale;

		/**
		 * Creates an instance of the dictionary.
		 * 
		 * @param baseFileName
		 *            the base name of the dictionary. the base name of the
		 *            dictionary, passing /dict/da_DK means that the files
		 *            /dict/da_DK.dic and /dict/da_DK.aff get loaded
		 */
		Dictionary(String baseFileName) throws FileNotFoundException,
				UnsupportedEncodingException {
			File dic = new File(baseFileName + ".dic");
			File aff = new File(baseFileName + ".aff");

			if (!dic.canRead() || !aff.canRead()) {
				throw new FileNotFoundException("The dictionary files "
						+ baseFileName + "(.aff|.dic) could not be read");
			}

			hunspellDict = hsl.Hunspell_create(aff.toString(), dic.toString());
			encoding = hsl.Hunspell_get_dic_encoding(hunspellDict);

			pLocale = initLocaleFromDicFilename(dic.getName());

			// This will blow up if the encoding doesn't exist
			stringToBytes("test");
		}

		/**
		 * Deallocate the dictionary.
		 */
		public void destroy() {
			if (hsl != null && hunspellDict != null) {
				hsl.Hunspell_destroy(hunspellDict);
				hunspellDict = null;
			}
		}

		/**
		 * @return
		 */
		public String getEncoding() {
			return encoding;
		}

		/**
		 * @return the Locale of this dictionary
		 */
		public Locale getLocale() {
			return pLocale;
		}

		/**
		 * @param wDicFileName
		 * @return
		 */
		private Locale initLocaleFromDicFilename(String wDicFileName) {

			String wLocaleId = wDicFileName.substring(0,
					wDicFileName.lastIndexOf('.'));

			int wPosUnderScore = wLocaleId.indexOf('_');
			boolean wHasCountry = wPosUnderScore > -1
					&& wPosUnderScore < wLocaleId.length() - 1;

			String wLangage = (wHasCountry) ? wLocaleId.substring(0,
					wPosUnderScore) : wLocaleId;

			if (!wHasCountry)
				return new Locale(wLangage);

			String wCountry = wLocaleId.substring(wPosUnderScore + 1);

			return new Locale(wLangage, wCountry);
		}

		/**
		 * Check if a word is spelled correctly
		 * 
		 * @param word
		 *            The word to check.
		 */
		public boolean misspelled(String word) {
			try {
				return hsl.Hunspell_spell(hunspellDict, stringToBytes(word)) == 0;
			} catch (UnsupportedEncodingException e) {
				return true; // this should probably never happen.
			}
		}

		/**
		 * Returns a list of stems
		 * 
		 * @param word
		 *            The word to get stems for
		 * @return List of stems or null if the word doesn't exist in dictionary
		 */
		public List<String> stem(String word) {
			List<String> res = new ArrayList<String>();
			try {
				int stemsCount = 0;

				PointerByReference stems = new PointerByReference();
				stemsCount = hsl.Hunspell_stem(hunspellDict, stems,
						stringToBytes(word));

				if (stemsCount == 0)
					return null;

				Pointer[] pointerArray = stems.getValue().getPointerArray(0,
						stemsCount);

				for (int i = 0; i < stemsCount; i++) {
					/* Flemming's comment... */
					/*
					 * This only works for 8 bit chars, luckily hunspell uses
					 * either 8 bit encodings or utf8, if someone implements
					 * support in hunspell for utf16 we are in trouble.
					 */
					long len = pointerArray[i].indexOf(0, (byte) 0);
					if (len != -1) {
						if (len > Integer.MAX_VALUE) {
							throw new RuntimeException(
									"String improperly terminated: " + len);
						}
						byte[] data = pointerArray[i]
								.getByteArray(0, (int) len);
						res.add(new String(data, encoding));
					}
				}
			} catch (UnsupportedEncodingException ex) {
			}

			return res;
		}

		/**
		 * Convert a Java string to a zero terminated byte array, in the
		 * encoding of the dictionary, as expected by the hunspell functions.
		 */
		protected byte[] stringToBytes(String str)
				throws UnsupportedEncodingException {
			return (str + "\u0000").getBytes(encoding);
		}

		/**
		 * Returns a list of suggestions
		 * 
		 * @param word
		 *            The word to check and offer suggestions for
		 */
		public List<String> suggest(String word) {
			List<String> res = new ArrayList<String>();
			int suggestionsCount = 0;
			try {
				PointerByReference suggestions = new PointerByReference();
				suggestionsCount = hsl.Hunspell_suggest(hunspellDict,
						suggestions, stringToBytes(word));

				// ogattaz - if no suggestion the "pointer array" is null, so
				// break
				if (suggestionsCount == 0)
					return res;

				// Get each of the suggestions out of the pointer array.
				Pointer[] pointerArray = suggestions.getValue()
						.getPointerArray(0, suggestionsCount);

				for (int i = 0; i < suggestionsCount; i++) {

					/*
					 * This only works for 8 bit chars, luckily hunspell uses
					 * either 8 bit encodings or utf8, if someone implements
					 * support in hunspell for utf16 we are in trouble.
					 */
					long len = pointerArray[i].indexOf(0, (byte) 0);
					if (len != -1) {
						if (len > Integer.MAX_VALUE) {
							throw new RuntimeException(
									"String improperly terminated: " + len);
						}
						byte[] data = pointerArray[i]
								.getByteArray(0, (int) len);
						res.add(new String(data, encoding));
					}
				}

			} catch (UnsupportedEncodingException ex) {
				// Shouldn't happen...
				CLog.logErr(this, "suggest", ex, "UnsupportedEncodingException");
			}

			// diagnose (activated if system the "hunspell.log.on" property is
			// defined).
			if (CLog.on()) {
				CLog.logOut(this, "suggest", "suggestionsCount=[%d] dump=[%s]",
						suggestionsCount, CTools.listToString(res, ","));
			}
			return res;
		}

		@Override
		public String toString() {
			return String.format("Dictionary: Locale=[%s] encoding=[%s]",
					getLocale().toString(), getEncoding());
		}
	}

	/**
	 * The Singleton instance of Hunspell
	 */
	private static Hunspell hunspell = null;

	/**
	 * The instance of the HunspellManager, looks for the native lib in the
	 * default directories
	 */
	public static Hunspell getInstance() throws UnsatisfiedLinkError,
			UnsupportedOperationException {
		return getInstance(null);
	}

	/**
	 * The instance of the HunspellManager, looks for the native lib in the
	 * directory specified.
	 * 
	 * @param libDir
	 *            Optional absolute directory where the native lib can be found.
	 */
	public static Hunspell getInstance(String libDir)
			throws UnsatisfiedLinkError, UnsupportedOperationException {
		if (hunspell != null) {
			return hunspell;
		}

		hunspell = new Hunspell(libDir);
		return hunspell;
	}

	/**
	 * Calculate the filename of the native hunspell lib. The files have
	 * completely different names to allow them to live in the same directory
	 * and avoid confusion.
	 */
	public static String libName() throws UnsupportedOperationException {
		// Windows
		if (CPlatform.isWindows())
			return libNameBare() + ".dll";

		// Mac Os X
		if (CPlatform.isMacOs())
			return "lib" + libNameBare() + ".dylib";

		// Linux, Solaris, ...
		return "lib" + libNameBare() + ".so";
	}

	public static String libNameBare() throws UnsupportedOperationException {

		// Windows
		if (CPlatform.isWindows()) {
			if (CPlatform.is32Bits())
				return "hunspell-win-x86-32";

			if (CPlatform.is64Bits()) {
				// Note: No bindings exist for this yet (no JNA support).
				// return "hunspell-win-x86-64";
			}

		}
		// Mac Os X
		else if (CPlatform.isMacOs()) {
			if (CPlatform.is32Bits())
				return "hunspell-darwin-x86-32";

			if (CPlatform.isMacPowerPc())
				return "hunspell-darwin-ppc-32";

			if (CPlatform.is64Bits())
				return "hunspell-darwin-x86-64";

		}
		// Linux
		else if (CPlatform.isLinux()) {
			if (CPlatform.is32Bits())
				return "hunspell-linux-x86-32";
			if (CPlatform.is64Bits())
				return "hunspell-linux-x86-64";

		}

		throw new UnsupportedOperationException("Unknown OS/arch: ["
				+ CPlatform.getOsName() + "] [" + CPlatform.getOsArch() + ']');
	}

	/**
	 * The native library instance, created by JNA.
	 */
	private HunspellLibrary hsl = null;

	/**
	 * This is the cache where we keep the already loaded dictionaries around
	 */
	private final HashMap<String, Dictionary> map = new HashMap<String, Dictionary>();

	/**
	 * Constructor for the library, loads the native lib.
	 * 
	 * Loading is done in the first of the following three ways that works: 1)
	 * Unmodified load in the provided directory. 2) libFile stripped back to
	 * the base name (^lib(.*)\.so on unix) 3) The library is searched for in
	 * the classpath, extracted to disk and loaded.
	 * 
	 * @param libDir
	 *            Optional absolute directory where the native lib can be found.
	 * @throws UnsupportedOperationException
	 *             if the OS or architecture is simply not supported.
	 */
	protected Hunspell(String libDir) throws UnsatisfiedLinkError,
			UnsupportedOperationException {

		String libFile = (libDir != null) ? libDir + "/" + libName()
				: libNameBare();
		try {
			// diagnose (activated if the "hunspell.log.on" system property is
			// defined).
			if (CLog.on())
				CLog.logOut(this, CLog.LIB_CONSTRUCTOR,
						"Loading lib : libFile=[%s]", libFile);

			hsl = (HunspellLibrary) Native.loadLibrary(libFile,
					HunspellLibrary.class);
		} catch (UnsatisfiedLinkError urgh) {

			// diagnose (activated if the "hunspell.log.on" system property is
			// defined).
			if (CLog.on())
				CLog.logOut(this, CLog.LIB_CONSTRUCTOR,
						"Loading lib : libFile not in System path", libFile);

			// Oh dear, the library was not found in the file system, let's try
			// the classpath
			libFile = libName();
			InputStream is = Hunspell.class.getResourceAsStream('/' + libFile);
			if (is == null) {
				throw new UnsatisfiedLinkError("Can't find [" + libFile
						+ "] in the filesystem nor in the classpath\n" + urgh);
			}

			// Extract the library from the classpath into a temp file.
			File lib;
			FileOutputStream fos = null;
			try {
				lib = File.createTempFile("jna", '_' + libFile);

				// diagnose (activated if the "hunspell.log.on" system property
				// is defined).
				if (CLog.on())
					CLog.logOut(this, CLog.LIB_CONSTRUCTOR,
							"Writing temp lib : name=[%s]", lib.getName());

				lib.deleteOnExit();
				fos = new FileOutputStream(lib);
				int count;
				int wSize = 0;
				byte[] buf = new byte[1024];
				while ((count = is.read(buf, 0, buf.length)) > 0) {
					fos.write(buf, 0, count);
					wSize += count;
				}

				// diagnose (activated if the "hunspell.log.on" system property
				// is defined).
				if (CLog.on())
					CLog.logOut(this, CLog.LIB_CONSTRUCTOR,
							"Writing temp lib : exists=[%b] size=[%s]",
							lib.exists(), wSize);

			} catch (IOException e) {
				throw new Error("Failed to create temporary file for ["
						+ libFile + ']', e);

			} finally {
				try {
					is.close();
				} catch (IOException e) {
					if (CLog.on())
						CLog.logOut(this, CLog.LIB_CONSTRUCTOR,
								"IOException during is.close [%s]",
								e.getLocalizedMessage());
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						if (CLog.on())
							CLog.logOut(this, CLog.LIB_CONSTRUCTOR,
									"IOException during fos.close [%s]",
									e.getLocalizedMessage());
					}
				}
			}

			// diagnose (activated if the "hunspell.log.on" system property is
			// defined).
			if (CLog.on())
				CLog.logOut(this, CLog.LIB_CONSTRUCTOR,
						"Loading temp lib : AbsolutePath=[%s]",
						lib.getAbsolutePath());

			hsl = (HunspellLibrary) Native.loadLibrary(lib.getAbsolutePath(),
					HunspellLibrary.class);
		}
	}

	/**
	 * Removes a dictionary from the internal cache
	 * 
	 * @param baseFileName
	 *            the base name of the dictionary, as passed to getDictionary()
	 */
	public void destroyDictionary(String baseFileName) {
		if (map.containsKey(baseFileName)) {
			map.remove(baseFileName);
		}
	}

	/**
	 * Gets an instance of the dictionary.
	 * 
	 * @param baseFileName
	 *            the base name of the dictionary, passing /dict/da_DK means
	 *            that the files /dict/da_DK.dic and /dict/da_DK.aff get loaded
	 */
	public Dictionary getDictionary(String baseFileName)
			throws FileNotFoundException, UnsupportedEncodingException {

		Dictionary d;
		// TODO: Detect if the dictionary files have changed and reload if they
		// have.
		if (map.containsKey(baseFileName)) {
			d = map.get(baseFileName);
		} else {
			d = new Dictionary(baseFileName);
			map.put(baseFileName, d);
		}

		if (CLog.on())
			CLog.logOut(this, "getDictionary", "Dictionary :[%s", d.toString());

		return d;
	}

	protected void tryLoad(String libFile) throws UnsupportedOperationException {
		hsl = (HunspellLibrary) Native.loadLibrary(libFile,
				HunspellLibrary.class);
	}
}
