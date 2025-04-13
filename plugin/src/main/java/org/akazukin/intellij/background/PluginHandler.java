package org.akazukin.intellij.background;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import lombok.experimental.UtilityClass;
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.tasks.BackgroundScheduler;
import org.akazukin.intellij.background.tasks.SetRandomBackgroundTask;

@UtilityClass
public final class PluginHandler {
    public void onUnload() {
        BackgroundScheduler.shutdown();
    }

    public boolean isLoaded() {
        return PluginManager.getInstance()
            .findEnabledPlugin(
                PluginId.findId(EditorBackgroundImage.ACT_PLUGIN_ID)) != null;
    }

    public void onLoad() {
        final Config.State state = Config.getInstance();
        if (state.isChanges()) {
            new SetRandomBackgroundTask().getAsBoolean();
            BackgroundScheduler.schedule();
        }
    }
}
