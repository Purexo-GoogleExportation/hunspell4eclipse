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

	// The shared instance
	private static Hunspell4EclipsePlugin plugin;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.lorands.hunspell4eclipse";

	public static final String SPELLENGINE_EXTENSION_POINT_ID = "com.lorands.hunspell4eclipse.content.governor";

	// the plug-in preferences
	public static final String SPELLING_OPTIONS = "DefaultOptions";
	public static final String SPELLING_DICTPATH = "DictPath";
	public static final String SPELLING_PROBLEMS_THRESHOLD = "Threshold.problems";
	public static final String SPELLING_PROPOSALS_THRESHOLD = "Threshold.proposals";

	/**
	 * Find suitable engine or return null.
	 * 
	 * @param id
	 * @return
	 */
	public static AbstractHunSpellEngine findEngine(String id) {
		IConfigurationElement[] configArray = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(SPELLENGINE_EXTENSION_POINT_ID);

		for (IConfigurationElement config : configArray) {
			IConfigurationElement[] contents = config
					.getChildren("contentType");
			if (contents.length < 1) {
				continue;
			}
			// System.out.println(config.getChildren("contentType")[0].getAttribute("governsContentTypeId"));
			if (contents[0].getAttribute("governsContentTypeId").equals(id)) {
				try {
					final AbstractHunSpellEngine engine = (AbstractHunSpellEngine) config
							.createExecutableExtension("class");
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
		return Engine.ENGINE_ID.equals(aSpellingEngineDescriptor.getId());
	}

	private Hunspell hunspell;

	/**
	 * The constructor
	 */
	public Hunspell4EclipsePlugin() {
		super();
	}

	/**
	 * 
	 */
	private void findGovernors() {
		// test purpose
		IConfigurationElement[] configArray = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(SPELLENGINE_EXTENSION_POINT_ID);

		for (IConfigurationElement config : configArray) {

			CLog.logOut(this, "findGovernors",
					"Class=[%s] Label=[%s] ContentTypeId=[%s]",
					config.getAttribute("class"), config.getAttribute("label"),
					config.getAttribute("governsContentTypeId"));
		}
	}

	/**
	 * @return
	 */
	public Hunspell getHunspell() {
		return hunspell;
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
			findGovernors();
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
