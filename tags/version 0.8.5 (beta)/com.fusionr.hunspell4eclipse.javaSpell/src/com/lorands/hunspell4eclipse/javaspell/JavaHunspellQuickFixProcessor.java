package com.lorands.hunspell4eclipse.javaspell;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.text.spelling.JavaSpellingReconcileStrategy;
import org.eclipse.jdt.internal.ui.text.spelling.WordCorrectionProposal;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingEngineDescriptor;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import com.lorands.hunspell4eclipse.Hunspell4EclipsePlugin;
import com.stibocatalog.hunspell.CLog;
import com.stibocatalog.hunspell.CTools;

/**
 * This class build a list of WordCorrectionProposal
 * 
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 12/05/2011 (dd/mm/yy)
 * 
 */
@SuppressWarnings("restriction")
public class JavaHunspellQuickFixProcessor implements IQuickFixProcessor {

	/**
	 * 
	 */
	public JavaHunspellQuickFixProcessor() {
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
	 */
	@Override
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {

		// hypothesis
		IJavaCompletionProposal[] result = null;

		// retrieve the Hunspell engine
		final ISpellingEngine wHunspellEngine = retrieveHunspellEngine();

		// Prepare the spelling context
		// String contentTypeString = IContentTypeManager.CT_TEXT;
		IContentType contentType = Platform.getContentTypeManager()
				.getContentType(IContentTypeManager.CT_TEXT);

		SpellingContext wSpellingContext = new SpellingContext();
		wSpellingContext.setContentType(contentType);

		// retrieve the current document
		final IDocument wCurrentDocument = retreiveDocument(context);

		for (IProblemLocation location : locations) {

			String[] arguments = location.getProblemArguments();

			if (CLog.on())
				CLog.logOut(this, "getCorrections",
						"ProblemId=[%s] arguments=[%d|%s]",
						location.getProblemId(), arguments.length,
						CTools.arrayToString(arguments, ","));

			JavaHunspellProblemCollector collector = new JavaHunspellProblemCollector();

			// Define the region of the wrong word using the location
			IRegion wWrongRegion = new Region(location.getOffset(),
					location.getLength());

			wHunspellEngine.check(wCurrentDocument,
					new IRegion[] { wWrongRegion }, wSpellingContext,
					collector, null);

			List<SpellingProblem> wProblems = collector.getProblems();

			IQuickAssistInvocationContext wQuickAssistInvocationContext = new TextInvocationContext(
					retreiveSourceViewer(context), location.getOffset(),
					location.getLength());

			for (SpellingProblem wProblem : wProblems) {

				ICompletionProposal[] wProposals = wProblem.getProposals();

				result = new IJavaCompletionProposal[wProposals.length];

				int wI = 0;
				for (ICompletionProposal wProposal : wProposals) {

					result[wI] = new WordCorrectionProposal(
							wProposal.getDisplayString(), arguments,
							location.getOffset(), location.getLength(),
							wQuickAssistInvocationContext, 1);
					if (CLog.on())
						CLog.logOut(this, "getCorrections",
								"WordProposal(%d)=[%s]", wI,
								wProposal.getDisplayString());
					wI++;

				}// for proposals
			}// for problems
		}// for locations

		return result;
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
			CLog.logOut(this, "hasCorrections", "CompilationUnit=[%s] id=[%d]",
					aCompilationUnit.getElementName(), id);

		return id == JavaSpellingReconcileStrategy.SPELLING_PROBLEM_ID;

	}

	/**
	 * @param context
	 * @return
	 */
	private IDocument retreiveDocument(IInvocationContext context) {
		return retreiveSourceViewer(context).getDocument();
	}

	/**
	 * @param context
	 * @return
	 */
	private ISourceViewer retreiveSourceViewer(IInvocationContext context) {
		return ((IQuickAssistInvocationContext) context).getSourceViewer();

	}

	/**
	 * @return
	 * @throws CoreException
	 */
	private ISpellingEngine retrieveHunspellEngine() throws CoreException {
		SpellingService wSpellingService = new SpellingService(
				Hunspell4EclipseJavaspellPlugin.getDefault()
						.getPreferenceStore());

		SpellingEngineDescriptor wHunspellSpellingEngineDescriptor = null;

		SpellingEngineDescriptor[] wDefaultSPEDs = wSpellingService
				.getSpellingEngineDescriptors();

		if (CLog.on()) {
			int wI = 0;
			for (SpellingEngineDescriptor wDefaultSPED : wDefaultSPEDs) {
				if (Hunspell4EclipsePlugin.isMyDescriptor(wDefaultSPED))
					wHunspellSpellingEngineDescriptor = wDefaultSPED;
				CLog.logOut(
						this,
						"getCorrections",
						"DefaultSpellingEngineDescriptor(%d)=[%s] isHunspell=[%b] SpellingEngineLabel=[%s] isDefault=[%b]",
						wI, wDefaultSPED.getId(),
						Hunspell4EclipsePlugin.isMyDescriptor(wDefaultSPED),
						wDefaultSPED.getLabel(), wDefaultSPED.isDefault());
				wI++;
			}
		}

		return wHunspellSpellingEngineDescriptor.createEngine();
	}

}
