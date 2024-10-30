package org.akazukin.intellij.background;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import java.io.File;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import lombok.Getter;
import lombok.Setter;
import org.akazukin.intellij.background.tasks.BackgroundScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EditorBackgroundImage implements ProjectActivity, DynamicPluginListener {
    public static final String PLUGIN_NAME_SPACE = "Editor Background Image";
    public static final String PLUGIN_NAME = "EditorBackgroundImage";
    public static final String PLUGIN_ID = "editor_background_image";
    public static final String ACT_PLUGIN_ID = "org.akazukin.editorBackgroundImage";
    public static final String PLUGIN_VERSION = "1.0.0";

    @Getter
    @Setter
    private static File[] imageCache;

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        Config.State state = Config.getInstance();
        if (state.isChanges()) {
            BackgroundScheduler.schedule();
        }
        return null;
    }

    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        Utils.notice("Loaded", pluginDescriptor.getPluginId().getIdString(), NotificationType.INFORMATION);
        if (!isEditorBgImgPlugin(pluginDescriptor)) {
            return;
        }

        BackgroundScheduler.schedule();

        DynamicPluginListener.super.pluginLoaded(pluginDescriptor);
    }

    private static boolean isEditorBgImgPlugin(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        return pluginDescriptor.getPluginId().equals(PluginId.getId(ACT_PLUGIN_ID));
    }

    @Override
    public void pluginUnloaded(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        Utils.notice("Unloaded", pluginDescriptor.getPluginId().getIdString(), NotificationType.INFORMATION);
        if (!isEditorBgImgPlugin(pluginDescriptor)) {
            return;
        }

        BackgroundScheduler.shutdown();

        DynamicPluginListener.super.pluginUnloaded(pluginDescriptor, isUpdate);
    }
}
