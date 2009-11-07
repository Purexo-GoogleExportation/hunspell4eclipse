/**
 *
 */
package com.lorands.hunspell4eclipse;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.stibocatalog.hunspell.Hunspell;
import com.stibocatalog.hunspell.Hunspell.Dictionary;

/** Spell checking engine. 
 * 
 * @author Lorand Somogyi
 * 
 */
public class Engine implements ISpellingEngine {

	private Dictionary dictionary;

	private boolean initOk = false;

	/**
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * 
	 */
	public Engine() throws FileNotFoundException, UnsupportedEncodingException {
		final IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();

		final String dictPath = preferenceStore.getString(Activator.DICTPATH);
		if (dictPath == null || dictPath.isEmpty()) {
			MessageDialog
					.openError(
							Activator.getDefault().getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							"No dictionary selected",
							"Pleases select a dictionray in Window > Preferences > General > Editors > Text Editors > Spelling.");
			initOk = false;
		} else {
			final Hunspell hunspell = Hunspell.getInstance(); // try it without any param...
																// TODO: this might go to Activation.
			final String dictPerfix = dictPath.substring(0, dictPath.lastIndexOf('.'));
			dictionary = hunspell.getDictionary(dictPerfix); // "/usr/share/myspell/dicts/hu_HU"
			initOk = true;
		}
	}

	@Override
	public void check(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {
		if (!initOk) {
			return;
		}

		for (final IRegion region : regions) {

			try {
				final String docPart = document.get(region.getOffset(), region
						.getLength());

				// slice if needed
				final String[] strings = docPart.split("\\s");
				int distance = 0;
				for (final String str : strings) {
					final int strLength = str.length();
					if (strLength > 1) {
						if (dictionary.misspelled(str)) {
							final int inOffset = region.getOffset() + distance;

							// get sugg.
							final List<String> suggestList = dictionary
									.suggest(str);
							final List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
							for (final String suggest : suggestList) {
								proposalList
										.add(new CompletionProposal(suggest,
												inOffset, strLength, strLength));
							}

							final SpellingProblem problem = new HunspellingProblem(
									inOffset, strLength, "foo", proposalList
											.toArray(new CompletionProposal[0]));
							collector.accept(problem);
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

}
