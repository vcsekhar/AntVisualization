package org.ant_vis.main;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.ant_vis"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private static boolean isDebugging = false;

    // private ILog logger;

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
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        // logger = getLog();
        isDebugging = "TRUE".equalsIgnoreCase(Platform.getDebugOption(PLUGIN_ID + "/debug"));
        debug("start: " + context);
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
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
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

    public static void debug(final String message) {
        // final IStatus status = new Status(IStatus.INFO, PLUGIN_ID, message,
        // null);
        // plugin.logger.log(status);
        if (!isDebugging) {
            return;
        }
        
        System.out.println(message);
    }
}
