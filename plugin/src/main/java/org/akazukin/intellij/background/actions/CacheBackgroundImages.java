package org.akazukin.intellij.background.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.tasks.CacheBackgroundImagesTask;
import org.jetbrains.annotations.NotNull;

public final class CacheBackgroundImages extends AnAction {
    public CacheBackgroundImages() {
        super("Cache Background Images", "Cache images from the paths", null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new CacheBackgroundImagesTask().getAsBoolean();
    }
}
