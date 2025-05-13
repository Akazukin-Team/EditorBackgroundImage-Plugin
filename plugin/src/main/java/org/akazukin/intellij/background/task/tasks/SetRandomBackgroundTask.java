package org.akazukin.intellij.background.task.tasks;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.utils.FileUtils;
import org.akazukin.intellij.background.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A task implementation for setting a random background image in the IDE.
 * This task is responsible for fetching a random image from a cache of
 * preloaded images and updating the editor and/or frame background
 * properties based on the user's configuration.
 * <p>
 * The task ensures that the images used are valid and not already applied
 * as a background. Supports synchronized or independent updates to the
 * editor and frame backgrounds based on the configuration settings.
 */
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
    public Boolean get() {
        return this.get(0);
    }

    @NotNull
    public Boolean get(final int tries) {
        final PropertiesComponent props = PropertiesComponent.getInstance();
        final Config.State state = Config.getInstance();

        final List<String> targets = new ArrayList<>();
        if (state.isChangeEditor()) {
            targets.add(IdeBackgroundUtil.EDITOR_PROP);
        }
        if (state.isChangeFrame()) {
            targets.add(IdeBackgroundUtil.FRAME_PROP);
        }

        final int imgsCount = state.isSynchronizeImages() ? 1 : targets.size();

        if (this.plugin.getImageCache() == null
            || this.plugin.getImageCache().length < imgsCount) {
            if (!this.plugin.getTaskMgr()
                .getServiceByImplementation(CacheBackgroundImagesTask.class).get()) {
                state.setAutoChangeEnabled(false);
                return false;
            }
        }

        final List<File> cachedImgs =
            new ArrayList<>(List.of(this.plugin.getImageCache()));
        final List<File> curImgs = new ArrayList<>();
        for (int i = 0; i < imgsCount; i++) {
            // Set a target for the image that selected during the current loop
            final List<String> curTargets = new ArrayList<>();
            if (state.isSynchronizeImages()) {
                curTargets.addAll(targets);
            } else {
                curTargets.add(targets.get(i));
            }

            // Set selectable images by cache
            final List<File> images =
                new ArrayList<>(cachedImgs);
            // remove the images that already selected
            images.removeAll(curImgs);
            // remove duplicated image from props
            images.removeIf(f -> curTargets.stream().anyMatch(t ->
                f.getAbsolutePath().equals(props.getValue(t))));

            // select an image in some tried or less
            File img = null;
            while (img == null) {
                if (images.isEmpty()) {
                    break;
                }

                img = images.get(this.random.nextInt(images.size()));
                images.remove(img);

                if (!FileUtils.isValidImage(img)) {
                    cachedImgs.remove(img);
                    img = null;
                    continue;
                }

                curImgs.add(img);
            }

            // when failed to fetch the image
            if (img == null) {
                NotificationUtils.errorBundled(
                    "messages.failedFetchImg.title",
                    "messages.failedFetchImg.message");
                this.plugin.setImageCache(null);
                return false;
            }
        }
        this.plugin.setImageCache(cachedImgs.toArray(FileUtils.EMPTY_FILES));

        // Set the backgrounds
        int i3 = 0;
        for (final String t : targets) {
            props.setValue(t, curImgs.get(i3).getAbsolutePath());
            if (!state.isSynchronizeImages()) {
                i3++;
            }
        }

        return true;
    }
}
