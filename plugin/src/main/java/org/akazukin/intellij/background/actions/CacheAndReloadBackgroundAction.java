package org.akazukin.intellij.background.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.task.tasks.CacheBackgroundImagesTask;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an action that facilitates caching background images and setting a random background
 * image for the editor.
 * <p>
 * Upon invocation, this action executes the following:
 * 1. Caches background images using the {@link CacheBackgroundImagesTask}.
 * 2. Sets a random background image using the {@link SetRandomBackgroundTask}.
 * <p>
 * The action interacts with the {@link EditorBackgroundImage} plugin to manage the caching
 * and background image operations via its task manager.
 */
public final class CacheAndReloadBackgroundAction extends AnAction {

    /**
     * A constructor for the `CacheAndReloadBackgroundAction` class.
     * Initializes the action with a name, description, and icon.
     */
    public CacheAndReloadBackgroundAction() {
        super("Cache and Reload Background", "Cache images and set a random background image", null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final EditorBackgroundImage plugin = PluginHandler.getPlugin();

        plugin.getTaskMgr()
            .getServiceByInterfaceClass(CacheBackgroundImagesTask.class).get();
        plugin.getTaskMgr()
            .getServiceByInterfaceClass(SetRandomBackgroundTask.class).get();
    }
}
