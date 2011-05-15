package com.lorands.hunspell4eclipse.javaspell;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ui.texteditor.HippieProposalProcessor;

import com.stibocatalog.hunspell.CLog;

/**
 * 
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 12/05/2011 (dd/mm/yy) *
 */
public class JavaSpellCompletionProposalComputer implements
		IJavaCompletionProposalComputer {

	private final HippieProposalProcessor fProcessor = new HippieProposalProcessor();

	/**
	 * 
	 */
	public JavaSpellCompletionProposalComputer() {
		super();

		if (CLog.on())
			CLog.logOut(this, CLog.LIB_CONSTRUCTOR, CLog.LIB_INSTANCIATED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#
	 * computeCompletionProposals
	 * (org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public List<ICompletionProposal> computeCompletionProposals(
			ContentAssistInvocationContext context, IProgressMonitor arg1) {
		if (CLog.on())
			CLog.logOut(this, "computeCompletionProposals",
					"InvocationOffset=[%d]", context.getInvocationOffset());

		return Arrays.asList(fProcessor.computeCompletionProposals(
				context.getViewer(), context.getInvocationOffset()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#
	 * computeContextInformation
	 * (org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public List<IContextInformation> computeContextInformation(
			ContentAssistInvocationContext context, IProgressMonitor arg1) {

		if (CLog.on())
			CLog.logOut(this, "computeContextInformation",
					"InvocationOffset=[%d]", context.getInvocationOffset());

		return Arrays.asList(fProcessor.computeContextInformation(
				context.getViewer(), context.getInvocationOffset()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#getErrorMessage
	 * ()
	 */
	@Override
	public String getErrorMessage() {
		String wMessage = fProcessor.getErrorMessage();

		if (CLog.on())
			CLog.logOut(this, "getErrorMessage", "Message=[%s]", wMessage);

		return wMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#sessionEnded
	 * ()
	 */
	@Override
	public void sessionEnded() {
		if (CLog.on())
			CLog.logOut(this, "sessionEnded");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#sessionStarted
	 * ()
	 */
	@Override
	public void sessionStarted() {
		if (CLog.on())
			CLog.logOut(this, "sessionStarted");
	}

}
