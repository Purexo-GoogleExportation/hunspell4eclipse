package com.lorands.hunspell4eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Point;

/**
 * 
 */

/** Hunspell preferences UI part.
 * 
 * @see HunspellPreferences
 * @author Lorand Somogyi
 * 
 */
public class HunspellPrefsComposite extends Composite {

	private Label lDictionary = null;
	private Label lTreshold = null;
	private Text text = null;
	private Text tThreshold = null;
	private Button button = null;
	private String dictPath = null; // @jve:decl-index=0:
	private DefaultPreferenceOptions composite = null;
	private int threshold;

	public HunspellPrefsComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		this.setSize(new Point(286, 261));
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.verticalAlignment = GridData.CENTER;
		final GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = GridData.CENTER;
		gridData1.verticalAlignment = GridData.CENTER;
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		
		if (dictPath == null) {
		} else {
			text.setText(dictPath);
		}
		lDictionary = new Label(this, SWT.NONE);
		lDictionary.setText("Dictionary");
		lDictionary.setLayoutData(gridData1);
		text = new Text(this, SWT.BORDER);
		text.setText("Select dictionary");
		text.setLayoutData(gridData);
		text.setEditable(false);
		button = new Button(this, SWT.NONE);
		button.setText("Browse...");
		button
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						final String dict = getDict();
						setDictPath(dict);
					}
				});
		this.setLayout(gridLayout);
		// setSize(new Point(300, 200));
		createComposite();
		
		lTreshold = new Label(this, SWT.NONE);
		lTreshold.setText("Threshold");
		lTreshold.setLayoutData(gridData1);
		tThreshold = new Text(this, SWT.BORDER);
		tThreshold.setText("100");
		tThreshold.setLayoutData(gridData11);
		tThreshold.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				threshold = Integer.valueOf(tThreshold.getText());
			}
		});
	}

	private String getDict() {
		final FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.dic", "*" });
		fd.setText("Select Dictionary");
		final String path = fd.open();
		return path;
	}

	public void setDictPath(String dictPath) {
		this.dictPath = dictPath;
		this.text.setText(dictPath);
	}

	public String getDictPath() {
		return dictPath;
	}
	
	public int getThreshold() {
		return threshold;
	}
	
	public void setThreshold(int th) {
		this.threshold = th;
		tThreshold.setText(Integer.toString(th));
	}

	/**
	 * This method initializes composite	
	 *
	 */
	private void createComposite() {
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = false;
		gridData2.horizontalSpan = 3;
		gridData2.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 1;
		composite = new DefaultPreferenceOptions(this, SWT.NONE);
		composite.setLayout(gridLayout1);
		composite.setLayoutData(gridData2);
	}
	
	boolean isWWDigitsIgnored() {
		return composite.isWWDigitsIgnored();
	}
	
	boolean isWWMixedCaseIgnored() {
		return composite.isWWMixedCaseIgnored();
	}		
	
	boolean isUpperCase() {
		return composite.isUpperCase();
	}
	
	boolean isSingleLetter() {
		return composite.isSingleLetter();
	}
	
	boolean isWWNonLetters() {
		return composite.isWWNonLetters();
	}
	
	void setWWDigitsIgnored(boolean opt) {
		composite.setWWDigitsIgnored(opt);
	}
	
	void setWWMixedCaseIgnored(boolean opt) {
		composite.setWWMixedCaseIgnored(opt);
	}		
	
	void setUpperCase(boolean opt) {
		composite.setUpperCase(opt);
	}
	
	void setSingleLetter(boolean opt) {
		composite.setSingleLetter(opt);
	}
	
	void setWWNonLetters(boolean opt) {
		composite.setWWNonLetters(opt);
	}	
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
