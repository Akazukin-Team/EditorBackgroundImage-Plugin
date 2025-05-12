package org.akazukin.intellij.background.task;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.service.ServiceManager;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TaskManager extends ServiceManager<ITask> {
    EditorBackgroundImage plugin;

    public TaskManager(@NotNull final EditorBackgroundImage plugin) {
        super(ITask.class);
        this.plugin = plugin;
    }

    public void registerServices() {
        this.registerService(new CacheBackgroundImagesTask(this.plugin));
        this.registerService(new SetRandomBackgroundTask(this.plugin));
    }
}
