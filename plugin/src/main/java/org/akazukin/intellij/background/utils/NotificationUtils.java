package org.akazukin.intellij.background.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import lombok.experimental.UtilityClass;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * Utility class for sending various types of notifications.
 * This class provides methods to send error, warning, and informational notifications.
 * Notifications can be sent with either plain text or localized messages from a resource bundle.
 */
@UtilityClass
public class NotificationUtils {

    public void error(final String title, final String message) {
        notice(title, message, NotificationType.ERROR);
    }

    public void notice(
        final String title, final String message, final NotificationType type) {
        Notifications.Bus.notify(
            new Notification(
                EditorBackgroundImage.PLUGIN_ID_STRING,
                title,
                message,
                type
            ));
    }

    public void warning(final String title, final String message) {
        notice(title, message, NotificationType.WARNING);
    }

    public void info(final String title, final String message) {
        notice(title, message, NotificationType.INFORMATION);
    }

    public void errorBundled(
        @NotNull @PropertyKey(resourceBundle = BundleUtils.BUNDLE_NAME) final String title,
        @NotNull @PropertyKey(resourceBundle = BundleUtils.BUNDLE_NAME) final String message) {
        noticeBundled(title, message, NotificationType.ERROR);
    }

    public void noticeBundled(
        @NotNull @PropertyKey(resourceBundle = BundleUtils.BUNDLE_NAME) final String title,
        @NotNull @PropertyKey(resourceBundle = BundleUtils.BUNDLE_NAME) final String message,
        final NotificationType type) {
        notice(BundleUtils.message(title), BundleUtils.message(message), type);
    }

    public void warningBundled(
        @NotNull @PropertyKey(resourceBundle = BundleUtils.BUNDLE_NAME) final String title,
        @NotNull @PropertyKey(resourceBundle = BundleUtils.BUNDLE_NAME) final String message) {
        noticeBundled(title, message, NotificationType.WARNING);
    }

    public void infoBundled(
        @NotNull @PropertyKey(resourceBundle = BundleUtils.BUNDLE_NAME) final String title,
        @NotNull @PropertyKey(resourceBundle = BundleUtils.BUNDLE_NAME) final String message) {
        noticeBundled(title, message, NotificationType.INFORMATION);
    }
}
