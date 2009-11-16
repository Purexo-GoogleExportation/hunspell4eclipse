package com.lorands.hunspell4eclipse;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.stibocatalog.hunspell.Hunspell;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Lorand Somogyi
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.lorands.hunspell4eclipse";
	
	public static final String SPELLENGINE_EXTENSION_POINT_ID = "com.lorands.hunspell4eclipse.content.governor";

	public static final String DICTPATH = "DictPath";
	public static final String DEFAULT_OPTIONS = "DefaultOptions";

	// The shared instance
	private static Activator plugin;

	private Hunspell hunspell;

	/**
	 * The constructor
	 */
	public Activator() {
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
		Activator.plugin = this;
		
		hunspell = Hunspell.getInstance(); // init once!
		
		findGovernors();
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
		Activator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return Activator.plugin;
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
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				path);
	}
	
	private void findGovernors() { //test purpose
		IConfigurationElement[] configArray = Platform.getExtensionRegistry()
			.getConfigurationElementsFor(SPELLENGINE_EXTENSION_POINT_ID);

		for (IConfigurationElement config : configArray) {
			System.out.println(String.format("%s [%s] (%s)", 
					config.getAttribute("class"),
					config.getAttribute("label"),
					config.getAttribute("governsContentTypeId")
			));
		}
	}
	
	/** Find suitable engine or return null.
	 * @param id
	 * @return
	 */
	public static AbstractHunSpellEngine findEngine(String id) { 
		IConfigurationElement[] configArray = Platform.getExtensionRegistry()
			.getConfigurationElementsFor(SPELLENGINE_EXTENSION_POINT_ID);

		for (IConfigurationElement config : configArray) {
			IConfigurationElement[] contents = config.getChildren("contentType");
			if(contents.length < 1 ) {
				continue;
			}
			//System.out.println(config.getChildren("contentType")[0].getAttribute("governsContentTypeId"));
			if( contents[0].getAttribute("governsContentTypeId").equals(id) ) {
				try {
					final AbstractHunSpellEngine engine = (AbstractHunSpellEngine) config.createExecutableExtension("class");
					return engine;
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		
		return null;
	}	
	
	public Hunspell getHunspell() {
		return hunspell;
	}
	
}
