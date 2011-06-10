/*******************************************************************************
 * Copyright (c) 2011 lorands.com, L—r‡nd Somogyi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    L—r‡nd Somogyi (lorands.com) - initial API and implementation
 *    Olivier Gattaz (isandlaTech) - improvments
 *******************************************************************************/
package com.lorands.hunspell4eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.lorands.hunspell4eclipse.i18n.Messages;

/**
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 * 
 */
class HunspellPrefsCompositeOptions extends Composite {

	private Button checkBox;

	private Button checkBox1;
	private Button checkBox2;
	private Button checkBox3;
	private Button checkBox4;
	private Group group1;
	private final Messages pMessages;

	/**
	 * @param parent
	 * @param style
	 */
	public HunspellPrefsCompositeOptions(Composite parent, int style) {
		super(parent, style);
		pMessages = Messages.getInstance();

		initialize();
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		// group = new Group(this, SWT.NONE);
		// group.setLayout(gridLayout);
		createGroup1();
	}

	/**
	 * This method initializes group1
	 * 
	 */
	private void createGroup1() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 1;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		group1 = new Group(this, SWT.NONE);
		group1.setText(pMessages.getString("prefs.group.options.title"));
		group1.setLayout(gridLayout1);
		group1.setLayoutData(gridData);
		checkBox = new Button(group1, SWT.CHECK);
		checkBox.setText(pMessages
				.getString("prefs.group.options.label.ignore.digits"));
		checkBox.setSelection(true);
		checkBox1 = new Button(group1, SWT.CHECK);
		checkBox1.setText(pMessages
				.getString("prefs.group.options.label.ignore.mixed"));
		checkBox1.setSelection(true);
		checkBox2 = new Button(group1, SWT.CHECK);
		checkBox2.setText(pMessages
				.getString("prefs.group.options.label.ignore.uppercase"));
		checkBox2.setSelection(true);
		checkBox3 = new Button(group1, SWT.CHECK);
		checkBox3.setText(pMessages
				.getString("prefs.group.options.label.ignore.single"));
		checkBox3.setSelection(true);
		checkBox4 = new Button(group1, SWT.CHECK);
		checkBox4.setText(pMessages
				.getString("prefs.group.options.label.ignore.notletter"));
		checkBox4.setSelection(true);
	}

	private void initialize() {
		createGroup();
		this.setLayout(new FillLayout());
		setSize(new Point(300, 200));
	}

	/**
	 * @return
	 */
	boolean isSingleLetter() {
		return checkBox3.getSelection();
	}

	/**
	 * @return
	 */
	boolean isUpperCase() {
		return checkBox2.getSelection();
	}

	/**
	 * @return
	 */
	boolean isWWDigitsIgnored() {
		return checkBox.getSelection();
	}

	/**
	 * @return
	 */
	boolean isWWMixedCaseIgnored() {
		return checkBox1.getSelection();
	}

	/**
	 * @return
	 */
	boolean isWWNonLetters() {
		return checkBox4.getSelection();
	}

	/**
	 * @param opt
	 */
	public void setSingleLetter(boolean opt) {
		checkBox3.setSelection(opt);

	}

	/**
	 * @param opt
	 */
	public void setUpperCase(boolean opt) {
		checkBox2.setSelection(opt);
	}

	/**
	 * @param opt
	 */
	public void setWWDigitsIgnored(boolean opt) {
		checkBox.setSelection(opt);
	}

	/**
	 * @param opt
	 */
	public void setWWMixedCaseIgnored(boolean opt) {
		checkBox1.setSelection(opt);

	}

	/**
	 * @param opt
	 */
	public void setWWNonLetters(boolean opt) {
		checkBox4.setSelection(opt);
	}

}
