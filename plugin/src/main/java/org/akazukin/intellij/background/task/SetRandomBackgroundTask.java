package org.akazukin.intellij.background.task;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.utils.FileUtils;
import org.akazukin.intellij.background.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public final class SetRandomBackgroundTask implements ITask<Boolean> {
    EditorBackgroundImage plugin;

    Random random = new Random();

    @Override
    public String getTaskName() {
        return "SetRandomBackground";
    }

    @Override
    @NotNull
    public Boolean get() {
        final PropertiesComponent props = PropertiesComponent.getInstance();
        final Config.State state = Config.getInstance();

        final List<String> targets = new ArrayList<>();
        if (state.isChangeEditor()) {
            targets.add(IdeBackgroundUtil.EDITOR_PROP);
        }
        if (state.isChangeFrame()) {
            targets.add(IdeBackgroundUtil.FRAME_PROP);
        }

        if (this.plugin.getImageCache() == null) {
            if (!this.plugin.getTaskMgr()
                .getTask(CacheBackgroundImagesTask.class).get()) {
                state.setChanges(false);
                return false;
            }
        }

        final File[] images = this.plugin.getImageCache();
        File image = null;
        for (final String type : targets) {
            if (image == null
                || !(state.isSynchronizeImages() || images.length == 1)) {
                for (int i = 0; i < 10; i++) {
                    image = images[this.random.nextInt(images.length)];
                    if (image != null && FileUtils.isValidImage(image)) {
                        break;
                    } else {
                        image = null;
                    }
                }
                if (image == null) {
                    NotificationUtils.error("Error",
                        "Failed to fetch image paths");
                    state.setChanges(false);
                    return false;
                }
            }
            props.setValue(type, image.getAbsolutePath());
        }

        return true;
    }
}
