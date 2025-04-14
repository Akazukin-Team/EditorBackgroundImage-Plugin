package org.akazukin.intellij.background.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.task.CacheBackgroundImagesTask;
import org.jetbrains.annotations.NotNull;

public final class CacheBackgroundImagesAction extends AnAction {
    public CacheBackgroundImagesAction() {
        super("Cache Background Images", "Cache images from the paths", null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        PluginHandler.getPlugin().getTaskMgr()
            .getTask(CacheBackgroundImagesTask.class).get();
    }
}
