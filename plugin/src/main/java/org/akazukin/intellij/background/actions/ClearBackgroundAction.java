package org.akazukin.intellij.background.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.config.Config;
import org.jetbrains.annotations.NotNull;

public final class ClearBackgroundAction extends AnAction {

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
        state.setChanges(false);
    }
}
