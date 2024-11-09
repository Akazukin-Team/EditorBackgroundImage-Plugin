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
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.utils.Utils;

public final class SetRandomBackgroundTask implements BooleanSupplier {

    private final Random random = new Random();

    @Override
    public boolean getAsBoolean() {
        final PropertiesComponent props = PropertiesComponent.getInstance();
        final Config.State state = Config.getInstance();

        final List<String> targets = new ArrayList<>();
        if (state.isChangeEditor()) {
            targets.add(IdeBackgroundUtil.EDITOR_PROP);
        }
        if (state.isChangeFrame()) {
            targets.add(IdeBackgroundUtil.FRAME_PROP);
        }

        if (EditorBackgroundImage.getImageCache() == null) {
            if (!new CacheBackgroundImagesTask().getAsBoolean()) {
                state.setChanges(false);
                return false;
            }
        }

        final File[] images = EditorBackgroundImage.getImageCache();
        File image = null;
        for (final String type : targets) {
            if (image == null || !(state.isSynchronizeImages() || images.length == 1)) {
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
                    state.setChanges(false);
                    return false;
                }
            }
            props.setValue(type, image.getAbsolutePath());
        }

        return true;
    }
}
