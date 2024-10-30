package org.akazukin.intellij.background.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.Config;
import org.akazukin.intellij.background.tasks.BackgroundScheduler;
import org.akazukin.intellij.background.tasks.SetRandomBackgroundTask;
import org.jetbrains.annotations.NotNull;

public final class SetBackground extends AnAction {

    public SetBackground() {
        super("Set Random Background Image", "Set random background image from cached images", null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Config.State state = Config.getInstance();
        if (state.isChanges()) {
            BackgroundScheduler.schedule();
        } else {
            new SetRandomBackgroundTask().getAsBoolean();
        }
    }
}
