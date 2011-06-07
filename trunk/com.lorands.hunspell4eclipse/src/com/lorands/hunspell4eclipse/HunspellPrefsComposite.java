package com.lorands.hunspell4eclipse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.texteditor.spelling.IPreferenceStatusMonitor;

import com.stibocatalog.hunspell.CLog;

/**
 * 
 */

/**
 * Hunspell preferences UI part.
 * 
 * @see HunspellPreferences
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 */
public class HunspellPrefsComposite extends Composite {

	private int pAcceptEnglishWords = 0;
	private Button pButton = null;
	private HunspellPrefsCompositeOptions pCompositeOptions;
	private String pDictionaryPath = "";
	private Button pEngishCheckBox;
	private Label pLabelAcceptEnglishWords;
	private Label pLabelDictionary;
	private Label pLabelProblemsTreshold;
	private Label pLabelProposalsTreshold;
	final PixelConverter pPixelConverter = new PixelConverter(this);
	private int pProblemsThreshold = 0;
	private int pProposalsThreshold = 0;
	private IPreferenceStatusMonitor pStatusMonitor;
	private Text pTextDictonaryPath;
	private Text pTextProblemsThreshold;
	private Text pTextProposalsThreshold;

	/**
	 * @param parent
	 * @param style
	 */
	public HunspellPrefsComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	/**
	 * @return
	 */
	boolean acceptEnglishWords() {
		return getAcceptEnglishWords() != 0;
	}

	/**
	 * @return
	 */
	boolean canPerformOk() {
		String wDictionaryPath = getDictionaryPath();

		boolean wPerformOk = (wDictionaryPath != null && !wDictionaryPath
				.isEmpty());

		if (CLog.on())
			CLog.logOut(this, "canPerformOk",
					"wDictionaryPath=[%s] canPerformOk=[%b]", wDictionaryPath,
					wPerformOk);

		return wPerformOk;
	}

	/**
	 * @return
	 */
	private String chooseFileDictionary() {
		final FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.dic" });
		fd.setText("Select an hunspell dictionary file ( *.dic).");
		final String path = fd.open();
		return path;
	}

	/**
	 * 
	 */
	void desableAcceptEngishWords() {
		pEngishCheckBox.setSelection(false);
		pEngishCheckBox.setEnabled(false);
		setAcceptEngishWords(HunspellPreferences.DEFAULT_ACCEPT_ENGLISH);

		pLabelAcceptEnglishWords
				.setText("No english dictionary available near the selected one.");
	}

	/**
 * 
 */
	private void doAfterChange() {
		if (hasStatusMonitor()) {
			if (!hasDictionaryPath())
				pStatusMonitor.statusChanged(new Status(IStatus.ERROR,
						Hunspell4EclipsePlugin.PLUGIN_ID,
						"No dictionary file selected."));
			else if (!hasProblemsThreshold())
				pStatusMonitor.statusChanged(new Status(IStatus.ERROR,
						Hunspell4EclipsePlugin.PLUGIN_ID,
						"Not an integer  value for the problems threshold."));
			else if (!hasProposalsThreshold())
				pStatusMonitor.statusChanged(new Status(IStatus.ERROR,
						Hunspell4EclipsePlugin.PLUGIN_ID,
						"Not an integer value for the proposals threshold."));
			else
				pStatusMonitor.statusChanged(Status.OK_STATUS);
		}
	}

	void enableAcceptEngishWords() {
		pEngishCheckBox.setEnabled(true);
		pLabelAcceptEnglishWords.setText("");
	}

	/**
	 * @return
	 */
	int getAcceptEnglishWords() {
		return pAcceptEnglishWords;
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
	int getProblemsThreshold() {
		return pProblemsThreshold;
	}

	/**
	 * @return
	 */
	int getProposalsThreshold() {
		return pProposalsThreshold;
	}

	/**
	 * @return
	 */
	boolean hasDictionaryPath() {
		return (pDictionaryPath != null && !pDictionaryPath.isEmpty());
	}

	/**
	 * @return
	 */
	boolean hasProblemsThreshold() {
		return pProblemsThreshold > 0;
	}

	/**
	 * @return
	 */
	boolean hasProposalsThreshold() {
		return pProposalsThreshold > 0;
	}

	/**
	 * @return
	 */
	boolean hasStatusMonitor() {
		return pStatusMonitor != null;
	}

	void initAcceptEngishWords(int aAcceptEnglishWords) {
		if (pEngishCheckBox.getEnabled()) {
			setAcceptEngishWords(aAcceptEnglishWords);

			pEngishCheckBox.setSelection(aAcceptEnglishWords != 0);
		}
	}

	/**
	 * 
	 */
	private void initGroupDictPath() {
		// three columns. Different size.
		GridLayout gridLayoutDictPath = new GridLayout(3, false);

		Group wGroupDictionary = new Group(this, SWT.NONE);
		wGroupDictionary.setText("Dictionary");
		wGroupDictionary.setLayout(gridLayoutDictPath);
		wGroupDictionary.setLayoutData(newGridDataGroup());

		pLabelDictionary = new Label(wGroupDictionary, SWT.NONE);
		pLabelDictionary.setText("Dictionary file path (*.dic file)");
		pLabelDictionary.setLayoutData(newGridDataLabel(50));

		pTextDictonaryPath = new Text(wGroupDictionary, SWT.BORDER);
		pTextDictonaryPath.setText(getDictionaryPath());
		pTextDictonaryPath.setLayoutData(newGridDataField(60));
		pTextDictonaryPath.setEditable(false);

		pButton = new Button(wGroupDictionary, SWT.NONE);
		pButton.setText("Browse...");
		pButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				final String dict = chooseFileDictionary();
				setDictionaryPath((dict != null) ? dict : "");

			}
		});

		pEngishCheckBox = new Button(wGroupDictionary, SWT.CHECK);
		pEngishCheckBox.setText("accept also english words");
		pEngishCheckBox.setSelection(true);
		pEngishCheckBox
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						setAcceptEngishWords(pEngishCheckBox.getSelection() ? 1
								: 0);
					}

				});

		pLabelAcceptEnglishWords = new Label(wGroupDictionary, SWT.NONE);
		pLabelAcceptEnglishWords.setLayoutData(newGridDataLabel(70));

	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void initGroupOptionseOptions() {

		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = false;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.horizontalAlignment = GridData.FILL;

		// one column
		GridLayout gridLayoutOptions = new GridLayout(1, true);

		pCompositeOptions = new HunspellPrefsCompositeOptions(this, SWT.NONE);
		pCompositeOptions.setLayout(gridLayoutOptions);
		pCompositeOptions.setLayoutData(gridData2);
	}

	/**
	 * 
	 */
	private void initGroupThresolds() {
		// two columns. Different sizes.
		GridLayout gridLayoutThresolds = new GridLayout(2, false);

		Group wGroupThresold = new Group(this, SWT.FILL);
		wGroupThresold.setText("Thresolds");
		wGroupThresold.setLayoutData(newGridDataGroup());
		wGroupThresold.setLayout(gridLayoutThresolds);

		pLabelProblemsTreshold = new Label(wGroupThresold, SWT.NONE);
		pLabelProblemsTreshold
				.setText("Maximum number of problems reported per file:");
		pLabelProblemsTreshold.setLayoutData(newGridDataLabel(50));

		pTextProblemsThreshold = new Text(wGroupThresold, SWT.BORDER);
		pTextProblemsThreshold.setText(String.valueOf(pProblemsThreshold));
		pTextProblemsThreshold.setLayoutData(newGridDataField(10));
		pTextProblemsThreshold
				.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					@Override
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {

						setProblemsThreshold(pTextProblemsThreshold.getText());

					}
				});

		pLabelProposalsTreshold = new Label(wGroupThresold, SWT.NONE);
		pLabelProposalsTreshold
				.setText("Maximum number of correction proposals:");
		pLabelProposalsTreshold.setLayoutData(newGridDataLabel(50));

		pTextProposalsThreshold = new Text(wGroupThresold, SWT.BORDER);
		pTextProposalsThreshold.setText(String.valueOf(pProposalsThreshold));
		pTextProposalsThreshold.setLayoutData(newGridDataField(10));
		pTextProposalsThreshold
				.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					@Override
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {

						setProposalsThreshold(pTextProposalsThreshold.getText());

					}
				});
	}

	/**
	 * 
	 */
	private void initialize() {
		// setSize(new Point(286, 261));

		// one column
		setLayout(new GridLayout(1, true));

		initGroupDictPath();

		initGroupThresolds();

		initGroupOptionseOptions();

		if (CLog.on())
			CLog.logOut(this, "initialize", "OK");
	}

	/**
	 * @return
	 */
	boolean isSingleLetter() {
		return pCompositeOptions.isSingleLetter();
	}

	/**
	 * @return
	 */
	boolean isUpperCase() {
		return pCompositeOptions.isUpperCase();
	}

	/**
	 * @return
	 */
	boolean isWWDigitsIgnored() {
		return pCompositeOptions.isWWDigitsIgnored();
	}

	/**
	 * @return
	 */
	boolean isWWMixedCaseIgnored() {
		return pCompositeOptions.isWWMixedCaseIgnored();
	}

	/**
	 * @return
	 */
	boolean isWWNonLetters() {
		return pCompositeOptions.isWWNonLetters();
	}

	/**
	 * @return
	 */
	private GridData newGridData(int aNbCharacterWidth) {
		GridData wGridData = new GridData();
		wGridData.verticalAlignment = GridData.CENTER;
		wGridData.horizontalAlignment = GridData.FILL;
		if (aNbCharacterWidth > 0)
			wGridData.widthHint = pPixelConverter
					.convertWidthInCharsToPixels(40);
		return wGridData;
	}

	/**
	 * @param aNbCharacterWidth
	 * @return
	 */
	private GridData newGridDataField(int aNbCharacterWidth) {
		GridData wGridData = newGridData(aNbCharacterWidth);
		return wGridData;
	}

	/**
	 * @return
	 */
	private GridData newGridDataGroup() {
		GridData wGridData = newGridData(0);
		return wGridData;
	}

	private GridData newGridDataLabel(int aNbCharacterWidth) {
		GridData wGridData = newGridData(aNbCharacterWidth);
		return wGridData;
	}

	/**
	 * @param aAcceptEnglishWords
	 */
	void setAcceptEngishWords(int aAcceptEnglishWords) {
		pAcceptEnglishWords = aAcceptEnglishWords;
	}

	/**
	 * @param dictPath
	 */
	void setDictionaryPath(String dictPath) {
		this.pDictionaryPath = dictPath;
		this.pTextDictonaryPath.setText(dictPath);

		if (!hasDictionaryPath()
				|| !Engine.hasEnglishDictionaryInSameDir(pDictionaryPath))
			desableAcceptEngishWords();
		else
			enableAcceptEngishWords();

		doAfterChange();
	}

	/**
	 * @param th
	 */
	void setProblemsThreshold(int th) {
		this.pProblemsThreshold = th;
		pTextProblemsThreshold.setText(Integer.toString(th));
	}

	/**
	 * @param th
	 */
	void setProblemsThreshold(String th) {
		try {
			pProblemsThreshold = Integer.valueOf(th);

		} catch (Exception ex) {
			if (CLog.on())
				CLog.logErr(HunspellPrefsComposite.this,
						"setProblemsThreshold", ex,
						"unable to convert ProblemsThreshold [%s]",
						pTextProblemsThreshold.getText());

			pProblemsThreshold = -1;
		}

		doAfterChange();
	}

	/**
	 * @param th
	 */
	void setProposalsThreshold(int th) {
		this.pProposalsThreshold = th;
		pTextProposalsThreshold.setText(Integer.toString(th));
	}

	/**
	 * @param th
	 */
	void setProposalsThreshold(String th) {
		try {
			pProposalsThreshold = Integer.valueOf(pTextProposalsThreshold
					.getText());
		} catch (Exception ex) {
			if (CLog.on())
				CLog.logErr(HunspellPrefsComposite.this, "modifyText", ex,
						"unable to convert ProposalsThreshold [%s]",
						pTextProblemsThreshold.getText());
			pProposalsThreshold = -1;
		}

		doAfterChange();
	}

	/**
	 * @param opt
	 */
	void setSingleLetter(boolean opt) {
		pCompositeOptions.setSingleLetter(opt);
	}

	/**
	 * @param aStatusMonitor
	 */
	void setStatusMonitor(IPreferenceStatusMonitor aStatusMonitor) {
		pStatusMonitor = aStatusMonitor;
		if (CLog.on())
			CLog.logOut(this, "setStatusMonitor", "StatusMonitor=[%s]",
					pStatusMonitor.toString());
	}

	/**
	 * @param opt
	 */
	void setUpperCase(boolean opt) {
		pCompositeOptions.setUpperCase(opt);
	}

	/**
	 * @param opt
	 */
	void setWWDigitsIgnored(boolean opt) {
		pCompositeOptions.setWWDigitsIgnored(opt);
	}

	/**
	 * @param opt
	 */
	void setWWMixedCaseIgnored(boolean opt) {
		pCompositeOptions.setWWMixedCaseIgnored(opt);
	}

	/**
	 * @param opt
	 */
	void setWWNonLetters(boolean opt) {
		pCompositeOptions.setWWNonLetters(opt);
	}

}
