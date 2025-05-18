package org.akazukin.intellij.background.utils;

import com.intellij.DynamicBundle;
import lombok.experimental.UtilityClass;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * Utility class for handling localization and retrieving messages
 * from a resource bundle.
 * Provides methods to retrieve localized messages defined
 * in a specific resource bundle for the plugin.
 * <p>
 * The resource bundle is identified by {@code BUNDLE_NAME}.
 * This class uses the {@link DynamicBundle}
 * to dynamically load and manage localization resources.
 */
@UtilityClass
public class BundleUtils {
    public static final String BUNDLE_NAME =
        "messages." + EditorBackgroundImage.PLUGIN_NAME;

    private static final DynamicBundle INSTANCE =
        new DynamicBundle(BundleUtils.class, BUNDLE_NAME);

    /**
     * Retrieves a localized message from a resource bundle using the given key
     * and formats it with optional parameters.
     * This method leverages {@link DynamicBundle} for resource management.
     *
     * @param key    The key for the message in the resource bundle.
     *               Must not be {@code null} and should correspond to a valid entry
     *               in the resource bundle identified by {@link BundleUtils#BUNDLE_NAME}.
     * @param params Optional parameters to format the retrieved message.
     *               These parameters will replace placeholders in the message template.
     * @return A localized and optionally formatted message
     * Never returns {@code null}.
     */
    @NotNull
    public @Nls String message(
        @NotNull @PropertyKey(resourceBundle = BUNDLE_NAME) final String key,
        final Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
