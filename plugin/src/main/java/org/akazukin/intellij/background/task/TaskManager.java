package org.akazukin.intellij.background.task;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public final class TaskManager {
    EditorBackgroundImage plugin;

    Map<Class<? extends ITask<?>>, ITask<?>> taskMap = new HashMap<>();

    public void registerTasks() {
        this.registerTasks(
            new CacheBackgroundImagesTask(this.plugin),
            new SetRandomBackgroundTask(this.plugin)
        );
    }

    public void registerTasks(@NotNull final ITask<?>... tasks) {
        Arrays.stream(tasks).forEach(this::registerTask);
    }

    @SuppressWarnings("unchecked")
    public void registerTask(@NotNull final ITask<?> task) {
        this.taskMap.put((Class<? extends ITask<?>>) task.getClass(), task);
    }

    public void unregisterTasks(@NotNull final Class<ITask<?>>... tasks) {
        Arrays.stream(tasks).forEach(this::unregisterTask);
    }

    public void unregisterTask(@NotNull final Class<ITask<?>> task) {
        this.taskMap.remove(task);
    }

    @SuppressWarnings("unchecked")
    public <T extends ITask<?>> T getTask(final Class<T> taskClass) {
        return (T) this.taskMap.get(taskClass);
    }
}
