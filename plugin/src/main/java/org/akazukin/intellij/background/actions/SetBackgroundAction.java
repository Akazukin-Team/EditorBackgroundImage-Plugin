package org.akazukin.intellij.background.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
import org.jetbrains.annotations.NotNull;

/**
 * An action to set a random background image from cached images in the plugin.
 * <p>
 * This action uses the task manager from the plugin to execute the
 * {@link SetRandomBackgroundTask} responsible for selecting and applying
 * a random background image based on cached images.
 * <p>
 * Use this action to dynamically change the background image within the editor
 * or frame environments provided by the plugin. It is designed to update the
 * visuals based on user-configured and validated image caches.
 */
public final class SetBackgroundAction extends AnAction {
    public SetBackgroundAction() {
        super("Set Random Background Image",
            "Set random background image from cached images", null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        PluginHandler.getPlugin().getTaskMgr()
            .getServiceByImplementation(SetRandomBackgroundTask.class).get();
    }
}
