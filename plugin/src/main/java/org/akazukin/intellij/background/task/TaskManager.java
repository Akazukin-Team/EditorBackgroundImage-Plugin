package org.akazukin.intellij.background.task;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.task.tasks.CacheBackgroundImagesTask;
import org.akazukin.intellij.background.task.tasks.ITask;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
import org.akazukin.service.manager.single.ASingleServiceManager;
import org.jetbrains.annotations.NotNull;

/**
 * Manages task registration and execution within the plugin.
 * This class extends {@link ASingleServiceManager} to handle tasks
 * implementing the {@link ITask} interface.
 * <p>
 * Responsibilities:
 * - Initializes and registers task services.
 * - Manages task service lifecycle for plugin operations.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TaskManager extends ASingleServiceManager<ITask<?>> {
    EditorBackgroundImage plugin;

    @SuppressWarnings("unchecked")
    public TaskManager(@NotNull final EditorBackgroundImage plugin) {
        super((Class<ITask<?>>) (Object) ITask.class);
        this.plugin = plugin;
    }

    public void registerServices() {
        this.registerService(new CacheBackgroundImagesTask(this.plugin));
        this.registerService(new SetRandomBackgroundTask(this.plugin));
    }
}
