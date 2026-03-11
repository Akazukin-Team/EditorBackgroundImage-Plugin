package org.akazukin.intellij.background.task;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.task.tasks.ITask;
import org.akazukin.service.manager.single.SingleServiceManager;
import org.akazukin.service.registry.IServiceRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Manages task registration and execution within the plugin.
 * This class extends {@link SingleServiceManager} to handle tasks
 * implementing the {@link ITask} interface.
 * <p>
 * Responsibilities:
 * - Initializes and registers task services.
 * - Manages task service lifecycle for plugin operations.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TaskManager extends SingleServiceManager<ITask<?>> {
    EditorBackgroundImage plugin;

    public TaskManager(@NotNull final IServiceRegistry<ITask<?>> reg, final EditorBackgroundImage plugin) {
        super(reg);
        this.plugin = plugin;
    }
}
