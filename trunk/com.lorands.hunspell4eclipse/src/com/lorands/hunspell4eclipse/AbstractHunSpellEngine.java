/**
 *
 */
package com.lorands.hunspell4eclipse;

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

	private int opts;

	private Dictionary pEnglishDictionary;
	private final int pNbAcceptedProblems;
	private final int pNbMaxProposals;
	private final IPreferenceStore preferenceStore;
	private Dictionary pSelectedDictionary;

	/**
	 * 
	 */
	public AbstractHunSpellEngine() {
		this.preferenceStore = Hunspell4EclipsePlugin.getDefault()
				.getPreferenceStore();

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
	protected SpellingProblem buildSpellingProblem(IDocument document,
			IRegion region, Dictionary aDictionary, String str, int strLength,
			int distance) {

		SpellingProblem problem = null;
		final int inOffset = distance;

		// get suggestions using the Dictionary passed in the params
		final List<String> suggestList = aDictionary.suggest(str);

		final List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();

		// add one proposal per suggest
		for (final String suggest : suggestList) {
			proposalList
					.add(newProposal(document, suggest, inOffset, strLength));

			// limits the number of proposals
			if (proposalList.size() >= pNbMaxProposals)
				break;
		}
		int wNbProposal = proposalList.size();

		problem = newSpellingProblem(document, inOffset, strLength,
				String.format(PROBLEM_MESSAGE_FORMAT, str, wNbProposal,
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

		// BreakIterator bi = BreakIterator.getWordInstance(getDictionary()
		// .getLocale());
		// bi.setText(docPart);
		HunspellCheckIterator bi = new HunspellCheckIterator(docPart,
				wRegionOffset, getSelectedDictionary().getLocale());

		int wWordIdx = 0;
		String wWord;
		int wWordDistance = 0;
		while (bi.hasNext()) {
			// retrieve the word
			wWord = bi.next();
			wWordIdx++;
			// the distance of the current word in the region is its
			// firstIndex
			wWordDistance = bi.getBegin();

			// diagnose (activated if the "hunspell.log.on" system
			// property is defined).
			if (CLog.on())
				CLog.logOut(this, "checkInner",
						"WordIdx=[%d] Word=[%s] len=[%d]", wWordIdx, wWord,
						(wWord != null) ? wWord.length() : -1);

			if (!checkOneWord(document, region, collector, wWord, wWordDistance)) {
				nbFoundProblem++;
				// limit reached, get out
				if (nbFoundProblem >= pNbAcceptedProblems)
					return -1;
			}
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
	protected boolean checkOneWord(IDocument document, IRegion region,
			ISpellingProblemCollector collector, String wWord, int wWordDistance) {

		// hypothesis
		boolean wWrong = false;

		// if the word must be checked and if it is misspelled
		if (checkRules(wWord)) {
			wWrong = getSelectedDictionary().misspelled(wWord);

			if (wWrong) {
				if (hasEnglishDictionary()) {
					wWrong = getEnglishDictionary().misspelled(wWord);
					if (wWrong) {
						// adds spelling problem in the collector using the
						// selected
						// dictionary
						collector.accept(buildSpellingProblem(document, region,
								getSelectedDictionary(), wWord, wWord.length(),
								wWordDistance));
						// adds spelling problem in the collector using the
						// english dictionary
						collector.accept(buildSpellingProblem(document, region,
								getEnglishDictionary(), wWord, wWord.length(),
								wWordDistance));
					}

				} else {
					// adds spelling problem in the collector using the selected
					// dictionary
					collector.accept(buildSpellingProblem(document, region,
							getSelectedDictionary(), wWord, wWord.length(),
							wWordDistance));
				}
			}
		}

		// adds a new spelling problem in the collector

		// check is true if not wrong
		return !wWrong;
	}

	/**
	 * @param str
	 * @return
	 */
	protected boolean checkRules(String str) {
		// forced default rules
		if (str == null || str.length() == 0 || str.matches("\\s+"))
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
	 * @return
	 */
	public final Dictionary getEnglishDictionary() {
		return pEnglishDictionary;
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
	public final Dictionary getSelectedDictionary() {
		return pSelectedDictionary;
	}

	/**
	 * @return true if this engine has an english dictionry
	 */
	public final boolean hasEnglishDictionary() {
		return getEnglishDictionary() != null;
	}

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
	 * @param suggest
	 * @param strLength
	 * @param inOffset
	 * @return
	 */
	protected ICompletionProposal newProposal(IDocument document,
			String suggest, int inOffset, int strLength) {
		return new CompletionProposal(suggest, inOffset, strLength, strLength);

	}

	/**
	 * @param document
	 * @param offset
	 * @param length
	 * @param message
	 * @param proposals
	 * @return
	 */
	protected SpellingProblem newSpellingProblem(IDocument document,
			int offset, int length, String message,
			ICompletionProposal[] proposals) {
		return new HunspellingProblem(offset, length, message, proposals);
	}

	/**
	 * @param dictionary
	 */
	public final void setEnglishDictionary(Dictionary dictionary) {
		this.pEnglishDictionary = dictionary;
	}

	/**
	 * @param opts
	 */
	public void setOptions(int opts) {
		this.opts = opts;
	}

	/**
	 * @param dictionary
	 */
	public final void setSelectedDictionary(Dictionary dictionary) {
		this.pSelectedDictionary = dictionary;
	}
}