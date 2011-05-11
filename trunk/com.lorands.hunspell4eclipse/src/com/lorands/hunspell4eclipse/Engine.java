/**
 *
 */
package com.lorands.hunspell4eclipse;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

import com.stibocatalog.hunspell.CLog;
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

	private static String NO_DICTIONARY_SELECTED_INFO = "Pleases select a dictionray in Preferences > General > Editors > Text Editors > Spelling.";
	private static String NO_DICTIONARY_SELECTED_TITLE = "No dictionary selected";

	private Dictionary dictionary;
	private boolean initOk = false;

	/**
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * 
	 */
	public Engine() throws FileNotFoundException, UnsupportedEncodingException {

		final IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();

		final String dictPath = preferenceStore.getString(Activator.DICTPATH);
		if (dictPath == null || dictPath.isEmpty()) {

			// log in StdErr
			CLog.logErr(this, CLog.LIB_CONSTRUCTOR, "%s\näs",
					NO_DICTIONARY_SELECTED_TITLE, NO_DICTIONARY_SELECTED_INFO);

			// ask the user !
			MessageDialog.openError(Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					NO_DICTIONARY_SELECTED_TITLE, NO_DICTIONARY_SELECTED_INFO);

			initOk = false;
		} else {
			final Hunspell hunspell = Activator.getDefault().getHunspell();

			final boolean wHasExt = dictPath.indexOf('.') > -1;

			final String dictPrefix = (wHasExt) ? dictPath.substring(0,
					dictPath.lastIndexOf('.')) : dictPath;
			// "/usr/share/myspell/dicts/hu_HU"
			// "/Users/ogattaz/Library/Spelling/fr"
			dictionary = hunspell.getDictionary(dictPrefix);
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
		if (spellEngine == null) {
			spellEngine = new SimpleTextEngine();
		}
		spellEngine.setDictionary(dictionary);
		spellEngine.setOptions(Activator.getDefault().getPreferenceStore()
				.getInt(Activator.DEFAULT_OPTIONS));
		spellEngine.check(document, regions, context, collector, monitor);

	}

	/**
	 * Find spell engine or return null. Try to find most suitable spell engine,
	 * which means if not found for the given content type try it's parent, and
	 * so on. If none found, will return null, which would mean use the default
	 * text one.
	 * 
	 * @param contentType
	 * @return
	 */
	private AbstractHunSpellEngine findContentProvider(IContentType contentType) {
		String id = contentType.getId();
		/*
		 * org.eclipse.core.runtime.text org.eclipse.jdt.core.javaSource
		 * org.eclipse.core.runtime.xml
		 */
		AbstractHunSpellEngine engine = Activator.findEngine(id);
		if (engine == null) {
			IContentType baseType = contentType.getBaseType();
			if (baseType != null) {
				findContentProvider(baseType);
			}
		} else {
			return engine;
		}

		return null;
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