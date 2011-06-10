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

import java.util.Iterator;

/**
 * 
 * Thanks to the jdt team (IBM Corporation )
 * 
 * @see org.eclipse.jdt.internal.ui.text.spelling.engine.ISpellCheckIterator;
 * 
 * @author IBM Corporation - jdt team
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 11/05/2011 (dd/mm/yy)
 */
public interface IHunspellCheckIterator extends Iterator<String>,
		IHunspellCheckConstants {

	/**
	 * Returns the begin index (inclusive) of the current word.
	 * 
	 * @return The begin index of the current word
	 */
	public int getBegin();

	/**
	 * Returns the end index (exclusive) of the current word.
	 * 
	 * @return The end index of the current word
	 */
	public int getEnd();

	/**
	 * Tells whether to ignore single letters from being checked.
	 * 
	 * @param state
	 *            <code>true</code> if single letters should be ignored
	 */
	public void setIgnoreSingleLetters(boolean state);

}
