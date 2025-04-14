package org.akazukin.intellij.background.utils;

import com.intellij.DynamicBundle;
import lombok.experimental.UtilityClass;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

@UtilityClass
public class BundleUtils {
    private static final String BUNDLE_NAME =
        "messages." + EditorBackgroundImage.PLUGIN_NAME;

    private static final DynamicBundle INSTANCE =
        new DynamicBundle(BundleUtils.class, BUNDLE_NAME);

    @NotNull
    public @Nls String message(
        @NotNull @PropertyKey(resourceBundle = BUNDLE_NAME) final String key,
        final Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
