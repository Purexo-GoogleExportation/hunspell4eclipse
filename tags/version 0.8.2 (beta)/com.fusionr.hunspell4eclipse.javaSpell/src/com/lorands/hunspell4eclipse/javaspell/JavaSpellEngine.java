/**
 * 
 */
package com.lorands.hunspell4eclipse.javaspell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

/**
 * @author Lorand Somogyi
 * 
 */
public final class JavaSpellEngine extends AbstractHunSpellEngine {

	private static final JavaProposalCreator JAVA_PROPOSAL_CREATOR = new JavaProposalCreator();
	private static final IRegion[] EMPTY_REGION_ARRAY = new IRegion[0];

	public JavaSpellEngine() {
	}

	@Override
	public void check(IDocument document, IRegion[] regions,
			SpellingContext context, ISpellingProblemCollector collector,
			IProgressMonitor monitor) {

//		ASTParser parser = ASTParser.newParser(AST.JLS3);
//		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
//		parser.setSource(document.get().toCharArray());
//		ASTNode astNode = parser.createAST(null);
		
		// astNode.
		try {
			List<IRegion> regionsList = new ArrayList<IRegion>();
			for (int i = 0; i < regions.length; i++) {
				IRegion region = regions[i];
				ITypedRegion[] partitions = TextUtilities.computePartitioning(
						document, 
						IJavaPartitions.JAVA_PARTITIONING, 
						region.getOffset(), 
						region.getLength(), 
						false);
				for (int index = 0; index < partitions.length; index++) {
					if (monitor != null && monitor.isCanceled())
						return;

					ITypedRegion partition = partitions[index];
					if (!partition.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
						// checker.execute(new SpellCheckIterator(document,
						// partition, locale));
						
						IRegion innerRegion = new Region(region.getOffset() + partition.getOffset(), partition.getLength());
						if( partition.getType().equals(IJavaPartitions.JAVA_DOC)
								|| partition.getType().equals(IJavaPartitions.JAVA_MULTI_LINE_COMMENT) 
								|| partition.getType().equals(IJavaPartitions.JAVA_SINGLE_LINE_COMMENT)
								) {//javadocs, comments
							regionsList.add(innerRegion);
						} else {
							regionsList.add(innerRegion);
						}
					}
				}
			}
			checkInner(document, regionsList.toArray(EMPTY_REGION_ARRAY), 
					context, collector, monitor);

		} catch (BadLocationException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
					"Spelling Service provided offset/length that points out of the document", e));
		} finally {

		}

	}
	
	@Override
	public ICompletionProposalCreator getCompletionProposalCreator() {
		return JAVA_PROPOSAL_CREATOR;
	}	
	
	public static class JavaProposalCreator implements ICompletionProposalCreator{
		
		private static final int relevance = 1;
		
		@Override
		public ICompletionProposal createProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition) {
			return new JavaCompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition, relevance);
		}

		@Override
		public void setup(Map<String, ?> configuration) {
			//nothing
		}
	}
	
	public static class JavaCompletionProposal implements IJavaCompletionProposal {
		private CompletionProposal inner;
		
		private int relevance = 1;
		
		public JavaCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition) {
			inner = new CompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition);
		}
		
		public JavaCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, int relevance) {
			inner = new CompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition);
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



}
