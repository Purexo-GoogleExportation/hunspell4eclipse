package com.lorands.hunspell4eclipse.javaspell;

import org.eclipse.jdt.internal.ui.text.spelling.WordCorrectionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.TextInvocationContext;

import com.lorands.hunspell4eclipse.HunspellingProblem;
import com.stibocatalog.hunspell.CLog;

/**
 * @author ogattaz
 * 
 */
@SuppressWarnings("restriction")
public class JavaHunspellingProblem extends HunspellingProblem {

	/**
	 * @return
	 */
	public static String[] calcWordCorrectionArguments(IDocument document,
			int aOffset, int aLength) {

		String prefix = ""; //$NON-NLS-1$
		String postfix = ""; //$NON-NLS-1$
		String word;
		Boolean wIsStart = false;
		try {
			word = document.get(aOffset, aLength);
		} catch (BadLocationException e) {
			return null;
		}

		try {
			IRegion line = document.getLineInformationOfOffset(aOffset);
			wIsStart = line.getOffset() == aOffset;
			prefix = document.get(line.getOffset(), aOffset - line.getOffset());
			int postfixStart = aOffset + aLength;
			postfix = document.get(postfixStart,
					line.getOffset() + line.getLength() - postfixStart);

		} catch (BadLocationException exception) {
			// Do nothing
		}
		return new String[] { word, prefix, postfix, Boolean.toString(false),
				wIsStart ? Boolean.toString(true) : Boolean.toString(false) };
	}

	private final IDocument pDocument;

	/**
	 * @param offset
	 * @param length
	 * @param message
	 * @param proposals
	 */
	public JavaHunspellingProblem(int offset, int length, String message,
			ICompletionProposal[] proposals, IDocument document) {
		super(offset, length, message, proposals);

		pDocument = document;

		if (CLog.on())
			CLog.logOut(this, CLog.LIB_CONSTRUCTOR, CLog.LIB_INSTANCIATED);
	}

	/**
	 * @return
	 */
	public String[] getArguments() {
		return calcWordCorrectionArguments(pDocument, getOffset(), getLength());
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

		if (CLog.on())
			CLog.logOut(this, "getProposals",
					"Offset=[%s] Length=[%s] SourceViewer=[%s] ",
					(context != null) ? String.valueOf(context.getOffset())
							: "no context",
					(context != null) ? String.valueOf(context.getLength())
							: "no context", (context != null && context
							.getSourceViewer() != null) ? context
							.getSourceViewer().toString() : "no SourceViewer");

		// Look at the method public ICompletionProposal[]
		// getProposals(IQuickAssistInvocationContext context) in the class
		// org.eclipse.jdt.internal.ui.text.spelling.JavaSpellingProblem

		String[] arguments = getArguments();

		if (context == null)
			context = new TextInvocationContext(null, getOffset(), getLength());
		else
			context = new TextInvocationContext(context.getSourceViewer(),
					getOffset(), getLength());

		ICompletionProposal[] wProposals = super.getProposals();
		ICompletionProposal[] wResult = new ICompletionProposal[wProposals.length];
		int wI = 0;
		for (ICompletionProposal wProposal : wProposals) {

			wResult[wI] = new WordCorrectionProposal(
					wProposal.getDisplayString(), arguments, getOffset(),
					getLength(), context, 1);

		}

		return wResult;
	}
}
