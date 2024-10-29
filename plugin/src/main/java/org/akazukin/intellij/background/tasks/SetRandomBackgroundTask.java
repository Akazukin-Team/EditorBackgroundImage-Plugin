package org.akazukin.intellij.background.tasks;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.Utils;
import org.akazukin.intellij.background.gui.Settings;

public final class SetRandomBackgroundTask implements BooleanSupplier {

    private final Random random = new Random();

    @Override
    public boolean getAsBoolean() {
        PropertiesComponent props = PropertiesComponent.getInstance();

        List<String> targets = new ArrayList<>();
        if (props.getBoolean(Settings.EDITOR_BUTTON, false)) {
            targets.add(IdeBackgroundUtil.EDITOR_PROP);
        }
        if (props.getBoolean(Settings.FRAME_BUTTON, false)) {
            targets.add(IdeBackgroundUtil.FRAME_PROP);
        }

        if (EditorBackgroundImage.getImageCache() == null) {
            if (!new CacheBackgroundImagesTask().getAsBoolean()) {
                props.setValue(Settings.EDITOR_BUTTON, false);
                return false;
            }
        }

        boolean keepSameImage = props.getBoolean(Settings.SYNCHRONIZE_IMAGE);
        File[] images = EditorBackgroundImage.getImageCache();
        File image = null;
        for (String type : targets) {
            if (image == null || !(keepSameImage || images.length == 1)) {
                for (int i = 0; i < 10; i++) {
                    image = images[random.nextInt(images.length)];
                    if (image != null && Utils.isValidImage(image)) {
                        break;
                    } else {
                        image = null;
                    }
                }
                if (image == null) {
                    Utils.notice("Error", "Failed to fetch image paths", NotificationType.ERROR);
                    props.setValue(Settings.EDITOR_BUTTON, false);
                    return false;
                }
            }
            props.setValue(type, image.getAbsolutePath());
        }

        return true;
    }
}
