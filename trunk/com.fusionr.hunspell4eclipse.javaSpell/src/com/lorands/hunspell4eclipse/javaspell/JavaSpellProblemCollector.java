package com.lorands.hunspell4eclipse.javaspell;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

/**
 * Stores all encountered problems while testing words spelling
 * 
 * @author OThomas Calmant < thomas dot almant at isandlatech dot com >
 * 
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 12/05/2011 (dd/mm/yy)
 */
public class JavaSpellProblemCollector implements ISpellingProblemCollector {

	/** Problems list */
	private final List<SpellingProblem> pProblemsList = new ArrayList<SpellingProblem>();

	@Override
	public void accept(final SpellingProblem aProblem) {
		pProblemsList.add(aProblem);
	}

	@Override
	public void beginCollecting() {
		// Do nothing
	}

	@Override
	public void endCollecting() {
		// Do nothing
	}

	/**
	 * Retrieves the list of all encountered spelling problems.
	 * 
	 * The result can't be null.
	 * 
	 * @return The list of all problems found
	 */
	public List<SpellingProblem> getProblems() {
		return pProblemsList;
	}
}
