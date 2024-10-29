package org.akazukin.intellij.background.tasks;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationType;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.Utils;
import org.akazukin.intellij.background.gui.Settings;

public final class CacheBackgroundImagesTask implements BooleanSupplier {
    @Override
    public boolean getAsBoolean() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        if (!props.isValueSet(Settings.IMAGES_ID) || props.getValue(Settings.IMAGES_ID).isEmpty()) {
            EditorBackgroundImage.setImageCache(null);
            return false;
        }

        int[] ids = Arrays.stream(props.getValue(Settings.IMAGES_ID).split(",")).map(Integer::valueOf).mapToInt(i -> i).toArray();
        Set<File> paths = new HashSet<>();
        for (int id : ids) {
            if (!props.isValueSet(Settings.IMAGE + "." + id + ".Path")
                || !Utils.getBoolean(props.getValue(Settings.IMAGE + "." + id + ".Enabled", "true"))) {
                continue;
            }
            paths.add(new File(props.getValue(Settings.IMAGE + "." + id + ".Path")));
        }

        if (paths.isEmpty()) {
            Utils.notice("Warning", "Image paths is empty", NotificationType.WARNING);
            props.setValue(Settings.CHANGE_EVERY, false);
            EditorBackgroundImage.setImageCache(null);
            return false;
        }

        Set<File> imagePaths = new HashSet<>();
        for (File path : paths) {
            if (path.isDirectory()) {
                imagePaths.addAll(Arrays.asList(Utils.collectFiles(path, 3)));
            } else {
                imagePaths.add(path);
            }
        }
        imagePaths.removeIf(file -> !Utils.isValidImage(file));

        if (imagePaths.isEmpty()) {
            Utils.notice("Error", "Images is empty", NotificationType.ERROR);
            props.setValue(Settings.CHANGE_EVERY, false);
            EditorBackgroundImage.setImageCache(null);
            return false;
        }

        EditorBackgroundImage.setImageCache(imagePaths.toArray(new File[0]));
        return true;
    }
}
