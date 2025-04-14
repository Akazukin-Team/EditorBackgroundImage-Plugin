package org.akazukin.intellij.background.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.task.SetRandomBackgroundTask;
import org.jetbrains.annotations.NotNull;

public final class SetBackgroundAction extends AnAction {

    public SetBackgroundAction() {
        super("Set Random Background Image",
            "Set random background image from cached images", null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Config.State state = Config.getInstance();
        if (state.isChanges()) {
            PluginHandler.getPlugin().getScheduler().schedule();
        } else {
            PluginHandler.getPlugin().getTaskMgr()
                .getTask(SetRandomBackgroundTask.class).get();
        }
    }
}
