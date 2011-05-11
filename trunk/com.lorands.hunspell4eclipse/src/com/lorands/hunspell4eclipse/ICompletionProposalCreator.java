/**
 * 
 */
package com.lorands.hunspell4eclipse;

import java.util.Map;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 */
public interface ICompletionProposalCreator {
	ICompletionProposal createProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition);

	void setup(Map<String, ?> configuration);
}
