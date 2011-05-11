/**
 *
 */
package com.lorands.hunspell4eclipse;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

import com.stibocatalog.hunspell.CLog;
import com.stibocatalog.hunspell.Hunspell.Dictionary;

/**
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 28/04/2011 (dd/mm/yy)
 * 
 */
public abstract class AbstractHunSpellEngine implements ISpellingEngine {

	private final static String PROBLEM_MESSAGE_FORMAT = "Hunspell: the word '%s' is not correctly spelled ( %s proposal%s)";
	private static final ICompletionProposal[] PROPOSALS_EMPTY_ARRAY = new ICompletionProposal[0];
	private Dictionary dictionary;
	private int opts;

	private final IPreferenceStore preferenceStore;

	private int threshold;

	/**
	 * 
	 */
	public AbstractHunSpellEngine() {
		this.preferenceStore = Activator.getDefault().getPreferenceStore();
		// TODO this part should be in some kind of utility
		this.threshold = preferenceStore.getInt(Activator.THRESHOLD);
		if (threshold == 0) {
			threshold = 100;
		}
	}

	/**
	 * @param region
	 *            the current region
	 * @param str
	 *            a wrong word
	 * @param distance
	 * @return an instance of SpellingProblem containing a array of proposal(s)
	 */
	private SpellingProblem buildSpellingProblem(final IRegion region,
			final String str, final int strLength, int distance) {

		SpellingProblem problem = null;
		final int inOffset = region.getOffset() + distance;

		// get suggestions
		final List<String> suggestList = getDictionary().suggest(str);

		final List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();

		for (final String suggest : suggestList) {

			ICompletionProposal proposal;

			if (hasCompletionProposalCreator()) {
				proposal = getCompletionProposalCreator().createProposal(
						suggest, inOffset, strLength, strLength);
			} else {
				proposal = new CompletionProposal(suggest, inOffset, strLength,
						strLength);
			}
			proposalList.add(proposal);
		}
		int wNbProposal = proposalList.size();

		problem = new HunspellingProblem(inOffset, strLength, String.format(
				PROBLEM_MESSAGE_FORMAT, str, wNbProposal,
				(wNbProposal > 1) ? "s" : ""),
				proposalList.toArray(PROPOSALS_EMPTY_ARRAY));

		// diagnose (activated if the "hunspell.log.on"
		// system property is defined).
		if (CLog.on())
			CLog.logOut(this, "checkInner",
					"Problem Message=[%s] Length=[%d] Offset=[%d]",
					problem.getMessage(), problem.getLength(),
					problem.getOffset());

		return problem;
	}

	/**
	 * @param document
	 *            the current document
	 * @param regions
	 *            the array of regions to parse
	 * @param context
	 * @param collector
	 * @param monitor
	 */
	protected final void checkInner(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {

		int cntr = 0;

		// diagnose (activated if the "hunspell.log.on" system property is
		// defined).
		if (CLog.on())
			CLog.logOut(this, "checkInner", " nb regions=[%d]", regions.length);

		for (final IRegion region : regions) {
			// the user wants to stop ?
			if (monitor != null && monitor.isCanceled())
				return;

			// retrieve the doc part of the document corresponding to the region
			int wRegionOffset = region.getOffset();
			int wRegionLength = region.getLength();
			String docPart = null;
			try {
				docPart = document.get(wRegionOffset, wRegionLength);
			} catch (final BadLocationException e) {
				CLog.logErr(
						this,
						"checkInner",
						e,
						"Spelling Service provided offset/length that points out of the document. RegionOffset=[%d] RegionLength=[%d]",
						wRegionOffset, wRegionLength);
				// ends the method
				return;
			}

			// diagnose (activated if the "hunspell.log.on" system
			// property is defined).
			if (CLog.on())
				CLog.logOut(
						this,
						"checkInner",
						"RegionOffset=[%d] RegionLength=[%d] docPartSize=[%d] ",
						wRegionOffset, wRegionLength, docPart.length());

			BreakIterator bi = BreakIterator.getWordInstance(getDictionary()
					.getLocale());

			bi.setText(docPart);

			int wWordIdx = 0;
			String wWord;
			int wWordLength;
			int wWordDistance = 0;
			int wFirstIndex = 0;
			while (bi.next() != BreakIterator.DONE) {
				// retrieve the word
				// TODO : must manage the composite word including non letter
				// character like "@param"
				wWord = docPart.substring(wFirstIndex, bi.current());
				wWordLength = wWord.length();
				wWordIdx++;
				// the distance of the current word in the region is its
				// firstIndex
				wWordDistance = wFirstIndex;

				// diagnose (activated if the "hunspell.log.on" system
				// property is defined).
				if (CLog.on())
					CLog.logOut(this, "checkInner",
							"WordIdx=[%d] Word=[%s] len=[%d]", wWordIdx, wWord,
							wWordLength);

				// if the word must be checked and id it is misspelled
				if (checkRules(wWord) && getDictionary().misspelled(wWord)) {
					// adds a new spelling problem in the collector
					collector.accept(buildSpellingProblem(region, wWord,
							wWordLength, wWordDistance));
					cntr++;
					// limit reached, get out
					if (cntr >= threshold)
						return;
				}

				wFirstIndex = bi.current();
			}
		}
	}

	/**
	 * @param str
	 * @return
	 */
	private boolean checkRules(String str) {
		// forced default rules
		if (str.length() == 0 || str.matches("\\s+")) {
			return false;
		}

		// option rules
		// SingleLetter
		if ((opts & 1) == 1) {
			if (str.length() == 1) {
				return false;
			}
		}

		// UpperCase
		if ((opts & 2) == 2) {
			if (str.toUpperCase().equals(str)) {
				return false;
			}
		}

		// WWDigitsIgnored
		if ((opts & 4) == 4) {
			if (str.matches(".*[\\d]+.*")) {
				return false;
			}
		}

		// WWMixedCaseIgnored
		if ((opts & 8) == 8 && str.length() > 1) {

			// "Tomorrow" is not a mixed case word : only the first char is in
			// upper case.
			// "CMyClass" is a mixed case word

			String wTestedPart = str.substring(1);

			// The test rules :
			// a) abCd => ABCD ( => different) && abCd => abcd ( => different)
			// ===> Mixed case
			// b) abcd => ABCD ( => different) && abcd => abcd ( => equal)
			// ===> Not mixed case
			// c) ABCD => ABCD ( => equal) && ABCD => abcd ( => different)
			// ===> Not mixed case

			if (!wTestedPart.toUpperCase().equals(wTestedPart)
					&& !wTestedPart.toLowerCase().equals(wTestedPart)) {
				return false;
			}
		}
		// WWNonLetters
		if ((opts & 16) == 16) {
			if (str.matches("[^\\p{L}]+")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get completion proposal provider or null if none.
	 * 
	 * @return
	 */
	public abstract ICompletionProposalCreator getCompletionProposalCreator();

	/**
	 * @return
	 */
	public final Dictionary getDictionary() {
		return dictionary;
	}

	/**
	 * @return
	 */
	public abstract boolean hasCompletionProposalCreator();

	/**
	 * @param dictionary
	 */
	public final void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * @param opts
	 */
	public void setOptions(int opts) {
		this.opts = opts;
	}
}