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

/** Preferences extension for Hunspell4Eclipse.
 * 
 * 
 * @author Lorand Somogyi
 * 
 */
public final class HunspellPreferences implements ISpellingPreferenceBlock {
	private final IPreferenceStore preferenceStore;
	private HunspellPrefsComposite preferencesComp;

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
		preferenceStore.setValue(Activator.DICTPATH, preferencesComp
				.getDictPath());
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
