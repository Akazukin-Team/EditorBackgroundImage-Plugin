package org.akazukin.intellij.background.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.akazukin.intellij.background.EditorBackgroundImage;

public class NotificationUtils {

    public static void error(final String title, final String message) {
        notice(title, message, NotificationType.ERROR);
    }

    public static void notice(
        final String title, final String message, final NotificationType type) {
        Notifications.Bus.notify(
            new Notification(
                EditorBackgroundImage.PLUGIN_ID_STRING,
                title,
                message,
                type
            ));
    }

    public static void warning(final String title, final String message) {
        notice(title, message, NotificationType.WARNING);
    }

    public static void info(final String title, final String message) {
        notice(title, message, NotificationType.INFORMATION);
    }
}
