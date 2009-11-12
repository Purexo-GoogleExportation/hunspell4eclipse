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
 * @author Lorand Somogyi
 *
 */
public final class SimpleTextEngine extends AbstractHunSpellEngine {
	@Override
	public void check(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {

			checkInner(document, regions, context, collector, monitor);
	}
}
