package org.akazukin.intellij.background;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class Utils {
    private static final MimetypesFileTypeMap FILE_TYPE_MAP = new MimetypesFileTypeMap();

    /**
     * @param name boolean as strings as
     * @return Returns true if only if param equal to, ignoring case, the string is "true". or if param equal to, ignoring case, the string is "false".
     * @throws java.lang.IllegalArgumentException throws exception if param is invalid for to return
     */
    public static boolean getAsBoolean(@NotNull final String name) throws IllegalArgumentException {
        if ("true".equalsIgnoreCase(name)) {
            return true;
        }
        if ("false".equalsIgnoreCase(name)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid boolean value " + name);
    }

    public static File[] collectFiles(@NotNull final File directory, final int depth) {
        if (directory.isFile()) {
            throw new IllegalArgumentException("directory is not a file");
        }
        if (depth < 0) {
            throw new IllegalArgumentException("depth is not positive");
        }

        final File[] files = directory.listFiles();
        if (depth == 0) {
            return files;
        }

        final List<File> files2 = new ArrayList<>();
        for (final File file : files) {
            if (file.isDirectory()) {
                files2.addAll(Arrays.asList(collectFiles(file, depth - 1)));
            } else {
                files2.add(file);
            }
        }

        return files2.toArray(new File[0]);
    }

    public static boolean isValidImage(final File file) {
        if (!(file.exists() && file.isFile() && file.canRead())) {
            return false;
        }
        final String contentType = FILE_TYPE_MAP.getContentType(file);
        return contentType.startsWith("image/") || contentType.equals("application/octet-stream");
    }

    public static void notice(final String title, final String message, final NotificationType type) {
        Notifications.Bus.notify(
            new Notification(
                EditorBackgroundImage.PLUGIN_ID,
                title,
                message,
                type
            ));
    }
}
