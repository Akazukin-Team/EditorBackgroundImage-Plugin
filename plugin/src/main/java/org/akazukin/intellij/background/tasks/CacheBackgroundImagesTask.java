package org.akazukin.intellij.background.tasks;

import com.intellij.notification.NotificationType;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.utils.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

public final class CacheBackgroundImagesTask implements BooleanSupplier {
    @Override
    public boolean getAsBoolean() {
        final Config.State state = Config.getInstance();

        if (state.getImages().isEmpty()) {
            Utils.notice("Warning",
                "Image paths is empty",
                NotificationType.WARNING);
            state.setChanges(false);
            EditorBackgroundImage.setImageCache(null);
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
                    Arrays.asList(Utils.collectFiles(path, depth)));
            } else {
                imagePaths.add(path);
            }
        }
        imagePaths.removeIf(file -> !Utils.isValidImage(file));

        if (imagePaths.isEmpty()) {
            Utils.notice("Error", "Images is empty", NotificationType.ERROR);
            state.setChanges(false);
            EditorBackgroundImage.setImageCache(null);
            return false;
        }

        EditorBackgroundImage.setImageCache(imagePaths.toArray(new File[0]));
        return true;
    }
}
