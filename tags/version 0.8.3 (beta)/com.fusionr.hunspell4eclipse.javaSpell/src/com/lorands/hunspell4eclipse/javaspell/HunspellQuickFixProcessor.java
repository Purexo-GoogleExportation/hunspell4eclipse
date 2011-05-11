package com.lorands.hunspell4eclipse.javaspell;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.SpellingEngineDescriptor;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import com.stibocatalog.hunspell.CLog;
import com.stibocatalog.hunspell.CTools;

/**
 * This class try to build a list of quick fix with the list of the proposals
 * associated to the spelling problems.
 * 
 * Note:
 * 
 * When the jdt editor re-check each wong word to get proposals it does that
 * with the jdt spellengine which is not the current spellengine ! So, hunspell
 * could find spelling problems with a french dictionnary, and the standard
 * quickfix of the jdt editor shon english proposals...
 * 
 * @author ogattaz
 * 
 */
public class HunspellQuickFixProcessor implements IQuickFixProcessor {

	/**
	 * 
	 */
	public HunspellQuickFixProcessor() {
		super();

		if (CLog.on())
			CLog.logOut(this, CLog.LIB_CONSTRUCTOR, CLog.LIB_INSTANCIATED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.ui.text.java.IQuickFixProcessor#getCorrections(org.eclipse
	 * .jdt.ui.text.java.IInvocationContext,
	 * org.eclipse.jdt.ui.text.java.IProblemLocation[])
	 * 
	 * 
	 * @see
	 * http://grepcode.com/file/repository.grepcode.com/java/eclipse.org/3.6
	 * .1/org.eclipse.jdt/ui/3.6.1/org/eclipse/jdt/internal/ui/text/spelling/
	 * WordQuickFixProcessor.java#WordQuickFixProcessor
	 */
	@Override
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {

		IPreferenceStore wPreferenceStore = Activator.getDefault()
				.getPreferenceStore();

		final int threshold = wPreferenceStore
				.getInt(PreferenceConstants.SPELLING_PROPOSAL_THRESHOLD);

		SpellingService wSpeller = new SpellingService(wPreferenceStore);

		SpellingEngineDescriptor wSpellingEngineDescriptor = wSpeller
				.getActiveSpellingEngineDescriptor(wPreferenceStore);

		if (CLog.on())
			CLog.logOut(
					this,
					"getCorrections",
					"SpellingEngineId=[%s] SpellingEngineLabel=[%s] threshold=[%d]",
					wSpellingEngineDescriptor.getId(),
					wSpellingEngineDescriptor.getLabel(), threshold);

		// int size = 0;
		// List proposals = null;
		String[] arguments = null;

		IProblemLocation location = null;
		// RankedWordProposal proposal = null;
		IJavaCompletionProposal[] result = null;

		final ISpellingEngine engine = wSpellingEngineDescriptor.createEngine();

		for (int index = 0; index < locations.length; index++) {

			location = locations[index];

			arguments = location.getProblemArguments();

			if (CLog.on())
				CLog.logOut(this, "getCorrections",
						"ProblemId=[%s] arguments=[%s]",
						location.getProblemId(),
						CTools.arrayToString(arguments, ","));
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.ui.text.java.IQuickFixProcessor#hasCorrections(org.eclipse
	 * .jdt.core.ICompilationUnit, int)
	 */
	@Override
	public boolean hasCorrections(ICompilationUnit aCompilationUnit, int id) {

		if (CLog.on())
			CLog.logOut(this, "getCorrections", "CompilationUnit=[%s] id=[%d]",
					aCompilationUnit.getElementName(), id);

		return true;

	}

}
