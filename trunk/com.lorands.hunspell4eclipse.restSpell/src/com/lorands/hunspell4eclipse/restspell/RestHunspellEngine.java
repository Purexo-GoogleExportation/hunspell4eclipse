/*******************************************************************************
 * Copyright (c) 2011 isandlaTech, Olivier Gattaz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olivier Gattaz (isandlaTech) - initial API and implementation
 *******************************************************************************/
package com.lorands.hunspell4eclipse.restspell;

import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.isandlatech.plugins.rest.parser.RestLanguage;

import com.lorands.hunspell4eclipse.HunspellEngineBase;
import com.lorands.hunspell4eclipse.HunspellCheckIterator;
import com.lorands.hunspell4eclipse.ICompletionProposalCreator;
import com.lorands.hunspell4eclipse.IHunspellCheckIterator;
import com.stibocatalog.hunspell.CLog;

/**
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 10/06/2011 (dd/mm/yy)
 * 
 */
public final class RestHunspellEngine extends HunspellEngineBase {

	/**
	 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
	 * @date 10/06/2011 (dd/mm/yy)
	 * 
	 */
	class RestHunspellCheckIterator extends HunspellCheckIterator {

		/**
		 * @param document
		 * @param region
		 * @param locale
		 */
		public RestHunspellCheckIterator(IDocument document, IRegion region,
				Locale locale) {
			super(document, region, locale);
		}

		/**
		 * @param aToken
		 * @return
		 */
		private boolean isRestOrSphinxWords(String aToken) {
			if (aToken == null)
				return false;

			boolean wResult = false;
			if (aToken.length() > 2
					&& RestLanguage.FIELD_MARKER.equals(super.getTokenNext())) {

				if (super.isToken(aToken, RestLanguage.DIRECTIVES)
						|| super.isToken(aToken, RestLanguage.SPHINX_DIRECTIVES)
						|| super.isToken(aToken,
								RestLanguage.SPHINX_EXTENSIONS_DIRECTIVES)
						|| super.isToken(aToken,
								RestHunspellEngine.SPHINX_DIRECTIVES_PARAMS)) {
					wResult = true;
				}
			}
			// diagnose (activated if the "hunspell.log.on"
			// system
			// property is defined).
			if (CLog.on())
				CLog.logOut(this, "isRestOrSphinxWords",
						"[%s] is Rest or Sphinx token [%b]",
						String.valueOf(aToken), wResult);

			return wResult;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.text.spelling.SpellCheckIterator#next()
		 */
		@Override
		public String next() {

			String token = super.next();

			while (isRestOrSphinxWords(token))
				token = super.next();

			return token;
		}
	}

	/**
	 * @author Lorand Somogyi
	 * 
	 */
	public static class RestProposalCreator implements
			ICompletionProposalCreator {

		private static final int relevance = 1;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.lorands.hunspell4eclipse.ICompletionProposalCreator#createProposal
		 * (java.lang.String, int, int, int)
		 */
		@Override
		public ICompletionProposal createProposal(String replacementString,
				int replacementOffset, int replacementLength, int cursorPosition) {

			return new CompletionProposal(replacementString, replacementOffset,
					replacementLength, cursorPosition);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.lorands.hunspell4eclipse.ICompletionProposalCreator#setup(java
		 * .util.Map)
		 */
		@Override
		public void setup(Map<String, ?> configuration) {
			// nothing
		}
	}

	private static final IRegion[] EMPTY_REGION_ARRAY = new IRegion[0];

	private static final String[] SPHINX_DIRECTIVES_PARAMS = { "alt",
			"maxdepth" };

	/**
	 * 
	 */
	public RestHunspellEngine() {
		super();
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

		// call the check implemented in the abstract AbstractHunSpellEngine
		super.checkInner(document, regions, context, collector, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lorands.hunspell4eclipse.AbstractHunSpellEngine#checkOneRegion(org
	 * .eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion,
	 * org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector,
	 * org.eclipse.core.runtime.IProgressMonitor, int)
	 */
	@Override
	protected int checkOneRegion(IDocument document, IRegion region,
			ISpellingProblemCollector collector, IProgressMonitor monitor,
			int aNbFoundProblem) {

		if (CLog.on())
			CLog.logOut(this, "checkOneRegion",
					"region: ofset=[%d] length=[%d]", region.getOffset(),
					region.getLength());

		// reuse the internal class SpellCheckIterator of the jdt to have the
		// same word splitting rules
		IHunspellCheckIterator wCheckIterator = new RestHunspellCheckIterator(
				document, region, getSelectedDictionary().getLocale());

		// set one of the option
		wCheckIterator.setIgnoreSingleLetters(isSingleLetterIgnored());

		int wI = 0;
		while (wCheckIterator.hasNext()) {
			Object wToken = wCheckIterator.next();
			if (wToken != null) {
				String wWord = String.valueOf(wToken);
				int wDistance = wCheckIterator.getBegin();
				wI++;
				if (CLog.on())
					CLog.logOut(this, "checkOneRegion", "word(%d)=[%s][%d]",
							wI, wWord, wDistance);

				if (!super.checkOneWord(document, region, collector, wWord,
						wDistance)) {
					aNbFoundProblem++;
					// limit reached, get out
					if (aNbFoundProblem >= getNbAcceptedProblems())
						return -1;
				}
			}
		}
		return aNbFoundProblem;
	}

}
