package org.akazukin.intellij.background.listener;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.PluginHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Implementation of the {@link DynamicPluginListener} interface,
 * responsible for handling events related to the loading and unloading of dynamic plugins
 * within an IntelliJ-based environment.
 * <p>
 * This class specifically listens for plugin lifecycle events
 * and performs operations when this plugin is loaded or unloaded.
 */
public final class DynamicPluginListenerImpl implements DynamicPluginListener {
    @Override
    public void pluginLoaded(
        @NotNull final IdeaPluginDescriptor pluginDescriptor) {
        if (!Objects.equals(pluginDescriptor.getPluginId(),
            EditorBackgroundImage.ACT_PLUGIN_ID)) {
            return;
        }

        PluginHandler.onStartup();
    }

    @Override
    public void beforePluginUnload(@NotNull final IdeaPluginDescriptor pluginDescriptor, final boolean isUpdate) {
        if (!Objects.equals(pluginDescriptor.getPluginId(),
            EditorBackgroundImage.ACT_PLUGIN_ID)) {
            return;
        }
        PluginHandler.onDisable();
    }
}
