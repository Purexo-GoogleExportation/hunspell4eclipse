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

import java.util.Map;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 */
public interface ICompletionProposalCreator {
	/**
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param cursorPosition
	 * @return
	 */
	ICompletionProposal createProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition);

	/**
	 * @param configuration
	 */
	void setup(Map<String, ?> configuration);
}
