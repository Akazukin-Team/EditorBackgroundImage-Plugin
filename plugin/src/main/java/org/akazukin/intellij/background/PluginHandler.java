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

    public void init() {
        plugin = new EditorBackgroundImage();
    }

    public void onUnload() {
        if (plugin == null) {
            return;
        }
        plugin.onUnload();
        plugin = null;
    }

    public boolean isEnabled() {
        return PluginManager.getInstance()
            .findEnabledPlugin(EditorBackgroundImage.ACT_PLUGIN_ID) != null;
    }

    public boolean isInitialized() {
        return plugin != null;
    }

    public void onEnable() {
        plugin.onEnable();
    }

    public void onDisable() {
        plugin.onUnload();
    }
}
