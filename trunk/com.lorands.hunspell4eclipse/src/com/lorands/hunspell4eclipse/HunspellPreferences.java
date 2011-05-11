/**
 * 
 */
package com.lorands.hunspell4eclipse;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.spelling.IPreferenceStatusMonitor;
import org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock;

/**
 * Preferences extension for Hunspell4Eclipse.
 * 
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 */
public final class HunspellPreferences implements ISpellingPreferenceBlock {
	private HunspellPrefsComposite preferencesComp;
	private final IPreferenceStore preferenceStore;

	/**
	 * 
	 */
	public HunspellPreferences() {
		this.preferenceStore = Activator.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#canPerformOk
	 * ()
	 */
	@Override
	public boolean canPerformOk() {
		if (preferencesComp.getDictPath() != null
				&& !preferencesComp.getDictPath().isEmpty()) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#createControl
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(Composite parent) {
		this.preferencesComp = new HunspellPrefsComposite(parent, SWT.NULL);
		final String dictPath = preferenceStore.getString(Activator.DICTPATH);
		if (dictPath != null) {
			preferencesComp.setDictPath(dictPath);
		}

		final int threshold = preferenceStore.getInt(Activator.THRESHOLD);
		if (threshold != 0) {
			preferencesComp.setThreshold(threshold);
		}

		if (preferenceStore.contains(Activator.DEFAULT_OPTIONS)) {
			intToOpts(preferenceStore.getInt(Activator.DEFAULT_OPTIONS));
		}

		return preferencesComp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#dispose()
	 */
	@Override
	public void dispose() {
		preferencesComp.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#initialize
	 * (org.eclipse.ui.texteditor.spelling.IPreferenceStatusMonitor)
	 */
	@Override
	public void initialize(IPreferenceStatusMonitor statusMonitor) {
		// none
	}

	private void intToOpts(int opt) {
		preferencesComp.setSingleLetter((opt & 1) == 1);
		preferencesComp.setUpperCase((opt & 2) == 2);
		preferencesComp.setWWDigitsIgnored((opt & 4) == 4);
		preferencesComp.setWWMixedCaseIgnored((opt & 8) == 8);
		preferencesComp.setWWNonLetters((opt & 16) == 16);
	}

	private int optsToInt() {
		return (preferencesComp.isSingleLetter() ? 1 : 0)
				+ (preferencesComp.isUpperCase() ? 2 : 0)
				+ (preferencesComp.isWWDigitsIgnored() ? 4 : 0)
				+ (preferencesComp.isWWMixedCaseIgnored() ? 8 : 0)
				+ (preferencesComp.isWWNonLetters() ? 16 : 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#performDefaults
	 * ()
	 */
	@Override
	public void performDefaults() {
		// none
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#performOk()
	 */
	@Override
	public void performOk() {
		preferenceStore.setValue(Activator.DICTPATH,
				preferencesComp.getDictPath());
		preferenceStore.setValue(Activator.THRESHOLD,
				preferencesComp.getThreshold());
		preferenceStore.setValue(Activator.DEFAULT_OPTIONS, optsToInt());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#performRevert
	 * ()
	 */
	@Override
	public void performRevert() {
		final String loadDict = preferenceStore.getString(Activator.DICTPATH);
		preferencesComp.setDictPath(loadDict);
		intToOpts(32 - 1); // all checked
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#setEnabled
	 * (boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		// none
	}

}
