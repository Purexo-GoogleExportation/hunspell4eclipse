/*******************************************************************************
 * Copyright (c) 2011 isandlaTech, Olivier Gattaz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olivier Gattaz (isandlaTech) - initial API and implementation
 *******************************************************************************/
package com.lorands.hunspell4eclipse;

//import java.text.BreakIterator;
import java.util.Locale;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.ibm.icu.text.BreakIterator;
import com.stibocatalog.hunspell.CLog;

/**
 * a tool to split a text in words
 * 
 * adopts the same architecture as that used by the JDT
 * 
 * Enhances the base BreakIterator to be able to distinguish the urls or the
 * html tags, or the html entities.
 * 
 * eg. "http://www.eclipse.org" , "<body>" "&eacute;"
 * 
 * Thanks to the jdt team (IBM Corporation )
 * 
 * @see org.eclipse.jdt.internal.ui.text.spelling.SpellCheckIterator
 * 
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 13/05/2011 (dd/mm/yy) *
 */
public class HunspellCheckIterator implements IHunspellCheckIterator {
	/**
	 * The token that denotes whitespace.
	 * 
	 * @since 3.6
	 */
	private static final int WHITE_SPACE_TOKEN = -1;

	/**
	 * @param aLocale
	 * @return
	 */
	private static com.ibm.icu.text.BreakIterator newBreakIterator(
			final Locale aLocale) {

		BreakIterator wBI = BreakIterator.getWordInstance(aLocale);

		if (CLog.on())
			CLog.logOut(HunspellCheckIterator.class, "newBreakIterator",
					"class=[%s] rules=[%s]", wBI.getClass().getName(),
					wBI.toString());
		return wBI;
	}

	/**
	 * @return
	 */
	private static String retrieveContent(IDocument document, IRegion region) {
		try {
			return document.get(region.getOffset(), region.getLength());

		} catch (Exception exception) {
			return ""; //$NON-NLS-1$
		}
	}

	private boolean fIsIgnoringSingleLetters;

	/** The content of the region */
	protected final String pContent;

	/** The last returned token */
	protected String pLastReturnedToken = null;

	/** The predecessor break */
	private int pOffsetCurrent = 0;

	/** The previous break */
	protected int pOffsetPrevious = 0;

	/** The offset of the region */
	protected final int pOffsetRegion;

	/** The successor break */
	protected int pOffsetSuccessor = 0;

	/** The previous token */
	protected String pPreviousToken = null;

	/**
	 * The word break iterator.
	 * 
	 * http://icu-project.org/apiref/icu4j/com/ibm/icu/text/
	 * RuleBasedBreakIterator.html
	 * 
	 * http://userguide.icu-project.org/boundaryanalysis
	 * 
	 * */
	private final BreakIterator pWordBreakIterator;

	/**
	 * Creates a new spell check iterator.
	 * 
	 * @param document
	 *            the document containing the specified partition
	 * @param region
	 *            the region to spell check
	 * @param locale
	 *            the locale to use for spell checking
	 */
	public HunspellCheckIterator(IDocument document, IRegion region,
			Locale locale) {
		this(document, region, locale, newBreakIterator(locale));
	}

	/**
	 * Creates a new spell check iterator.
	 * 
	 * @param document
	 *            the document containing the specified partition
	 * @param region
	 *            the region to spell check
	 * @param breakIterator
	 *            the break-iterator
	 */
	public HunspellCheckIterator(IDocument document, IRegion region,
			Locale locale, BreakIterator breakIterator) {
		this(retrieveContent(document, region), region.getOffset(),
				breakIterator);
	}

	/**
	 * @param aRegionContent
	 *            the content of the region
	 * @param aRegionOffset
	 *            the offset of the region in the document
	 * @param breakIterator
	 *            the break-iterator
	 */
	public HunspellCheckIterator(String aRegionContent, int aRegionOffset,
			BreakIterator breakIterator) {
		super();
		pContent = aRegionContent;
		pOffsetRegion = aRegionOffset;
		pWordBreakIterator = breakIterator;
		pWordBreakIterator.setText(pContent);
		pOffsetCurrent = pWordBreakIterator.first();
		pOffsetSuccessor = pWordBreakIterator.next();
		if (CLog.on())
			CLog.logOut(
					this,
					CLog.LIB_CONSTRUCTOR,
					"%s OffsetRegion=[%d] OffsetCurrent=[%d] OffsetSuccessor=[%d]",
					CLog.LIB_INSTANCIATED, aRegionOffset, pOffsetCurrent,
					pOffsetSuccessor);
	}

	/**
	 * @param aRegionContent
	 *            the content of the region
	 * @param aRegionOffset
	 *            the offset of the region in the document
	 * @param locale
	 *            the locale to use for spell checking
	 */
	public HunspellCheckIterator(String aRegionContent, int aRegionOffset,
			Locale locale) {
		this(aRegionContent, aRegionOffset, newBreakIterator(locale));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lorands.hunspell4eclipse.IHunspellCheckIterator#getBegin()
	 */
	@Override
	public final int getBegin() {
		return pOffsetPrevious + pOffsetRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lorands.hunspell4eclipse.IHunspellCheckIterator#getEnd()
	 */
	@Override
	public final int getEnd() {
		return pOffsetSuccessor + pOffsetRegion - 1;
	}

	protected String getLastReturnedToken() {
		return this.pLastReturnedToken;
	}

	/**
	 * @return
	 */
	private String getTokenCurrent() {
		return pContent.substring(pOffsetPrevious, pOffsetCurrent);
	}

	/**
	 * @return
	 */
	protected String getTokenNext() {
		return (pOffsetSuccessor != BreakIterator.DONE) ? pContent.substring(
				pOffsetCurrent, pOffsetSuccessor) : null;
	}

	/**
	 * @return
	 */
	protected String getTokenPrevious() {
		return pPreviousToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public final boolean hasNext() {
		return pOffsetSuccessor != BreakIterator.DONE;
	}

	/**
	 * Does the specified token consist of at least one letter and digits only?
	 * 
	 * @param begin
	 *            the begin index
	 * @param end
	 *            the end index
	 * @return <code>true</code> iff the token consists of digits and at least
	 *         one letter only, <code>false</code> otherwise
	 */
	protected final boolean isAlphaNumeric(final int begin, final int end) {

		char character = 0;

		boolean letter = false;
		for (int index = begin; index < end; index++) {

			character = pContent.charAt(index);
			if (Character.isLetter(character))
				letter = true;

			if (!Character.isLetterOrDigit(character))
				return false;
		}
		return letter;
	}

	/**
	 * Is the current token a single letter token surrounded by non-whitespace
	 * characters?
	 * 
	 * @param begin
	 *            the begin index
	 * @return <code>true</code> iff the token is a single letter token,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isSingleLetter(final int begin) {
		if (!Character.isLetter(pContent.charAt(begin)))
			return false;

		if (begin > 0 && !Character.isWhitespace(pContent.charAt(begin - 1)))
			return false;

		if (begin < pContent.length() - 1
				&& !Character.isWhitespace(pContent.charAt(begin + 1)))
			return false;

		return true;
	}

	/**
	 * Checks the given token against the given tags?
	 * 
	 * @param token
	 *            the token to check
	 * @param tags
	 *            the tags to check
	 * @return <code>true</code> if the token is in the given array
	 */
	protected final boolean isToken(final String token, final String[] tags) {

		if (token != null) {

			for (String wTag : tags) {

				if (token.equals(wTag)) {
					if (CLog.on())
						CLog.logOut(this, "isToken", "tag=[%s] true", wTag);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks the last token against the given tags?
	 * 
	 * @param tags
	 *            the tags to check
	 * @return <code>true</code> if the last token is in the given array
	 */
	protected final boolean isToken(final String[] tags) {
		return isToken(pLastReturnedToken, tags);
	}

	/**
	 * Does the specified token look like an URL?
	 * 
	 * @param begin
	 *            the begin index
	 * @return <code>true</code> iff this token look like an URL,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isUrlToken(final int begin) {

		for (String wPrefix : URL_PREFIXES) {
			if (pContent.startsWith(wPrefix, begin)) {
				if (CLog.on())
					CLog.logOut(this, "isUrlToken", "true");
				return true;
			}
		}
		return false;
	}

	/**
	 * @param begin
	 * @param end
	 * @return
	 */
	protected final boolean isWhitespace(final int begin, final int end) {

		for (int index = begin; index < end; index++) {

			if (!Character.isWhitespace(pContent.charAt(index)))
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public String next() {
		String token = nextToken();
		while (token == null && pOffsetSuccessor != BreakIterator.DONE)
			token = nextToken();
		pLastReturnedToken = token;

		return token;
	}

	/**
	 * Advances the end index to the next word break.
	 */
	protected final void nextBreak() {
		pOffsetCurrent = pOffsetSuccessor;
		pOffsetSuccessor = pWordBreakIterator.next();
	}

	/**
	 * @return
	 */
	protected String nextToken() {
		String token = null;

		pPreviousToken = getTokenCurrent();

		pOffsetPrevious = pOffsetCurrent;

		nextBreak();

		if (pOffsetCurrent - pOffsetPrevious > 0) {

			if (CLog.on()) {
				String wLRT = getLastReturnedToken();
				if (wLRT == null)
					wLRT = "null";
				String wWord0 = getTokenPrevious();
				if (wWord0 == null)
					wWord0 = "null";
				String wWord1 = getTokenCurrent();
				if ("\n".equals(wWord1))
					wWord1 = "\\n";
				String wWord2 = getTokenNext();
				if (wWord2 == null)
					wWord2 = "null";
				else if ("\n".equals(wWord2))
					wWord2 = "\\n";
				CLog.logOut(
						this,
						"nextToken",
						"OffsetPrevious=[%d] OffsetCurrent=[%d] OffsetSuccessor=[%d] Toklast=[%s] TokPrev=[%s]  TokCur=[%s] TokNext=[%s]",
						pOffsetPrevious, pOffsetCurrent, pOffsetSuccessor,
						wLRT, wWord0, wWord1, wWord2);
			}

			// s'il y a un suivant , que l'on a "<" ˆ l'offset courant et que le
			// caractre suivant est une lettre ou un "/"
			if (pOffsetSuccessor != BreakIterator.DONE
					&& pContent.charAt(pOffsetPrevious) == HTML_TAG_PREFIX
					&& (Character.isLetter(pContent.charAt(pOffsetCurrent)) || pContent
							.charAt(pOffsetCurrent) == '/')) {

				// si on a "</" ˆ l'offset courant => prochain mot
				if (pContent.startsWith(HTML_CLOSE_PREFIX, pOffsetPrevious))
					nextBreak();

				boolean wIsHtmlTag = isToken(getTokenNext(), HTML_GENERAL_TAGS);

				// s'il y a un suivant et que l'on a ">" ˆ l'offset du token
				// suivant
				if (pOffsetSuccessor != BreakIterator.DONE
						&& pContent.charAt(pOffsetSuccessor) == HTML_TAG_POSTFIX) {

					// prochain mot
					nextBreak();
				}

				// prochain mot
				nextBreak();

				if (!wIsHtmlTag)
					token = getTokenCurrent();

			}

			// s'il y a un suivant , que l'on a "&" ˆ l'offset courant et que le
			// caractre suivant est une lettre
			else if (pOffsetSuccessor != BreakIterator.DONE
					&& pContent.charAt(pOffsetPrevious) == HTML_ENTITY_START
					&& (Character.isLetter(pContent.charAt(pOffsetCurrent)))) {

				// s'il y a un suivant et que l'on a ";" ˆ l'offset du token
				// suivant
				if (pOffsetSuccessor != BreakIterator.DONE
						&& pContent.charAt(pOffsetSuccessor) == HTML_ENTITY_END) {
					// prochain mot
					nextBreak();
					// prochain mot
					nextBreak();

					boolean wIsHtmlEntity = isToken(getTokenCurrent(),
							HTML_ENTITY_CODES);

					// si le token
					if (!wIsHtmlEntity) {
						token = getTokenCurrent();
					}
				} else {
					token = getTokenCurrent();
				}

			} else if (!isWhitespace(pOffsetPrevious, pOffsetCurrent)
					&& isAlphaNumeric(pOffsetPrevious, pOffsetCurrent)) {

				if (isUrlToken(pOffsetPrevious))
					skipTokens(pOffsetPrevious, WHITE_SPACE_TOKEN);
				else if (pOffsetCurrent - pOffsetPrevious > 1
						|| isSingleLetter(pOffsetPrevious)
						&& !fIsIgnoringSingleLetters) {
					token = getTokenCurrent();
				}
			}

		}
		return token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lorands.hunspell4eclipse.IHunspellCheckIterator#setIgnoreSingleLetters
	 * (boolean)
	 */
	@Override
	public void setIgnoreSingleLetters(boolean state) {
		fIsIgnoringSingleLetters = state;
	}

	/**
	 * Skip the tokens until the stop character is reached.
	 * 
	 * @param begin
	 *            the begin index
	 * @param stop
	 *            the stop character
	 */
	protected final void skipTokens(final int begin, final int stop) {
		final boolean isStoppingOnWhiteSpace = stop == WHITE_SPACE_TOKEN;
		int end = begin;
		while (end < pContent.length()) {
			char ch = pContent.charAt(end);
			if (ch == stop || isStoppingOnWhiteSpace
					&& Character.isWhitespace(ch))
				break;
			end++;
		}

		if (end < pContent.length()) {
			pOffsetSuccessor = end;
			pOffsetCurrent = pOffsetSuccessor;

			pOffsetSuccessor = pWordBreakIterator.following(pOffsetSuccessor);
		} else
			pOffsetSuccessor = BreakIterator.DONE;
	}

}
