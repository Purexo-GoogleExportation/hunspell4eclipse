package com.lorands.hunspell4eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author L�r�nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 */
public final class GovernorsPreferences extends Composite {

	private Button checkBox = null;
	private Composite composite = null;
	private Group group = null;

	/**
	 * @param parent
	 * @param style
	 */
	public GovernorsPreferences(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createComposite() {
		composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout());
	}

	/**
	 * This method initializes group checkBox.setLayoutData(gridData);
	 * 
	 * 
	 */
	private void createGroup() {
		group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText("Governor");
		checkBox = new Button(group, SWT.CHECK);
		checkBox.setText("Enabled");
		checkBox.setSelection(true);
		createComposite();
	}

	/**
	 * 
	 */
	private void initialize() {
		createGroup();
		setSize(new Point(300, 200));
		setLayout(new FillLayout());
	}
}