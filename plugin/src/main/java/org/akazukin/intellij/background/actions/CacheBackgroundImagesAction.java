package org.akazukin.intellij.background.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.task.tasks.CacheBackgroundImagesTask;
import org.jetbrains.annotations.NotNull;

/**
 * An action that triggers caching of background images for use in the plugin.
 * <p>
 * This action interacts with the plugin's task manager to execute
 * the {@link CacheBackgroundImagesTask}, which processes the configured image
 * paths and validates the image files for caching. The caching process ensures
 * that only valid background images are loaded and available for later
 * operations.
 */
public final class CacheBackgroundImagesAction extends AnAction {
    /**
     * A constructor for the `CacheBackgroundImagesAction` class.
     * Initializes the action with a name, description, and icon.
     */
    public CacheBackgroundImagesAction() {
        super("Cache Background Images", "Cache images from the paths", null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        PluginHandler.getPlugin().getTaskMgr()
            .getServiceByImplementation(CacheBackgroundImagesTask.class).get();
    }
}
