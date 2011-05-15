package com.lorands.hunspell4eclipse;

/**
 * a tool to split a text in words
 * 
 * enhance the java.text.BreakIterator to be able to distinguish the words which
 * contain a non letter
 * 
 * eg. "fork.c", "MY_CONSTANT", "my-draft-spreed.xls"
 * 
 * 
 * @see org.eclipse.jdt.internal.ui.text.spelling.SpellCheckIterator
 * 
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 13/05/2011 (dd/mm/yy) *
 */
public class HunspellCheckIterator implements IHunspellCheckIterator {

	/**
	 * 
	 */
	public HunspellCheckIterator() {
		super();
	}

	@Override
	public int getBegin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIgnoreSingleLetters(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean startsSentence() {
		// TODO Auto-generated method stub
		return false;
	}

}
