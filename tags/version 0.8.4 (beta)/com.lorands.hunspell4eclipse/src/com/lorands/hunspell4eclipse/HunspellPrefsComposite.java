package com.lorands.hunspell4eclipse;

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

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

	private Button pButton = null;
	private HunspellPrefsCompositeOptions pCompositeOptions;

	private String pDictionaryPath = "";

	private Label pLabelDictionary;

	private Label pLabelProblemsTreshold;
	private Label pLabelProposalsTreshold;

	final PixelConverter pPixelConverter = new PixelConverter(this);
	private int pProblemsThreshold = 0;
	private int pProposalsThreshold = 0;

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
	private String chooseFileDictionary() {
		final FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.dic", "*" });
		fd.setText("Select Dictionary");
		final String path = fd.open();
		return path;
	}

	/**
	 * @return
	 */
	public String getDictionaryPath() {
		return pDictionaryPath;
	}

	/**
	 * @return
	 */
	public int getProblemsThreshold() {
		return pProblemsThreshold;
	}

	/**
	 * @return
	 */
	public int getProposalsThreshold() {
		return pProposalsThreshold;
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
				setDictionaryPath(dict);
			}
		});

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
						try {
							pProblemsThreshold = Integer
									.valueOf(pTextProblemsThreshold.getText());
						} catch (Exception ex) {
							if (CLog.on())
								CLog.logErr(
										HunspellPrefsComposite.this,
										"modifyText",
										ex,
										"unable to convert ProblemsThreshold [%s]",
										pTextProblemsThreshold.getText());
						}
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
						try {
							pProposalsThreshold = Integer
									.valueOf(pTextProposalsThreshold.getText());
						} catch (Exception ex) {
							if (CLog.on())
								CLog.logErr(
										HunspellPrefsComposite.this,
										"modifyText",
										ex,
										"unable to convert ProposalsThreshold [%s]",
										pTextProblemsThreshold.getText());
						}
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
	 * @param dictPath
	 */
	public void setDictionaryPath(String dictPath) {
		this.pDictionaryPath = dictPath;
		this.pTextDictonaryPath.setText(dictPath);
	}

	/**
	 * @param th
	 */
	public void setProblemsThreshold(int th) {
		this.pProblemsThreshold = th;
		pTextProblemsThreshold.setText(Integer.toString(th));
	}

	/**
	 * @param th
	 */
	public void setProposalsThreshold(int th) {
		this.pProposalsThreshold = th;
		pTextProposalsThreshold.setText(Integer.toString(th));
	}

	/**
	 * @param opt
	 */
	void setSingleLetter(boolean opt) {
		pCompositeOptions.setSingleLetter(opt);
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
