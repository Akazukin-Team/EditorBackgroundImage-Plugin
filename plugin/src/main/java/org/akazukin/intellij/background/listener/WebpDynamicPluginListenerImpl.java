package org.akazukin.intellij.background.listener;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.PluginHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Implementation of the {@link DynamicPluginListener} interface,
 * responsible for handling events related to the loading
 * and unloading of dynamic plugins within an IntelliJ-based environment.
 * <p>
 * This class specifically listens for plugin lifecycle events
 * and performs operations when this plugin is loaded or unloaded.
 */
@Slf4j
public final class WebpDynamicPluginListenerImpl implements DynamicPluginListener {
    public static final PluginId PLUGIN_ID = PluginId.getId("intellij.webp");

    @Override
    public void pluginLoaded(
        @NotNull final IdeaPluginDescriptor pluginDescriptor) {
        if (!Objects.equals(pluginDescriptor.getPluginId(), PLUGIN_ID)) {
            return;
        }

        synchronized (PluginHandler.getLOCK()) {
            PluginHandler.getPlugin().getCachedSettings().setWebpSupport(pluginDescriptor.isEnabled());
        }
    }

    @Override
    public void beforePluginUnload(
        @NotNull final IdeaPluginDescriptor pluginDescriptor, final boolean isUpdate) {
        if (!Objects.equals(pluginDescriptor.getPluginId(), PLUGIN_ID)) {
            return;
        }

        synchronized (PluginHandler.getLOCK()) {
            PluginHandler.getPlugin().getCachedSettings().setWebpSupport(false);
        }
    }
}
