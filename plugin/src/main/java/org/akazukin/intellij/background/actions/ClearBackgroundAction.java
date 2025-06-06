package org.akazukin.intellij.background.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.settings.Config;
import org.jetbrains.annotations.NotNull;

/**
 * An action that clears background images
 * and stops a scheduled background task for an automatic change background.
 * <p>
 * Once triggered, the action:
 * - Shuts down the background scheduler associated with the plugin.
 * - Resets the editor and frame properties for background images
 * to their default disabled state.
 * - Disables the auto-change functionality for background images
 * in the plugin's configuration.
 * <p>
 * Use this action to reset the plugin's background features to an
 * inactive state, stopping any ongoing or automated operations
 * related to background images.
 */
public final class ClearBackgroundAction extends AnAction {
    /**
     * A constructor for the `ClearBackgroundAction` class.
     * Initializes the action with a name, description, and icon.
     */
    public ClearBackgroundAction() {
        super("Clear Background",
            "Clear background images and stop animation",
            null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        PluginHandler.getPlugin().getScheduler().shutdown();

        final PropertiesComponent props = PropertiesComponent.getInstance();
        props.setValue(IdeBackgroundUtil.EDITOR_PROP, false);
        props.setValue(IdeBackgroundUtil.FRAME_PROP, false);

        final Config.State state = Config.getInstance();
        state.setAutoChangeEnabled(false);
    }
}
