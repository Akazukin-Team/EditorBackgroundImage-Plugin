package org.akazukin.intellij.background;

import com.intellij.ide.plugins.PluginManager;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class PluginHandler {
    @Getter
    @Nullable
    private static EditorBackgroundImage plugin;

    public synchronized void onUnload() {
        if (plugin == null) {
            return;
        }
        plugin.onDisable();
        plugin = null;
    }

    public boolean isLoaded() {
        return PluginManager.getInstance()
            .findEnabledPlugin(EditorBackgroundImage.ACT_PLUGIN_ID) != null;
    }

    public synchronized void onStartup() {
        if (!isInitialized()) {
            init();
        }
        plugin.onEnable();
    }

    public synchronized void init() {
        plugin = new EditorBackgroundImage();
    }

    public boolean isInitialized() {
        return plugin != null;
    }

    public synchronized boolean isEnabled() {
        return plugin != null && plugin.isEnabled();
    }

    public synchronized void onEnable() {
        plugin.onEnable();
    }

    public synchronized void onDisable() {
        plugin.onDisable();
    }
}
