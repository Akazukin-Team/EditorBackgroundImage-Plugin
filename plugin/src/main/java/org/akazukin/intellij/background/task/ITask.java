package org.akazukin.intellij.background.task;

import org.akazukin.service.IService;

import java.util.function.Supplier;

public interface ITask<T> extends Supplier<T>, IService {
    String getTaskName();
}
