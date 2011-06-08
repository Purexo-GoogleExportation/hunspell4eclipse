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

import com.stibocatalog.hunspell.CLog;

/**
 * Preferences extension for Hunspell4Eclipse.
 * 
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 */
public final class HunspellPreferences implements ISpellingPreferenceBlock {

	final static int DEFAULT_ACCEPT_ENGLISH = 0;
	final static int DEFAULT_FULL_OPTIONS = 1 + 2 + 4 + 8 + 16; // 31
	final static int DEFAULT_PROBLEMS_THRESOLD = 100;
	final static int DEFAULT_PROPOSALS_THRESOLD = 10;

	private int pAcceptEnglishWords = DEFAULT_ACCEPT_ENGLISH;
	private String pDictionaryPath = "";
	private int pOptions = DEFAULT_FULL_OPTIONS;
	private int pProblemsThreshold = DEFAULT_PROBLEMS_THRESOLD;
	private int pProposalsThreshold = DEFAULT_PROPOSALS_THRESOLD;

	private HunspellPrefsComposite preferencesComp;

	private final IPreferenceStore preferenceStore;

	/**
	 * 
	 */
	public HunspellPreferences() {
		this.preferenceStore = Hunspell4EclipsePlugin.getDefault()
				.getPreferenceStore();
		readAll();
	}

	/**
	 * @return
	 */
	boolean acceptEngishWords() {
		return (pAcceptEnglishWords != 0);
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

		boolean wPerformOk = preferencesComp.canPerformOk();

		if (CLog.on())
			CLog.logOut(this, "canPerformOk", "canPerformOk=[%b]", wPerformOk);

		return wPerformOk;
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

		preferencesComp.setDictionaryPath(pDictionaryPath);
		preferencesComp.initAcceptEngishWords(pAcceptEnglishWords);
		preferencesComp.setProblemsThreshold(pProblemsThreshold);
		preferencesComp.setProposalsThreshold(pProposalsThreshold);
		intToOpts(pOptions);

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

	/**
	 * @return
	 */
	String getDictionaryPath() {
		return pDictionaryPath;
	}

	/**
	 * @return
	 */
	public boolean hasDictionaryPath() {
		return (pDictionaryPath != null && !pDictionaryPath.isEmpty());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#performOk()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.spelling.ISpellingPreferenceBlock#initialize
	 * (org.eclipse.ui.texteditor.spelling.IPreferenceStatusMonitor)
	 */
	@Override
	public void initialize(IPreferenceStatusMonitor statusMonitor) {
		storeAll();
		preferencesComp.setStatusMonitor(statusMonitor);
	}

	/**
	 * @param opt
	 */
	private void intToOpts(int opt) {
		preferencesComp.setSingleLetter((opt & 1) == 1);
		preferencesComp.setUpperCase((opt & 2) == 2);
		preferencesComp.setWWDigitsIgnored((opt & 4) == 4);
		preferencesComp.setWWMixedCaseIgnored((opt & 8) == 8);
		preferencesComp.setWWNonLetters((opt & 16) == 16);
	}

	/**
	 * @return
	 */
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
		pDictionaryPath = preferencesComp.getDictionaryPath();

		pAcceptEnglishWords = preferencesComp.getAcceptEnglishWords();
		pProblemsThreshold = preferencesComp.getProblemsThreshold();
		pProposalsThreshold = preferencesComp.getProposalsThreshold();
		pOptions = optsToInt();
		storeAll();
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
		final String loadDict = readStringValue(Hunspell4EclipsePlugin.SPELLING_DICTPATH);
		preferencesComp.setDictionaryPath(loadDict);
		// all checked
		intToOpts(DEFAULT_FULL_OPTIONS);
	}

	private void readAll() {
		pDictionaryPath = readStringValue(Hunspell4EclipsePlugin.SPELLING_DICTPATH);

		pAcceptEnglishWords = readIntValue(
				Hunspell4EclipsePlugin.SPELLING_ACCEPT_ENGLISH,
				DEFAULT_ACCEPT_ENGLISH);

		pProblemsThreshold = readIntValue(
				Hunspell4EclipsePlugin.SPELLING_PROBLEMS_THRESHOLD,
				DEFAULT_PROBLEMS_THRESOLD);
		pProposalsThreshold = readIntValue(
				Hunspell4EclipsePlugin.SPELLING_PROPOSALS_THRESHOLD,
				DEFAULT_PROPOSALS_THRESOLD);
		pOptions = readIntValue(Hunspell4EclipsePlugin.SPELLING_OPTIONS,
				DEFAULT_FULL_OPTIONS);
	}

	/**
	 * @param aId
	 * @return
	 */
	private int readIntValue(String aId, int aDefaultValue) {
		int wValue = aDefaultValue;
		boolean wExists = preferenceStore.contains(aId);
		if (wExists)
			wValue = preferenceStore.getInt(aId);

		// diagnose (activated if the "hunspell.log.on"
		// system property is defined).
		if (CLog.on())
			CLog.logOut(this, "readIntValue",
					"Id=[%s] Exists=[%b] Default=[%d] Value=[%d]", aId,
					wExists, aDefaultValue, wValue);
		return wValue;
	}

	/**
	 * @param aId
	 * @return the value or empty if the parameter doesn't exist
	 */
	private String readStringValue(String aId) {
		boolean wExists = preferenceStore.contains(aId);

		String wValue = preferenceStore.getString(aId);
		// diagnose (activated if the "hunspell.log.on"
		// system property is defined).
		if (CLog.on())
			CLog.logOut(this, "readStringValue",
					"Id=[%s] Exists=[%b]  Value=[%s]", aId, wExists, wValue);
		return wValue;
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

	/**
	 * 
	 */
	private void storeAll() {
		storeStringValue(Hunspell4EclipsePlugin.SPELLING_DICTPATH,
				pDictionaryPath);
		storeIntValue(Hunspell4EclipsePlugin.SPELLING_ACCEPT_ENGLISH,
				pAcceptEnglishWords);
		storeIntValue(Hunspell4EclipsePlugin.SPELLING_PROBLEMS_THRESHOLD,
				pProblemsThreshold);
		storeIntValue(Hunspell4EclipsePlugin.SPELLING_PROPOSALS_THRESHOLD,
				pProposalsThreshold);
		storeIntValue(Hunspell4EclipsePlugin.SPELLING_OPTIONS, pOptions);
	}

	/**
	 * @param aId
	 * @param aValue
	 */
	private void storeIntValue(String aId, int aValue) {
		preferenceStore.setValue(aId, aValue);

		// diagnose (activated if the "hunspell.log.on"
		// system property is defined).
		if (CLog.on())
			CLog.logOut(this, "storeIntValue", "Id=[%s] Value=[%d]", aId,
					aValue);
	}

	/**
	 * @param aId
	 * @param aValue
	 */
	private void storeStringValue(String aId, String aValue) {
		preferenceStore.setValue(aId, aValue);
		// diagnose (activated if the "hunspell.log.on"
		// system property is defined).
		if (CLog.on())
			CLog.logOut(this, "storeStringValue", "Id=[%s] Value=[%s]", aId,
					aValue);
	}

}
