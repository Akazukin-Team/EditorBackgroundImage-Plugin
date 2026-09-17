package org.akazukin.intellij.background.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.task.BackgroundScheduler;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an action to toggle automatic background image switching functionality.
 * It integrates with the {@link EditorBackgroundImage} plugin to enable or disable
 * the auto background change feature and manages scheduled tasks accordingly.
 * <p>
 * This action is executed when triggered, and its state is updated based on
 * the current auto-switch configuration.
 */
public final class ToggleAutoSwitchAction extends AnAction {

    /**
     * A constructor for the `ToggleAutoSwitchAction` class.
     * Initializes the action with a name, description, and icon.
     */
    public ToggleAutoSwitchAction() {
        super("Toggle Auto Background Switch", "Enable or disable automatic background image switching", null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final EditorBackgroundImage plugin = PluginHandler.getPlugin();
        final Config.State state = Config.getInstance();
        final BackgroundScheduler scheduler = plugin.getScheduler();

        // フラグを反転
        final boolean newStatus = !state.isAutoChangeEnabled();
        state.setAutoChangeEnabled(newStatus);

        synchronized (scheduler) {
            if (newStatus) {
                plugin.getTaskMgr()
                    .getServiceByInterfaceClass(SetRandomBackgroundTask.class).get();
                if (!scheduler.isScheduled()) {
                    scheduler.schedule();
                }
            } else {
                scheduler.shutdown();
            }
        }
    }
}
