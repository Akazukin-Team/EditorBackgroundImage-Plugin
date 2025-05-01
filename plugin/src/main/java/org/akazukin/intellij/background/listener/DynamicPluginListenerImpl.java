package org.akazukin.intellij.background.listener;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.PluginHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Slf4j
public final class DynamicPluginListenerImpl implements DynamicPluginListener {
    @Override
    public void pluginLoaded(
        @NotNull final IdeaPluginDescriptor pluginDescriptor) {
        if (!Objects.equals(pluginDescriptor.getPluginId(),
            EditorBackgroundImage.ACT_PLUGIN_ID)) {
            return;
        }

        log.warn("pluginLoaded");
        if (!PluginHandler.isInitialized()) {
            PluginHandler.init();
        }
        PluginHandler.onEnable();
    }

    @Override
    public void beforePluginUnload(@NotNull final IdeaPluginDescriptor pluginDescriptor, final boolean isUpdate) {
        if (!Objects.equals(pluginDescriptor.getPluginId(),
            EditorBackgroundImage.ACT_PLUGIN_ID)) {
            return;
        }

        log.warn("beforePluginUnload");
        PluginHandler.onUnload();
    }
}
