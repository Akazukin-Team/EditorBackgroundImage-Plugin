package org.akazukin.intellij.background.cache;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class FileCache implements ICache {
    Map<File, FileData> cache = new HashMap<>();

    public void analyzeAndCache(final File[] files, final boolean enabled, final int maxdDepth, final boolean webpSupport) {
        final Set<File> fileSet = new HashSet<>();
        for (final File f : files) {
            if (f.isDirectory()) {
                fileSet.addAll(List.of(FileUtils.collectFiles(f, maxdDepth)));
            } else {
                fileSet.add(f);
            }
        }

        fileSet.forEach(f -> this.analyzeAndCache(f, enabled, webpSupport));
    }

    public void analyzeAndCache(final File file, final boolean enabled, final boolean webpSupport) {
        this.cache(file, enabled, webpSupport, FileUtils.isValidImage(file, webpSupport));
    }

    public synchronized void cache(final File file, final boolean enabled, final boolean webpSupport, final boolean valid) {
        this.cache.put(file, new FileData(enabled, new FileData.AnalyzeData(webpSupport, valid, file)));
    }

    @Override
    public synchronized void clear() {
        this.cache.clear();
    }

    @Override
    public synchronized File[] getValidFiles() {
        return this.cache.entrySet().stream()
            .filter(e -> e.getValue().getData().isValid())
            .map(Map.Entry::getKey)
            .toArray(File[]::new);
    }

    @Override
    public synchronized File[] getValidAndEnabledFiles() {
        return this.cache.entrySet().stream()
            .filter(e -> e.getValue().isEnabled() && e.getValue().getData().isValid())
            .map(Map.Entry::getKey)
            .toArray(File[]::new);
    }

    @Override
    public synchronized int getValidCount() {
        return (int) this.cache.entrySet().stream()
            .filter(e -> e.getValue().getData().isValid())
            .count();
    }

    @Override
    public int getValidAndEnabledCount() {
        return (int) this.cache.values().stream()
            .filter(e -> e.isEnabled() && e.getData().isValid())
            .count();
    }
}
