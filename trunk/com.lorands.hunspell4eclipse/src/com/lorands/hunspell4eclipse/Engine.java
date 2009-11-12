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

import com.stibocatalog.hunspell.Hunspell;
import com.stibocatalog.hunspell.Hunspell.Dictionary;

/** Spell checking engine. 
 * 
 * @author Lorand Somogyi
 * 
 */
public class Engine implements ISpellingEngine {

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
			MessageDialog
					.openError(
							Activator.getDefault().getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							"No dictionary selected",
							"Pleases select a dictionray in Window > Preferences > General > Editors > Text Editors > Spelling.");
			initOk = false;
		} else {
			final Hunspell hunspell = Activator.getDefault().getHunspell();
			final String dictPerfix = dictPath.substring(0, dictPath.lastIndexOf('.'));
			dictionary = hunspell.getDictionary(dictPerfix); // "/usr/share/myspell/dicts/hu_HU"
			initOk = true;
		}
	}
	
	private void traverseContentType(int d, IContentType contentType) { //just for test
		StringBuilder sb = new StringBuilder();
		for( int i=0; i <= d; i++ ) {
			sb.append("\t");
		}
		sb.append(contentType.getId());
		System.out.println(sb.toString());
		IContentType baseType = contentType.getBaseType();
		if( baseType != null) {
			traverseContentType(d+1, baseType);
		}
	}
	
	/** Find spell engine or return null. Try to find most suitable spell engine, 
	 * which means if not found for the given content type try it's parent, and so on.
	 * If none found, will return null, which would mean use the default text one.
	 * 
	 * @param contentType
	 * @return
	 */
	private AbstractHunSpellEngine findContentProvider(IContentType contentType) {
		String id = contentType.getId();
		/*
		 * org.eclipse.core.runtime.text
			org.eclipse.jdt.core.javaSource
			org.eclipse.core.runtime.xml
		 */
		AbstractHunSpellEngine engine = Activator.findEngine(id);
		if( engine == null ) {
			IContentType baseType = contentType.getBaseType();
			if( baseType != null) {
				findContentProvider(baseType);
			}
		} else {
			return engine;
		}
		
		return null;
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.spelling.ISpellingEngine#check(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion[], org.eclipse.ui.texteditor.spelling.SpellingContext, org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void check(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {
		if (!initOk) {
			return;
		}

		//find spell engine for contet
		IContentType contentType = context.getContentType();
		AbstractHunSpellEngine spellEngine = findContentProvider(contentType);
		if( spellEngine == null ) {
			spellEngine = new SimpleTextEngine();
		}
		spellEngine.setDictionary(dictionary);
		spellEngine.setOptions(Activator.getDefault()
				.getPreferenceStore().getInt(Activator.DEFAULT_OPTIONS));
		spellEngine.check(document, regions, context, collector, monitor);
		
	}

}
