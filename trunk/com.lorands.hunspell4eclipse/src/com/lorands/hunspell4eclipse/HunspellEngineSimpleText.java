/*******************************************************************************
 * Copyright (c) 2011 lorands.com, L—r‡nd Somogyi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    L—r‡nd Somogyi (lorands.com) - initial API and implementation
 *    Olivier Gattaz (isandlaTech) - improvments
 *******************************************************************************/
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
public final class HunspellEngineSimpleText extends HunspellEngineBase {

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
