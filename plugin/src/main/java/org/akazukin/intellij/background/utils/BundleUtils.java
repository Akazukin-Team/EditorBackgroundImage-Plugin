package org.akazukin.intellij.background.utils;

import com.intellij.DynamicBundle;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.jetbrains.annotations.NotNull;

public class BundleUtils {
    private static final DynamicBundle INSTANCE = new DynamicBundle(BundleUtils.class, "messages." + EditorBackgroundImage.PLUGIN_NAME);

    @NotNull
    public static String message(String message, Object... params) {
        return INSTANCE.getMessage(message, params);
    }
}
