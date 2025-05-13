package org.akazukin.intellij.background;

import com.intellij.openapi.extensions.PluginId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.task.BackgroundScheduler;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
import org.akazukin.intellij.background.task.TaskManager;

import java.io.File;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Slf4j
public final class EditorBackgroundImage {
    public static final String PLUGIN_NAME_SPACE = "Editor Background Image";
    public static final String PLUGIN_NAME = "EditorBackgroundImage";
    public static final String PLUGIN_ID_STRING = "editor_background_image";
    public static final String ACT_PLUGIN_ID_STRING =
        "org.akazukin.editorBackgroundImage";

    public static final PluginId ACT_PLUGIN_ID = PluginId.getId(ACT_PLUGIN_ID_STRING);

    final BackgroundScheduler scheduler = new BackgroundScheduler(this);
    final TaskManager taskMgr = new TaskManager(this);
    @Setter
    File[] imageCache;

    {
        this.taskMgr.registerServices();
    }

    public boolean isEnabled() {
        return this.scheduler.isScheduled();
    }

    public void onEnable() {
        log.info("Enabling " + PLUGIN_NAME_SPACE);
        final Config.State state = Config.getInstance();
        if (state.isAutoChangeEnabled()) {
            synchronized (this.scheduler) {
                if (!this.scheduler.isScheduled()) {
                    this.taskMgr.getServiceByImplementation(SetRandomBackgroundTask.class).get();
                    this.scheduler.schedule();
                }
            }
        }
        log.info("Enabled " + PLUGIN_NAME_SPACE);
    }

    public void onDisable() {
        log.info("Disabling " + PLUGIN_NAME_SPACE);
        this.scheduler.shutdown();
        log.info("Disabled " + PLUGIN_NAME_SPACE);
    }
}
