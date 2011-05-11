package com.lorands.hunspell4eclipse.javaspell;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.stibocatalog.hunspell.CLog;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The shared instance
	private static Activator plugin;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.lorands.hunspell4eclipse.javaGovernor";

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * The constructor
	 */
	public Activator() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
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
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		// diagnose (activated if the "hunspell.log.on" system
		// property is defined).
		if (CLog.on())
			CLog.logOut(this, "stop", "Bundle [%s] stopped", context
					.getBundle().getSymbolicName());
	}

}
