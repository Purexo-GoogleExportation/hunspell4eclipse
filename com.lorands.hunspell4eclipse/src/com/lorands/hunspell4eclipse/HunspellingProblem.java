/**
 *
 */
package com.lorands.hunspell4eclipse;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

/** SpelligProblem implementation for Hunspell.  Just a minimal implementation, nothing to see here.
 * @author Lorand Somogyi
 * 
 */
public final class HunspellingProblem extends SpellingProblem {

	private final int offset;
	private final int length;
	private final String message;
	private final ICompletionProposal[] proposals;

	/**
	 *
	 */
	public HunspellingProblem(int offset, int length, String message,
			ICompletionProposal[] proposals) {
		this.offset = offset;
		this.length = length;
		this.message = message;
		this.proposals = proposals;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.spelling.SpellingProblem#getLength()
	 */
	@Override
	public int getLength() {
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.spelling.SpellingProblem#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.spelling.SpellingProblem#getOffset()
	 */
	@Override
	public int getOffset() {
		return offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.spelling.SpellingProblem#getProposals()
	 */
	@Override
	public ICompletionProposal[] getProposals() {
		return proposals;
	}

}
