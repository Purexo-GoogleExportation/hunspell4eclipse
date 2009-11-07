package com.lorands.hunspell4eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

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

	private Label label = null;
	private Text text = null;
	private Button button = null;
	private String dictPath = null; // @jve:decl-index=0:

	public HunspellPrefsComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		final GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = false;
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		label = new Label(this, SWT.NONE);
		label.setText("Dictionary");
		label.setLayoutData(gridData1);
		text = new Text(this, SWT.BORDER);
		if (dictPath == null) {
			text.setText("Select dictionary");
		} else {
			text.setText(dictPath);
		}
		text.setEditable(false);
		text.setLayoutData(gridData);
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
}
