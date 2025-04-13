package org.akazukin.intellij.background.tasks;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.experimental.UtilityClass;
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.gui.Settings;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class BackgroundScheduler {

    private static final SetRandomBackgroundTask TASK =
        new SetRandomBackgroundTask();
    private static ScheduledExecutorService pool = null;

    public static void schedule() {
        final Config.State state = Config.getInstance();
        final int interval = state.getIntervalAmount();
        final int timeUnit = state.getIntervalUnit();

        if (pool != null) {
            shutdown();
        }

        if (interval == 0) {
            return;
        }

        final PropertiesComponent props = PropertiesComponent.getInstance();
        final int delay = props.isValueSet(IdeBackgroundUtil.EDITOR_PROP)
            ? interval : 0;
        final TimeUnit timeUnitEnum = Settings.TIME_UNITS[timeUnit];

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
            pool = null;
        }
    }
}
