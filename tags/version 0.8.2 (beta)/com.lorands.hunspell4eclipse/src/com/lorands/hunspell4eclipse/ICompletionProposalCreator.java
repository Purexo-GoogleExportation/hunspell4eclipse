/**
 * 
 */
package com.lorands.hunspell4eclipse;

import java.util.Map;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author Lorand Somogyi
 *
 */
public interface ICompletionProposalCreator {
	void setup(Map<String, ?> configuration);
	
	ICompletionProposal createProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition);
}
