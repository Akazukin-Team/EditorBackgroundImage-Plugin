package org.akazukin.intellij.background;

import java.io.File;
import lombok.Getter;
import lombok.Setter;

public final class EditorBackgroundImage {
    public static final String PLUGIN_NAME_SPACE = "Editor Background Image";
    public static final String PLUGIN_NAME = "EditorBackgroundImage";
    public static final String PLUGIN_ID = "editor_background_image";
    public static final String ACT_PLUGIN_ID = "org.akazukin.editorBackgroundImage";
    public static final String PLUGIN_VERSION = "1.1.0";

    @Getter
    @Setter
    private static File[] imageCache;
}
