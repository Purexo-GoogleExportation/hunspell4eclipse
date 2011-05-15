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

	private final int pNbAcceptedProblems;
	private final int pNbMaxProposals;
	private final IPreferenceStore preferenceStore;

	/**
	 * 
	 */
	public AbstractHunSpellEngine() {
		this.preferenceStore = Hunspell4EclipsePlugin.getDefault().getPreferenceStore();

		pNbAcceptedProblems = getJdtUiIntPreference(
				Hunspell4EclipsePlugin.SPELLING_PROBLEMS_THRESHOLD, 100);

		pNbMaxProposals = getJdtUiIntPreference(
				Hunspell4EclipsePlugin.SPELLING_PROPOSALS_THRESHOLD, 6);

		// diagnose (activated if the "hunspell.log.on"
		// system property is defined).
		if (CLog.on())
			CLog.logOut(this, CLog.LIB_CONSTRUCTOR,
					"%s NbAcceptedProblems=[%d] NbMaxProposals=[%d]",
					CLog.LIB_INSTANCIATED, pNbAcceptedProblems, pNbMaxProposals);
	}

	/**
	 * @param region
	 *            the current region
	 * @param str
	 *            a wrong word
	 * @param distance
	 * @return an instance of SpellingProblem containing a array of proposal(s)
	 */
	protected SpellingProblem buildSpellingProblem(IRegion region, String str,
			int strLength, int distance) {

		SpellingProblem problem = null;
		final int inOffset = distance;

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

			// limits the number of proposals
			if (proposalList.size() >= pNbMaxProposals)
				break;
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

		int wNbFoundProblem = 0;

		// diagnose (activated if the "hunspell.log.on" system property is
		// defined).
		if (CLog.on())
			CLog.logOut(this, "checkInner", " nb regions=[%d]", regions.length);

		for (final IRegion region : regions) {

			// the user wants to stop ?
			if (monitor != null && monitor.isCanceled())
				return;

			wNbFoundProblem = checkOneRegion(document, region, collector,
					monitor, wNbFoundProblem);
			if (wNbFoundProblem < 0)
				return;
		}
	}

	/**
	 * @param document
	 * @param region
	 * @param collector
	 * @param monitor
	 * @param nbFoundProblem
	 *            the previous total number of found problems
	 * @return the total number of found problems . -1 for any reason to stop
	 *         the checking.
	 */
	protected int checkOneRegion(IDocument document, IRegion region,
			ISpellingProblemCollector collector, IProgressMonitor monitor,
			int nbFoundProblem) {

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
			return -1;
		}

		// diagnose (activated if the "hunspell.log.on" system
		// property is defined).
		if (CLog.on())
			CLog.logOut(this, "checkInner",
					"RegionOffset=[%d] RegionLength=[%d] docPartSize=[%d] ",
					wRegionOffset, wRegionLength, docPart.length());

		BreakIterator bi = BreakIterator.getWordInstance(getDictionary()
				.getLocale());

		bi.setText(docPart);

		int wWordIdx = 0;
		String wWord;
		int wWordDistance = 0;
		int wFirstIndex = 0;
		while (bi.next() != BreakIterator.DONE) {
			// retrieve the word
			wWord = docPart.substring(wFirstIndex, bi.current());
			wWordIdx++;
			// the distance of the current word in the region is its
			// firstIndex
			wWordDistance = wFirstIndex;

			// diagnose (activated if the "hunspell.log.on" system
			// property is defined).
			if (CLog.on())
				CLog.logOut(this, "checkInner",
						"WordIdx=[%d] Word=[%s] len=[%d]", wWordIdx, wWord,
						wWord.length());

			if (!checkOneWord(region, collector, wWord, wWordDistance)) {
				nbFoundProblem++;
				// limit reached, get out
				if (nbFoundProblem >= pNbAcceptedProblems)
					return -1;
			}
			wFirstIndex = bi.current();
		}
		return nbFoundProblem;
	}

	/**
	 * @param region
	 * @param collector
	 * @param wWord
	 * @param wWordDistance
	 * @return
	 */
	protected boolean checkOneWord(IRegion region,
			ISpellingProblemCollector collector, String wWord, int wWordDistance) {

		// if the word must be checked and id it is misspelled
		boolean wWrong = (checkRules(wWord) && getDictionary()
				.misspelled(wWord));
		// adds a new spelling problem in the collector
		if (wWrong)
			collector.accept(buildSpellingProblem(region, wWord,
					wWord.length(), wWordDistance));

		// check is true if not wrong
		return !wWrong;
	}

	/**
	 * @param str
	 * @return
	 */
	protected boolean checkRules(String str) {
		// forced default rules
		if (str.length() == 0 || str.matches("\\s+"))
			return false;

		// option rules
		// SingleLetter
		if (isSingleLetterIgnored() && (str.length() == 1))
			return false;

		// UpperCase
		if (isUpperCaseWordIgnored() && str.toUpperCase().equals(str))
			return false;

		// WWDigitsIgnored
		if (isWWDigitsIgnoredIgnored() && str.matches(".*[\\d]+.*"))
			return false;

		// WWMixedCaseIgnored
		if (isWWMixedCaseIgnored() && str.length() > 1) {

			// "Tomorrow" is not a mixed case word : only the first char is in
			// upper case.
			// "CMyClass" is a mixed case word

			String wTestedPartOfWord = str.substring(1);

			// The test rules :
			// a) abCd => ABCD ( => different) && abCd => abcd ( => different)
			// ===> Mixed case
			// b) abcd => ABCD ( => different) && abcd => abcd ( => equal)
			// ===> Not mixed case
			// c) ABCD => ABCD ( => equal) && ABCD => abcd ( => different)
			// ===> Not mixed case

			if (!wTestedPartOfWord.toUpperCase().equals(wTestedPartOfWord)
					&& !wTestedPartOfWord.toLowerCase().equals(
							wTestedPartOfWord)) {
				return false;
			}
		}
		// WWNonLetters
		if (isWWNonLettersIgnored() && str.matches("[^\\p{L}]+"))
			return false;

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
	 * @param aParamName
	 * @param aDefault
	 * @return
	 */
	private int getJdtUiIntPreference(String aParamName, int aDefault) {
		int wValue = preferenceStore.getInt(aParamName);
		return (wValue != 0) ? wValue : aDefault;
	}

	/**
	 * @return
	 */
	public int getNbAcceptedProblems() {
		return pNbAcceptedProblems;
	}

	/**
	 * @param opts
	 */
	protected int getOptions() {
		return opts;
	}

	/**
	 * @return
	 */
	public abstract boolean hasCompletionProposalCreator();

	/**
	 * @return
	 */
	public boolean isSingleLetterIgnored() {
		return (getOptions() & 1) == 1;
	}

	/**
	 * @return
	 */
	public boolean isUpperCaseWordIgnored() {
		return (getOptions() & 2) == 2;
	}

	/**
	 * @return
	 */
	public boolean isWWDigitsIgnoredIgnored() {
		return (getOptions() & 4) == 4;
	}

	/**
	 * @return
	 */
	public boolean isWWMixedCaseIgnored() {
		return (getOptions() & 8) == 8;
	}

	/**
	 * @return
	 */
	public boolean isWWNonLettersIgnored() {
		return (getOptions() & 16) == 16;
	}

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