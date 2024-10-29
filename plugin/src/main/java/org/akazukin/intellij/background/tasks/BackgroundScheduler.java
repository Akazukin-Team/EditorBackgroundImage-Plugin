package org.akazukin.intellij.background.tasks;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import org.akazukin.intellij.background.gui.Settings;

@UtilityClass
public final class BackgroundScheduler {

    private static final SetRandomBackgroundTask TASK = new SetRandomBackgroundTask();
    private static ScheduledExecutorService pool = null;

    public static void schedule() {
        PropertiesComponent props = PropertiesComponent.getInstance();
        int interval = props.getInt(Settings.INTERVAL, Settings.INTERVAL_SPINNER_DEFAULT);
        int timeUnit = props.getInt(Settings.TIME_UNIT, Settings.TIME_UNIT_DEFAULT);

        if (pool != null) {
            shutdown();
        }

        if (interval == 0) {
            return;
        }

        int delay = props.isValueSet(IdeBackgroundUtil.EDITOR_PROP) ? interval : 0;
        TimeUnit timeUnitEnum = Settings.TIME_UNITS[timeUnit];

        pool = Executors.newScheduledThreadPool(1);
        pool.scheduleWithFixedDelay(() -> {
            if (!TASK.getAsBoolean()) {
                shutdown();
            }
        }, delay, interval, timeUnitEnum);
    }

    public static void shutdown() {
        if (pool != null && !pool.isTerminated()) {
            pool.shutdownNow();
        }
        pool = null;
    }
}
