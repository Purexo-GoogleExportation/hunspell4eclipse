/**
 *
 */
package com.lorands.hunspell4eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

import com.stibocatalog.hunspell.Hunspell.Dictionary;

/**
 * @author Lorand Somogyi
 *
 */
public abstract class AbstractHunSpellEngine implements ISpellingEngine {

	private static final ICompletionProposal[] PROPOSALS_EMPTY_ARRAY = new ICompletionProposal[0];
	private Dictionary dictionary;
	private int opts;
	private int threshold;
	private IPreferenceStore preferenceStore;

	public AbstractHunSpellEngine() {
		this.preferenceStore = Activator.getDefault().getPreferenceStore();
		
		this.threshold = preferenceStore.getInt(Activator.THRESHOLD); //TODO this part should be in some kind of utility
		if( threshold == 0 ) {
			threshold = 100;  
		}
	}

	public final Dictionary getDictionary() {
		return dictionary;
	}

	public final void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	protected final void checkInner(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {

		ICompletionProposalCreator completionProposalCreator = getCompletionProposalCreator();

		int cntr = 0;
		
		for (final IRegion region : regions) {
			if (monitor != null && monitor.isCanceled()) {
				return;
			}

			try {
				final String docPart = document.get(region.getOffset(), region.getLength());

				// slice if needed
				final String[] strings = docPart.split("[^\\p{L}]");
				int distance = 0;
				for (final String str : strings) {
					final int strLength = str.length();
					if( checkRules(str) ) {
						if (getDictionary().misspelled(str)) {
							final int inOffset = region.getOffset() + distance;

							// get sugg.
							final List<String> suggestList = getDictionary().suggest(str);
							final List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
							for (final String suggest : suggestList) {
								ICompletionProposal proposal;
								if( completionProposalCreator != null ) {
									proposal = completionProposalCreator.createProposal(suggest, inOffset, strLength, strLength);
								} else {
									proposal = new CompletionProposal(suggest,
											inOffset, strLength, strLength);
								}
								proposalList.add(proposal);
							}

							final SpellingProblem problem = new HunspellingProblem(
									inOffset, strLength, "Spell proposal",
										proposalList.toArray(PROPOSALS_EMPTY_ARRAY));
							
							if( cntr >= threshold ) { //limit reached, get out
								return ;
							}
							collector.accept(problem);
							cntr++;
						}
					}
					distance += strLength + 1; // +1 for whitespace
				}

			} catch (final BadLocationException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Spelling Service provided offset/length that points out of the document", e));
			}
		}
	}

	private boolean checkRules(String str) {
		//forced default rules
		if( str.length() == 0 || str.matches("\\s+")) {
			return false;
		}

		//option rules
		//SingleLetter
		if((opts & 1) == 1) {
			if( str.length() == 1 ) {
				return false;
			}
		}

		//UpperCase
		if((opts & 2) == 2) {
			if( str.toUpperCase().equals(str)) {
				return false;
			}
		}

		//WWDigitsIgnored
		if((opts & 4) == 4) {
			if( str.matches(".*[\\d]+.*")) {
				return false;
			}
		}

		//WWMixedCaseIgnored
		if((opts & 8) == 8) {
			if( str.toUpperCase().equals(str.toLowerCase())) {
				return false;
			}
		}
		//WWNonLetters
		if((opts & 16) == 16) {
			if( str.matches("[^\\p{L}]+")) {
				return false;
			}
		}
		return true;
	}

	public void setOptions(int opts) {
		this.opts = opts;

	}

	/** Get completion proposal provider or null if none.
	 * @return
	 */
	public abstract ICompletionProposalCreator getCompletionProposalCreator();

}
