package org.akazukin.intellij.background.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.gui.Settings;
import org.akazukin.intellij.background.tasks.BackgroundScheduler;
import org.akazukin.intellij.background.tasks.SetRandomBackgroundTask;
import org.jetbrains.annotations.NotNull;

public final class SetBackground extends AnAction {

    public SetBackground() {
        super("Set Random Background Image", "Set random background image from cached images", null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PropertiesComponent props = PropertiesComponent.getInstance();
        if (props.getBoolean(Settings.CHANGE_EVERY, false)) {
            BackgroundScheduler.schedule();
        } else {
            new SetRandomBackgroundTask().getAsBoolean();
        }
    }
}
