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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.spelling.SpellingEngineDescriptor;
import org.osgi.framework.BundleContext;

import com.stibocatalog.hunspell.CLog;
import com.stibocatalog.hunspell.Hunspell;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 */
public class Hunspell4EclipsePlugin extends AbstractUIPlugin {

	private static final String CONTENT_TYPE_ID = "contentType";
	private static final String GOVERNS_CONTENT_TYPE_ID = "governsContentTypeId";

	// The shared instance
	private static Hunspell4EclipsePlugin plugin;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.lorands.hunspell4eclipse";

	public static final String SPELLENGINE_EXTENSION_POINT_ID = "com.lorands.hunspell4eclipse.content.governor";

	public static final String SPELLING_ACCEPT_ENGLISH = "accept.english";
	// the plug-in preferences
	public static final String SPELLING_DICTPATH = "DictPath";
	public static final String SPELLING_OPTIONS = "DefaultOptions";
	public static final String SPELLING_PROBLEMS_THRESHOLD = "Threshold.problems";
	public static final String SPELLING_PROPOSALS_THRESHOLD = "Threshold.proposals";

	/**
	 * Find suitable engine or return null.
	 * 
	 * @param id
	 * @return
	 */
	public static HunspellEngineBase findEngine(String id) {
		IConfigurationElement[] configArray = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(SPELLENGINE_EXTENSION_POINT_ID);

		// diagnose (activated if the "hunspell.log.on" system
		// property is defined).
		if (CLog.on())
			CLog.logOut(Hunspell4EclipsePlugin.class, "findEngine",
					" idToFind=[%s] nbConfigFound=[%d]", id,
					(configArray != null) ? configArray.length : -1);

		int wConfigIdx = 0;

		for (IConfigurationElement config : configArray) {
			IConfigurationElement[] contents = config
					.getChildren(CONTENT_TYPE_ID);

			if (contents.length > 0) {

				String wGovernsContentTypeId = contents[0]
						.getAttribute(GOVERNS_CONTENT_TYPE_ID);

				// diagnose (activated if the "hunspell.log.on" system
				// property is defined).
				if (CLog.on())
					CLog.logOut(Hunspell4EclipsePlugin.class, "findEngine",
							"configIdx=[%d] %s=[%s]", wConfigIdx,
							SPELLENGINE_EXTENSION_POINT_ID,
							wGovernsContentTypeId);

				// System.out.println(config.getChildren("contentType")[0].getAttribute("governsContentTypeId"));
				if (wGovernsContentTypeId != null
						&& wGovernsContentTypeId.equals(id)) {
					try {
						final HunspellEngineBase engine = (HunspellEngineBase) config
								.createExecutableExtension("class");

						// diagnose (activated if the "hunspell.log.on" system
						// property is defined).
						if (CLog.on())
							CLog.logOut(Hunspell4EclipsePlugin.class,
									"findEngine", "new engine=[%s]",
									engine.toString());

						return engine;
					} catch (CoreException e) {
						CLog.logErr(
								Hunspell4EclipsePlugin.class,
								"findEngine",
								e,
								"Unable to instanciate engine matching the extension point [%s]. id=[%s]",
								SPELLENGINE_EXTENSION_POINT_ID, id);
					}
				}
			}
			wConfigIdx++;
		}

		return null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Hunspell4EclipsePlugin getDefault() {
		return Hunspell4EclipsePlugin.plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				Hunspell4EclipsePlugin.PLUGIN_ID, path);
	}

	/**
	 * @return
	 */
	public static HunspellPreferences getPrefs() {
		return new HunspellPreferences();
	}

	/**
	 * @param aSpellingEngineDescriptor
	 * @return
	 */
	public static boolean isMyDescriptor(
			SpellingEngineDescriptor aSpellingEngineDescriptor) {
		return SpellingEngineImpl.ENGINE_ID.equals(aSpellingEngineDescriptor
				.getId());
	}

	private Hunspell hunspell;

	/**
	 * The constructor
	 */
	public Hunspell4EclipsePlugin() {
		super();
	}

	/**
	 * @return
	 */
	public Hunspell getHunspell() {
		return hunspell;
	}

	/**
	 * 
	 */
	private void logsGovernors() {
		// test purpose
		IConfigurationElement[] configArray = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(SPELLENGINE_EXTENSION_POINT_ID);

		for (IConfigurationElement config : configArray) {

			CLog.logOut(this, "findGovernors",
					"Class=[%s] Label=[%s] ContentTypeId=[%s]",
					config.getAttribute("class"), config.getAttribute("label"),
					config.getAttribute(GOVERNS_CONTENT_TYPE_ID));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Hunspell4EclipsePlugin.plugin = this;

		// init once!
		hunspell = Hunspell.getInstance();

		// diagnose (activated if the "hunspell.log.on" system
		// property is defined).
		if (CLog.on()) {
			String wName = "???";
			String wVersion = "???";
			if (getBundle() != null) {
				wName = getBundle().getSymbolicName();
				wVersion = getBundle().getVersion().toString();
			}
			CLog.logOut(this, "start", "Bundle [%s] Version=[%s] started",
					wName, wVersion);

			// for test
			logsGovernors();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		Hunspell4EclipsePlugin.plugin = null;
		super.stop(context);

		// diagnose (activated if the "hunspell.log.on" system
		// property is defined).
		if (CLog.on())
			CLog.logOut(this, "stop", "Bundle [%s] stopped", context
					.getBundle().getSymbolicName());

	}

}
