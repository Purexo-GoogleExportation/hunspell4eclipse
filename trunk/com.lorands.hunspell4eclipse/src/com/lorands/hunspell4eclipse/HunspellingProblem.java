/*******************************************************************************
 * Copyright (c) 2011 lorands.com, L—r‡nd Somogyi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    L—r‡nd Somogyi (lorands.com) - initial API and implementation
 *******************************************************************************/
package com.lorands.hunspell4eclipse;

import java.util.Arrays;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

/**
 * SpelligProblem implementation for Hunspell. Just a minimal implementation,
 * nothing to see here.
 * 
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com *
 */
public class HunspellingProblem extends SpellingProblem {

	private final int length;
	private final String message;
	private final int offset;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.SpellingProblem#getProposals(org.eclipse
	 * .jface.text.quickassist.IQuickAssistInvocationContext)
	 * 
	 * @since 3.4
	 */
	@Override
	public ICompletionProposal[] getProposals(
			IQuickAssistInvocationContext context) {
		// @see extended class JavaHunspellingProblem
		return getProposals();
	}

	@Override
	public String toString() {
		return String.format(
				"%s:(length=[%d], offset=[%d], message=[%s], proposals=[%s])",
				getClass().getSimpleName(), length, offset, message,
				Arrays.toString(proposals));

	}
}
