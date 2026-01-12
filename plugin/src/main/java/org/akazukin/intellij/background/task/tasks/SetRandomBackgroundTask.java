package org.akazukin.intellij.background.task.tasks;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.intellij.Adjust;
import org.akazukin.intellij.background.intellij.BackgroundData;
import org.akazukin.intellij.background.intellij.BackgroundManager;
import org.akazukin.intellij.background.intellij.Frame;
import org.akazukin.intellij.background.intellij.Position;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.utils.FileUtils;
import org.akazukin.intellij.background.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
@Slf4j
public final class SetRandomBackgroundTask implements ITask<Boolean> {
    EditorBackgroundImage plugin;

    Random random = ThreadLocalRandom.current();

    @Override
    public String getTaskName() {
        return "SetRandomBackground";
    }

    @Override
    public Boolean get() {
        log.info("Try to set random background image");
        try {
            final Config.State state = Config.getInstance();

            final Targets[] targets = this.getTargets();
            if (targets.length == 0) {
                return false;
            }

            @NotNull File[] cachedImg = this.plugin.getCachedSettings().getImageCache();
            if (cachedImg.length < targets.length) {
                cachedImg = this.plugin.getTaskMgr()
                    .getServiceByInterfaceClass(CacheBackgroundImagesTask.class).get();
                if (cachedImg.length < targets.length) {
                    state.setAutoChangeEnabled(false);
                    return false;
                }
            }

            final Set<File> cachedImgList =
                new HashSet<>(List.of(cachedImg));
            final Map<Target, File> curImgs = new HashMap<>();
            final boolean webpSupported = this.plugin.getCachedSettings().isWebpSupport();
            for (final Targets tar : targets) {
                // Set a target for the image that selected during the current loop

                // Set selectable images by cache
                final List<File> images =
                    new ArrayList<>(cachedImgList);
                // remove the images that already selected
                images.removeAll(curImgs.values());
                // remove duplicated image from props
                images.removeIf(f -> Arrays.stream(tar.targets)
                    .anyMatch(t -> {
                        final BackgroundData bg = BackgroundManager.getBackground(t.getFrame());
                        return bg != null && f.getAbsoluteFile().equals(bg.getFile());
                    }));

                // select an image in some tried or less
                File img = null;
                while (!images.isEmpty() && img == null) {
                    img = images.get(this.random.nextInt(images.size()));
                    images.remove(img);

                    if (!FileUtils.isValidImage(img, webpSupported)) {
                        cachedImgList.remove(img);
                        img = null;
                        continue;
                    }

                    for (final Target t : tar.targets) {
                        curImgs.put(t, img);
                    }
                }

                // when failed to fetch the image
                if (img == null) {
                    NotificationUtils.errorBundled(
                        "messages.failedFetchImg.title",
                        "messages.failedFetchImg.message");
                    this.plugin.getCachedSettings().setImageCache(FileUtils.EMPTY_FILES);
                    return false;
                }
            }
            this.plugin.getCachedSettings().setImageCache(cachedImgList.toArray(FileUtils.EMPTY_FILES));

            // Set the backgrounds
            for (final Map.Entry<Target, File> e : curImgs.entrySet()) {
                BackgroundManager.setBackground(e.getKey().getFrame(), e.getValue(), e.getKey().getOpacity(), e.getKey().getAdjust(), e.getKey().getPos());
                log.info("Set background image to {}, {}", e.getValue().getAbsolutePath(), e);
            }

            return true;
        } catch (final Throwable e) {
            log.error("Failed to set random background image", e);
            return false;
        }
    }

    private Targets[] getTargets() {
        final Config.State state = Config.getInstance();

        final List<Target> targets = new ArrayList<>();
        if (state.isChangeEditor()) {
            targets.add(new Target(Frame.EDITOR, (byte) state.getEditorOpacity(), state.getEditorPos(), state.getEditorAdjust()));
        }
        if (state.isChangeFrame()) {
            targets.add(new Target(Frame.FRAME, (byte) state.getFrameOpacity(), state.getFramePos(), state.getFrameAdjust()));
        }

        if (state.isSynchronizeImages()) {
            return new Targets[]{new Targets(targets.toArray(Target.ARR))};
        } else {
            return targets.stream().map(Targets::new).toArray(Targets[]::new);
        }
    }

    private static class Targets {
        Target[] targets;

        public Targets(final Target... targets) {
            this.targets = targets;
        }
    }

    @Getter
    private static class Target {
        public static final Target[] ARR = new Target[0];
        Frame frame;
        byte opacity;
        Position pos;
        Adjust adjust;

        public Target(final Frame frame, final byte opacity, final Position pos, final Adjust adjust) {
            this.frame = frame;
            this.opacity = opacity;
            this.pos = pos;
            this.adjust = adjust;
        }
    }
}
