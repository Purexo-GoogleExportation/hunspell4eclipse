/**
 * 
 */
package com.lorands.hunspell4eclipse.javaspell;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

import com.lorands.hunspell4eclipse.AbstractHunSpellEngine;

/**
 * @author Lorand Somogyi
 * 
 */
public final class JavaSpellEngine extends AbstractHunSpellEngine {

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
						if( partition.getType().equals(IJavaPartitions.JAVA_DOC)
								|| partition.getType().equals(IJavaPartitions.JAVA_MULTI_LINE_COMMENT) 
								|| partition.getType().equals(IJavaPartitions.JAVA_SINGLE_LINE_COMMENT)
								) {//javadocs, comments
							checkInner(document, new IRegion[] {new Region(partition.getOffset(), partition.getLength())}, 
									context, collector, monitor);
						} else {
							checkInner(document, new IRegion[] {new Region(partition.getOffset(), partition.getLength())}, 
									context, collector, monitor);
						}
					}
				}
			}
		} catch (BadLocationException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
					"Spelling Service provided offset/length that points out of the document", e));
		} finally {

		}

	}

}
