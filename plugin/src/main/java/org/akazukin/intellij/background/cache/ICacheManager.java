package org.akazukin.intellij.background.cache;

import org.jetbrains.annotations.NotNull;

public interface ICacheManager {
    <T extends ICache> T getCache(@NotNull Class<T> service);

    ICache[] getCaches();
}
