package org.akazukin.intellij.background.task.tasks;

import java.util.function.Supplier;

/**
 * Represents a generalized interface for executable tasks with a return value.
 * The interface extends {@link Supplier}, allowing tasks
 * to be executed with a defined output type.
 *
 * @param <T> The type of result produced by the task.
 */
public interface ITask<T> extends Supplier<T> {
    String getTaskName();
}
