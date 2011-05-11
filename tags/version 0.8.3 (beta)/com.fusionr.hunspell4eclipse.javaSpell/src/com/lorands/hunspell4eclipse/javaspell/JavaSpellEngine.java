/**
 * 
 */
package com.lorands.hunspell4eclipse.javaspell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

import com.lorands.hunspell4eclipse.AbstractHunSpellEngine;
import com.lorands.hunspell4eclipse.ICompletionProposalCreator;
import com.stibocatalog.hunspell.CLog;

/**
 * @author Lorand Somogyi
 * 
 */
public final class JavaSpellEngine extends AbstractHunSpellEngine {

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
		public Point getSelection(IDocument document) {
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

		@Override
		public ICompletionProposal createProposal(String replacementString,
				int replacementOffset, int replacementLength, int cursorPosition) {
			return new JavaCompletionProposal(replacementString,
					replacementOffset, replacementLength, cursorPosition,
					relevance);
		}

		@Override
		public void setup(Map<String, ?> configuration) {
			// nothing
		}
	}

	private static final IRegion[] EMPTY_REGION_ARRAY = new IRegion[0];

	private static final JavaProposalCreator JAVA_PROPOSAL_CREATOR = new JavaProposalCreator();

	/**
	 * 
	 */
	public JavaSpellEngine() {
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
						regionsList.add(innerRegion);
					}
				}
			}
		}

		// call the check implemented in the abstract AbstractHunSpellEngine
		checkInner(document, regionsList.toArray(EMPTY_REGION_ARRAY), context,
				collector, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lorands.hunspell4eclipse.AbstractHunSpellEngine#
	 * getCompletionProposalCreator()
	 */
	@Override
	public ICompletionProposalCreator getCompletionProposalCreator() {
		return JAVA_PROPOSAL_CREATOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lorands.hunspell4eclipse.AbstractHunSpellEngine#
	 * hasCompletionProposalCreator()
	 */
	@Override
	public boolean hasCompletionProposalCreator() {
		return true;
	}

}
