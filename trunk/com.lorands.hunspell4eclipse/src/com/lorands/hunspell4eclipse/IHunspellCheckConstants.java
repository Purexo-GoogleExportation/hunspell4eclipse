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

/**
 * @author ogattaz
 * 
 */
public interface IHunspellCheckConstants {

	/** Html tag close prefix */
	public static final String HTML_CLOSE_PREFIX = "</";

	/** Html entity characters */
	public static final char[] HTML_ENTITY_CHARACTERS = new char[] { '<', '>',
			' ', '&', '^', '~', '\"' };

	/** Html entity codes */
	public static final String[] HTML_ENTITY_CODES = new String[] { "&lt;",
			"&agrave;", "&gt;", "&nbsp;", "&amp;", "&circ;", "&eacute;",
			"&tilde;", "&quot;" };

	/**
	 * Html entity end.
	 */
	public static final char HTML_ENTITY_END = ';';
	/**
	 * Html entity start.
	 */
	public static final char HTML_ENTITY_START = '&';

	/** Html general tags */
	public static final String[] HTML_GENERAL_TAGS = new String[] {
			"a", "b", "blockquote", "body", "br", "code", "dd", "dl", "dt", "em", "head", "hr", "html", "h1", "h2", "h3", "h4", "h5", "h6", "i", "li", "link", "nl", "ol", "p", "pre", "q", "strong", "tbody", "td", "th", "tr", "tt", "ul" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$ //$NON-NLS-22$ //$NON-NLS-23$ //$NON-NLS-24$ //$NON-NLS-25$ //$NON-NLS-26$ //$NON-NLS-27$ //$NON-NLS-28$ //$NON-NLS-29$ //$NON-NLS-30$

	/** Html tag postfix */
	public static final char HTML_TAG_POSTFIX = '>';

	/** Html tag prefix */
	public static final char HTML_TAG_PREFIX = '<';

	public static final String[] URL_PREFIXES = new String[] { "http://",
			"https://", "www.", "ftp://", "ftps://", "news://", "mailto://" };

}
