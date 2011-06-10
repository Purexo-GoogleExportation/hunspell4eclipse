/**
 * 
 */
package com.lorands.hunspell4eclipse.javaspell;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.text.spelling.IJavaDocTagConstants;
import org.eclipse.jdt.internal.ui.text.spelling.SpellCheckIterator;
import org.eclipse.jdt.internal.ui.text.spelling.WordCorrectionProposal;
import org.eclipse.jdt.internal.ui.text.spelling.engine.ISpellCheckIterator;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

import com.lorands.hunspell4eclipse.HunspellEngineBase;
import com.lorands.hunspell4eclipse.ICompletionProposalCreator;
import com.stibocatalog.hunspell.CLog;

/**
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 12/05/2011 (dd/mm/yy)
 * 
 */
@SuppressWarnings({ "restriction" })
public final class JavaHunspellEngine extends HunspellEngineBase {

	/**
	 * @author ogattaz
	 * 
	 */
	class HunspellJavaSpellCheckIterator extends SpellCheckIterator {

		/**
		 * @param document
		 * @param region
		 * @param locale
		 */
		public HunspellJavaSpellCheckIterator(IDocument document,
				IRegion region, Locale locale) {
			super(document, region, locale);
		}

		/**
		 * @param aToken
		 * @return
		 */
		private boolean isJavaDocToken(String aToken) {
			if (aToken == null)
				return false;

			boolean wResult = false;
			if (aToken.length() > 2
					&& aToken.charAt(0) == IJavaDocTagConstants.JAVADOC_TAG_PREFIX) {

				if (super.isToken(aToken,
						IJavaDocTagConstants.JAVADOC_ROOT_TAGS)
						|| super.isToken(aToken,
								IJavaDocTagConstants.JAVADOC_PARAM_TAGS)
						|| super.isToken(aToken,
								IJavaDocTagConstants.JAVADOC_LINK_TAGS)
						|| super.isToken(aToken,
								IJavaDocTagConstants.JAVADOC_REFERENCE_TAGS)) {
					wResult = true;
				}
			}
			// diagnose (activated if the "hunspell.log.on"
			// system
			// property is defined).
			if (CLog.on())
				CLog.logOut(this, "isJavaDocWords",
						"[%s] is javadoc token [%b]", String.valueOf(aToken),
						wResult);

			return wResult;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jdt.internal.ui.text.spelling.SpellCheckIterator#next()
		 */
		@Override
		public Object next() {

			String token = (String) super.next();

			while (isJavaDocToken(token))
				token = (String) super.next();

			return token;
		}
	}

	/**
	 * @author Lorand Somogyi
	 * 
	 */
	public static class JavaCompletionProposal implements
			IJavaCompletionProposal {
		private final CompletionProposal inner;

		private int relevance = 1;

		public JavaCompletionProposal(String replacementString,
				int replacementOffset, int replacementLength, int cursorPosition) {
			inner = new CompletionProposal(replacementString,
					replacementOffset, replacementLength, cursorPosition);
		}

		public JavaCompletionProposal(String replacementString,
				int replacementOffset, int replacementLength,
				int cursorPosition, int relevance) {
			inner = new CompletionProposal(replacementString,
					replacementOffset, replacementLength, cursorPosition);
			this.relevance = relevance;
		}

		@Override
		public void apply(IDocument document) {
			inner.apply(document);
		}

		@Override
		public String getAdditionalProposalInfo() {
			return inner.getAdditionalProposalInfo();
		}

		@Override
		public IContextInformation getContextInformation() {
			return inner.getContextInformation();
		}

		@Override
		public String getDisplayString() {
			return inner.getDisplayString();
		}

		@Override
		public Image getImage() {
			return inner.getImage();
		}

		@Override
		public int getRelevance() {
			return relevance;
		}

		@Override
		public org.eclipse.swt.graphics.Point getSelection(IDocument document) {
			return inner.getSelection(document);
		}

	}

	/**
	 * @author Lorand Somogyi
	 * 
	 */
	public static class JavaProposalCreator implements
			ICompletionProposalCreator {

		private static final int relevance = 1;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.lorands.hunspell4eclipse.ICompletionProposalCreator#createProposal
		 * (java.lang.String, int, int, int)
		 */
		@Override
		public ICompletionProposal createProposal(String replacementString,
				int replacementOffset, int replacementLength, int cursorPosition) {

			return new JavaCompletionProposal(replacementString,
					replacementOffset, replacementLength, cursorPosition,
					relevance);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.lorands.hunspell4eclipse.ICompletionProposalCreator#setup(java
		 * .util.Map)
		 */
		@Override
		public void setup(Map<String, ?> configuration) {
			// nothing
		}
	}

	private static final IRegion[] EMPTY_REGION_ARRAY = new IRegion[0];

	/**
	 * 
	 */
	public JavaHunspellEngine() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingEngine#check(org.eclipse.
	 * jface.text.IDocument, org.eclipse.jface.text.IRegion[],
	 * org.eclipse.ui.texteditor.spelling.SpellingContext,
	 * org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void check(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {

		// ASTParser parser = ASTParser.newParser(AST.JLS3);
		// parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		// parser.setSource(document.get().toCharArray());
		// ASTNode astNode = parser.createAST(null);

		// astNode.

		List<IRegion> regionsList = new ArrayList<IRegion>();
		for (int i = 0; i < regions.length; i++) {
			IRegion region = regions[i];

			// retrieve the array of partitions of the region
			ITypedRegion[] partitions = null;
			int wRegionOffset = region.getOffset();
			int wRegionLength = region.getLength();
			try {
				partitions = TextUtilities.computePartitioning(document,
						IJavaPartitions.JAVA_PARTITIONING, wRegionOffset,
						wRegionLength, false);
			} catch (BadLocationException e) {
				CLog.logErr(
						this,
						"check",
						e,
						"Spelling Service provided offset/length that points out of the document RegionOffset=[%d] RegionLength=[%d]",
						wRegionOffset, wRegionLength);

				// ends the method
				return;
			}

			for (int index = 0; index < partitions.length; index++) {
				if (monitor != null && monitor.isCanceled())
					return;

				ITypedRegion partition = partitions[index];
				if (!partition.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {

					IRegion innerRegion = new Region(region.getOffset()
							+ partition.getOffset(), partition.getLength());

					if (partition.getType().equals(IJavaPartitions.JAVA_DOC)
							|| partition.getType().equals(
									IJavaPartitions.JAVA_MULTI_LINE_COMMENT)
							|| partition.getType().equals(
									IJavaPartitions.JAVA_SINGLE_LINE_COMMENT)) {

						// diagnose (activated if the "hunspell.log.on"
						// system
						// property is defined).
						if (CLog.on())
							CLog.logOut(this, "check",
									"Adds one partitionType=[%s]",
									partition.getType());

						// adds javadocs, comments
						regionsList.add(innerRegion);
					} else {
						// regionsList.add(innerRegion);
					}
				}
			}
		}

		// call the check implemented in the abstract AbstractHunSpellEngine
		super.checkInner(document, regionsList.toArray(EMPTY_REGION_ARRAY),
				context, collector, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lorands.hunspell4eclipse.AbstractHunSpellEngine#checkOneRegion(org
	 * .eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion,
	 * org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector,
	 * org.eclipse.core.runtime.IProgressMonitor, int)
	 */
	@Override
	protected int checkOneRegion(IDocument document, IRegion region,
			ISpellingProblemCollector collector, IProgressMonitor monitor,
			int aNbFoundProblem) {

		if (CLog.on())
			CLog.logOut(this, "checkOneRegion",
					"region: ofset=[%d] length=[%d]", region.getOffset(),
					region.getLength());

		// reuse the internal class SpellCheckIterator of the jdt to have the
		// same word splitting rules
		ISpellCheckIterator wSPIterator = new HunspellJavaSpellCheckIterator(
				document, region, getSelectedDictionary().getLocale());

		// set one of the option
		wSPIterator.setIgnoreSingleLetters(isSingleLetterIgnored());

		int wI = 0;
		while (wSPIterator.hasNext()) {
			Object wToken = wSPIterator.next();
			if (wToken != null) {
				String wWord = String.valueOf(wToken);
				int wDistance = wSPIterator.getBegin();
				wI++;
				if (CLog.on())
					CLog.logOut(this, "checkOneRegion", "word(%d)=[%s][%d]",
							wI, wWord, wDistance);

				if (!super.checkOneWord(document, region, collector, wWord,
						wDistance)) {
					aNbFoundProblem++;
					// limit reached, get out
					if (aNbFoundProblem >= getNbAcceptedProblems())
						return -1;
				}
			}
		}
		return aNbFoundProblem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lorands.hunspell4eclipse.AbstractHunSpellEngine#buildIProposal(java
	 * .lang.String, int, int)
	 */
	@Override
	protected ICompletionProposal newProposal(IDocument document,
			String suggest, int inOffset, int strLength) {

		String[] wArguments = JavaHunspellingProblem
				.calcWordCorrectionArguments(document, inOffset, strLength);

		IQuickAssistInvocationContext wContext = new TextInvocationContext(
				null, inOffset, strLength);

		WordCorrectionProposal wProposal = new WordCorrectionProposal(suggest,
				wArguments, inOffset, strLength, wContext, 1);

		// diagnose (activated if the "hunspell.log.on"
		// system
		// property is defined).
		if (CLog.on())
			CLog.logOut(this, "newProposal", "kind=[%s] suggest=[%s]",
					wProposal.getClass().getSimpleName(), suggest);

		return wProposal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lorands.hunspell4eclipse.AbstractHunSpellEngine#newSpellingProblem
	 * (int, int, java.lang.String,
	 * org.eclipse.jface.text.contentassist.ICompletionProposal[])
	 */
	@Override
	protected SpellingProblem newSpellingProblem(IDocument document,
			int offset, int length, String message,
			ICompletionProposal[] proposals) {
		return new JavaHunspellingProblem(offset, length, message, proposals,
				document);
	}

}
