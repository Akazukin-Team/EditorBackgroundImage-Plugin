package org.akazukin.intellij.background.task.tasks;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.utils.FileUtils;
import org.akazukin.intellij.background.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A task responsible for caching background images for the EditorBackgroundImage plugin.
 * This task scans and validates image files as configured, generating a cache of valid image paths.
 * <p>
 * This task is executed when the plugin requires updating the cache of background images based
 * on user-defined configurations. It ensures that the file paths meet the plugin's requirements
 * for valid and accessible images.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public final class CacheBackgroundImagesTask implements ITask<Boolean> {
    EditorBackgroundImage plugin;

    @Override
    public String getTaskName() {
        return "CacheBackgroundImages";
    }

    @Override
    @NotNull
    public synchronized Boolean get() {
        final Config.State state = Config.getInstance();

        if (state.getImages().isEmpty()) {
            NotificationUtils.warningBundled("messages.nopath.title",
                "messages.nopath.message");
            state.setAutoChangeEnabled(false);
            this.plugin.setImageCache(FileUtils.EMPTY_FILES);
            return false;
        }

        final File[] files = state.getImages().entrySet().stream()
            .filter(Map.Entry::getValue)
            .map(e -> new File(e.getKey()))
            .toArray(File[]::new);

        final int depth = state.isHierarchicalExplore()
            ? state.getHierarchicalDepth() : 0;

        final Set<File> imagePaths = new HashSet<>();
        for (final File path : files) {
            if (path.isDirectory()) {
                imagePaths.addAll(
                    Arrays.asList(FileUtils.collectFiles(path, depth)));
            } else {
                imagePaths.add(path);
            }
        }
        imagePaths.removeIf(file -> !FileUtils.isValidImage(file));

        if (imagePaths.isEmpty()) {
            NotificationUtils.errorBundled("messages.noimage.title",
                "messages.noimage.message");
            state.setAutoChangeEnabled(false);
            this.plugin.setImageCache(FileUtils.EMPTY_FILES);
            return false;
        }

        this.plugin.setImageCache(imagePaths.toArray(FileUtils.EMPTY_FILES));
        return true;
    }
}
