/**
 * 
 */
package com.lorands.hunspell4eclipse;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

/**
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 */
public final class SimpleTextEngine extends AbstractHunSpellEngine {

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

		// call the check implemented in the abstract AbstractHunSpellEngine
		checkInner(document, regions, context, collector, monitor);
	}

}
