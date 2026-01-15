package org.akazukin.intellij.background.cache;

import java.io.File;

public interface ICache {
    void clear();

    File[] getValidFiles();

    File[] getValidAndEnabledFiles();

    int getValidCount();

    int getValidAndEnabledCount();
}
