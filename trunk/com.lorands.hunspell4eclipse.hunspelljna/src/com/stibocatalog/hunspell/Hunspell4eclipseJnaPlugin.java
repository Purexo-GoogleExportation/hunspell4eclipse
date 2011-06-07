package com.stibocatalog.hunspell;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author L—r‡nd Somogyi < lorand dot somogyi at gmail dot com >
 *         http://lorands.com
 * @author Olivier Gattaz < olivier dot gattaz at isandlatech dot com >
 * @date 28/04/2011 (dd/mm/yy)
 */
public class Hunspell4eclipseJnaPlugin extends AbstractUIPlugin {

	// The shared instance
	private static Hunspell4eclipseJnaPlugin plugin;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.lorands.hunspell4eclipse.hunspelljna"; //$NON-NLS-1$

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Hunspell4eclipseJnaPlugin getDefault() {
		return plugin;
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
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * The constructor
	 */
	public Hunspell4eclipseJnaPlugin() {
		super();
		// log some informations about the logger.
		CLog.logLoggerInfo();
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
		plugin = this;

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
		plugin = null;

		Hunspell.getInstance().destroyAllDictionaries();

		super.stop(context);

		// diagnose (activated if the "hunspell.log.on" system
		// property is defined).
		if (CLog.on())
			CLog.logOut(this, "stop", "Bundle [%s] stopped", context
					.getBundle().getSymbolicName());
	}
}
