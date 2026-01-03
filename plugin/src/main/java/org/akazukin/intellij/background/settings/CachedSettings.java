package org.akazukin.intellij.background.settings;

import lombok.Getter;
import lombok.Setter;
import org.akazukin.intellij.background.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Getter
@Setter
public class CachedSettings {
    boolean webpSupport;
    @NotNull
    File[] imageCache = FileUtils.EMPTY_FILES;
}
