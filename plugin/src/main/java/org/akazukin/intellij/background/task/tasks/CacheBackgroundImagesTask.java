package org.akazukin.intellij.background.task.tasks;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.cache.FileCache;
import org.akazukin.intellij.background.cache.UrlCache;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.utils.FileUtils;
import org.akazukin.intellij.background.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A task responsible for caching background images
 * for the EditorBackgroundImage plugin.
 * This task scans and validates image files as configured,
 * generating a cache of valid image paths.
 * <p>
 * This task is executed when the plugin
 * requires updating the cache of background images
 * based on user-defined configurations.
 * It ensures that the file paths meet the plugin's requirements
 * for valid and accessible images.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public final class CacheBackgroundImagesTask implements ITask<File[]> {
    EditorBackgroundImage plugin;

    @Override
    public String getTaskName() {
        return "CacheBackgroundImages";
    }

    @Override
    @NotNull
    public synchronized File[] get() {
        log.info("Try to cache background images");

        final Config.State state = Config.getInstance();

        final boolean webpSupported = this.plugin.getCachedSettings().isWebpSupport();

        final Set<File> imagePaths = new HashSet<>();
        {
            final int maxDepth = state.getHierarchicalDepth();

            final FileCache cache = this.plugin.getCacheMgr().getCache(FileCache.class);
            synchronized (cache) {
                cache.clear();
                state.getImages().forEach((file, enabled) ->
                    cache.analyzeAndCache(new File(file), enabled, maxDepth, webpSupported));
                imagePaths.addAll(Arrays.asList(cache.getValidAndEnabledFiles()));
            }
        }
        {
            final UrlCache cache = this.plugin.getCacheMgr().getCache(UrlCache.class);
            synchronized (cache) {
                cache.clear();
                state.getImageUrls().forEach((url, enabled) -> {
                    try {
                        cache.analyzeAndCache(URI.create(url).toURL(), enabled, webpSupported);
                    } catch (final MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                });
                imagePaths.addAll(Arrays.asList(cache.getValidAndEnabledFiles()));
            }
        }

        final File[] result = imagePaths.toArray(FileUtils.EMPTY_FILES);
        this.plugin.getCachedSettings().setImageCache(result);

        if (imagePaths.isEmpty()) {
            NotificationUtils.errorBundled("messages.noimage.title",
                "messages.noimage.message");
            state.setAutoChangeEnabled(false);
        }

        log.info("Cached {} background images", result.length);
        return result;
    }
}
