/**
 *
 */
package com.lorands.hunspell4eclipse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

import com.lorands.hunspell4eclipse.i18n.Messages;
import com.stibocatalog.hunspell.CLog;
import com.stibocatalog.hunspell.CTools;
import com.stibocatalog.hunspell.Hunspell;
import com.stibocatalog.hunspell.Hunspell.Dictionary;

/**
 * Spell checking engine.
 * 
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 28/04/2011 (dd/mm/yy)
 */
public class Engine implements ISpellingEngine {

	private static String DICTIONARY_DOESNT_EXIST_KEY = "engine.dict.doesnt.exist.mess";

	// attention declared in plugin.xml
	public final static String ENGINE_ID = "com.lorands.hunspell4eclipse.engine";

	private static String NO_DICTIONARY_SELECTED_INFO_KEY = "engine.dict.please.select.mess";
	private static String NO_DICTIONARY_SELECTED_TITLE_KEY = "engine.dict.not.selected.mess";

	/**
	 * @param aDictionaryFile
	 *            the file representing the dictionnary data on the file system.<br/>
	 *            eg. "/Users/ogattaz/Library/Spelling/fr.dic"
	 * @return the absolute path of the dictionnary whithout any extension.<br/>
	 *         eg. "/Users/ogattaz/Library/Spelling/fr"
	 */
	static String calcDictionaryPrefix(File aDictionaryFile) {

		String wDictPath = aDictionaryFile.getAbsolutePath();

		final boolean wHasExt = wDictPath.indexOf('.') > -1;

		return (wHasExt) ? wDictPath.substring(0, wDictPath.lastIndexOf('.'))
				: wDictPath;
	}

	/**
	 * <ul>
	 * <li>en_GB
	 * <li>en_US
	 * <li>en_ZA
	 * <li>en
	 * </ul>
	 * 
	 * @param aDirectory
	 *            the file representing the directory in which we must find the
	 *            english dictionaries.
	 * @return the file representing the first english dictionary available in
	 *         the directory.
	 */
	static File getEnglishDictionaryFile(File aDirectory) {
		if (!aDirectory.isDirectory())
			return null;

		// get the list of english dictionaries localized in the directory of
		// the given dictPasth
		String[] wEnglishDictPaths = aDirectory.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name.startsWith(Locale.ENGLISH.getLanguage()) && name
						.endsWith(".dic"));
			}
		});

		if (CLog.on())
			CLog.logOut(Engine.class, "calcEnglishDictPrefix",
					"EnglishDictPaths=[%s]",
					CTools.arrayToString(wEnglishDictPaths, ","));

		// return the first one
		for (String wEnglishDictPath : wEnglishDictPaths) {
			return new File(aDirectory, wEnglishDictPath);
		}

		// no english dict
		return null;
	}

	/**
	 * @param aFilepath
	 * @return
	 */
	static boolean hasEnglishDictionaryInSameDir(String aFilepath) {
		File wDictDir = new File(aFilepath).getParentFile();
		if (wDictDir.exists())
			return getEnglishDictionaryFile(wDictDir) != null;

		return false;
	}

	private boolean initOk = false;
	private Dictionary pEnglishDictionary = null;
	private Dictionary pSelectedDictionary = null;

	/**
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * 
	 */
	public Engine() throws FileNotFoundException, UnsupportedEncodingException {

		HunspellPreferences wPrefs = new HunspellPreferences();

		if (!wPrefs.hasDictionaryPath()) {

			Messages wMessages = Messages.getInstance();
			String wTitle = wMessages
					.getString(NO_DICTIONARY_SELECTED_TITLE_KEY);
			String wInfoText = wMessages
					.getString(NO_DICTIONARY_SELECTED_INFO_KEY);

			// log in StdErr
			CLog.logErr(this, CLog.LIB_CONSTRUCTOR, "%s\näs", wTitle, wInfoText);

			// ask the user !
			if (Hunspell4EclipsePlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow() != null)
				MessageDialog.openError(Hunspell4EclipsePlugin.getDefault()
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						wTitle, wInfoText);

			initOk = false;
		} else {
			String wSelectedDictPath = wPrefs.getDictionaryPath();

			File wSelectedDictFile = new File(wSelectedDictPath);

			if (!wSelectedDictFile.exists())
				throw new FileNotFoundException(String.format(Messages
						.getInstance().getString(DICTIONARY_DOESNT_EXIST_KEY,
								wSelectedDictPath)));

			final Hunspell hunspell = Hunspell4EclipsePlugin.getDefault()
					.getHunspell();

			// "/usr/share/myspell/dicts/hu_HU"
			// "/Users/ogattaz/Library/Spelling/fr"
			pSelectedDictionary = hunspell
					.getDictionary(calcDictionaryPrefix(wSelectedDictFile));

			//
			if (wPrefs.acceptEngishWords()) {
				File wEnglishDictFile = getEnglishDictionaryFile(wSelectedDictFile
						.getParentFile());
				if (wEnglishDictFile != null)
					pEnglishDictionary = hunspell
							.getDictionary(calcDictionaryPrefix(wEnglishDictFile));
			}
			initOk = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingEngine#check(org.eclipse.
	 * jface.text.IDocument, org.eclipse.jface.text.IRegion[],
	 * org.eclipse.ui.texteditor.spelling.SpellingContext,
	 * org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void check(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {
		if (!initOk) {
			return;
		}

		// find spell engine for contet
		IContentType contentType = context.getContentType();

		// diagnose (activated if the "hunspell.log.on" system
		// property is defined).
		if (CLog.on())
			CLog.logOut(this, "check", "SpellingContext.getContentType=[%s]",
					traverseContentType(0, contentType));

		AbstractHunSpellEngine spellEngine = findContentProvider(contentType);

		spellEngine.setSelectedDictionary(pSelectedDictionary);
		spellEngine.setEnglishDictionary(pEnglishDictionary);

		spellEngine.setOptions(Hunspell4EclipsePlugin.getDefault()
				.getPreferenceStore()
				.getInt(Hunspell4EclipsePlugin.SPELLING_OPTIONS));
		spellEngine.check(document, regions, context, collector, monitor);

	}

	/**
	 * Find spell engine or return default "SimpleTextEngine".
	 * <p>
	 * Try to find most suitable spell engine, which means if not found for the
	 * given content type try it's parent, and so on.
	 * <p>
	 * If none found, will return an instance "SimpleTextEngine"which would mean
	 * use the default text one.
	 * 
	 * @param contentType
	 * @return
	 */
	private AbstractHunSpellEngine findContentProvider(IContentType contentType) {
		/*
		 * org.eclipse.core.runtime.text org.eclipse.jdt.core.javaSource
		 * org.eclipse.core.runtime.xml
		 */
		AbstractHunSpellEngine engine = Hunspell4EclipsePlugin
				.findEngine(contentType.getId());
		if (engine != null)
			return engine;

		IContentType baseType = contentType.getBaseType();
		if (baseType != null)
			return findContentProvider(baseType);

		return new SimpleTextEngine();
	}

	/**
	 * @param level
	 *            the current level.
	 * @param contentType
	 * @return a string containing the dump of the chain of contentTypes
	 */
	private String traverseContentType(int level, IContentType contentType) {
		StringBuilder wSB = new StringBuilder();
		wSB.append(level);
		wSB.append("=");
		wSB.append(contentType.getId());
		IContentType baseType = contentType.getBaseType();
		if (baseType != null) {
			wSB.append(',');
			wSB.append(traverseContentType(level + 1, baseType));
		}
		return wSB.toString();
	}
}