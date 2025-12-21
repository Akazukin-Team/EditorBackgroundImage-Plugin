package org.akazukin.intellij.background;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.listener.WebpDynamicPluginListenerImpl;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class for managing the lifecycle and state of an `EditorBackgroundImage` plugin.
 * This class provides methods to initialize, enable, disable, and check the status of the plugin.
 */
@UtilityClass
@Slf4j
public class PluginHandler {
    @Getter
    private final Object LOCK = new Object();
    @Getter
    @Nullable
    private EditorBackgroundImage plugin;

    public synchronized void onUnload() {
        if (plugin == null) {
            return;
        }
        plugin.onDisable();
        plugin = null;
    }

    /**
     * Determines whether the `EditorBackgroundImage` plugin is currently loaded and enabled.
     *
     * @return true if the `EditorBackgroundImage` plugin is loaded and enabled; false otherwise.
     */
    public boolean isLoaded() {
        return PluginManager.getInstance()
            .findEnabledPlugin(EditorBackgroundImage.ACT_PLUGIN_ID) != null;
    }

    /**
     * Ensures the proper startup of the plugin by initializing and enabling it.
     * If the plugin is not initialized, it calls the initialization method.
     * Invokes the `onEnable` method of the plugin instance to activate the plugin.
     * Synchronized to ensure thread-safe execution during startup.
     */
    public void onStartup() {
        synchronized (LOCK) {
            if (!isInitialized()) {
                init();
            }

            onEnable();
        }
    }

    /**
     * Initializes the `EditorBackgroundImage` plugin instance.
     * Creates a new instance of the `EditorBackgroundImage` class and assigns it to the plugin variable.
     * Synchronized to ensure thread-safety during initialization.
     */
    public void init() {
        synchronized (LOCK) {
            plugin = new EditorBackgroundImage();

            {
                final IdeaPluginDescriptor webpPl =
                    PluginManager.getInstance().findEnabledPlugin(WebpDynamicPluginListenerImpl.PLUGIN_ID);
                PluginHandler.getPlugin().getCachedSettings().setWebpSupport(webpPl != null && webpPl.isEnabled());
            }
        }
    }

    /**
     * Checks if the plugin instance has been initialized.
     *
     * @return true if the plugin instance is initialized; false otherwise.
     */
    public boolean isInitialized() {
        return plugin != null;
    }

    /**
     * Activates the `EditorBackgroundImage` plugin.
     * Invokes the `onEnable` method of the plugin instance.
     * This method is synchronized to ensure thread-safe execution during activation.
     */
    public synchronized void onEnable() {
        plugin.onEnable();
    }

    /**
     * Checks if the `EditorBackgroundImage` plugin is enabled and actively scheduled.
     *
     * @return true if the plugin instance is non-null and enabled; false otherwise.
     */
    public synchronized boolean isEnabled() {
        synchronized (LOCK) {
            return plugin != null && plugin.isEnabled();
        }
    }

    /**
     * Invokes the `onDisable` method of the `EditorBackgroundImage` plugin.
     * This method is synchronized to ensure thread-safe execution during deactivation.
     */
    public synchronized void onDisable() {
        plugin.onDisable();
    }
}
