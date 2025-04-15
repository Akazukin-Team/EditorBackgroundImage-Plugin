package org.akazukin.intellij.background;

import com.intellij.openapi.extensions.PluginId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.task.BackgroundScheduler;
import org.akazukin.intellij.background.task.SetRandomBackgroundTask;
import org.akazukin.intellij.background.task.TaskManager;

import java.io.File;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class EditorBackgroundImage {
    public static final String PLUGIN_NAME_SPACE = "Editor Background Image";
    public static final String PLUGIN_NAME = "EditorBackgroundImage";
    public static final String PLUGIN_ID_STRING = "editor_background_image";
    public static final String ACT_PLUGIN_ID_STRING =
        "org.akazukin.editorBackgroundImage";
    public static final String PLUGIN_VERSION = "1.3.0";

    public static final PluginId ACT_PLUGIN_ID = PluginId.getId(ACT_PLUGIN_ID_STRING);

    final BackgroundScheduler scheduler = new BackgroundScheduler(this);
    final TaskManager taskMgr = new TaskManager(this);
    @Setter
    File[] imageCache;

    {
        this.taskMgr.registerTasks();
    }

    public void onEnable() {
        final Config.State state = Config.getInstance();
        if (state.isChanges()) {
            this.taskMgr.getTask(SetRandomBackgroundTask.class).get();
            this.scheduler.schedule();
        }
    }

    public void onUnload() {
        this.scheduler.shutdown();
    }
}
