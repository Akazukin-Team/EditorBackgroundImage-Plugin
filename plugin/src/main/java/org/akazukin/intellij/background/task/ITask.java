package org.akazukin.intellij.background.task;

import java.util.function.Supplier;

public interface ITask<T> extends Supplier<T> {
    String getTaskName();
}
