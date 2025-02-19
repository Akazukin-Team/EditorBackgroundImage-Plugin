package org.akazukin.intellij.background.utils;

import com.intellij.DynamicBundle;
import lombok.experimental.UtilityClass;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BundleUtils {
    private static final DynamicBundle INSTANCE = new DynamicBundle(BundleUtils.class, "messages." + EditorBackgroundImage.PLUGIN_NAME);

    @NotNull
    public String message(final String message, final Object... params) {
        return INSTANCE.getMessage(message, params);
    }
}
