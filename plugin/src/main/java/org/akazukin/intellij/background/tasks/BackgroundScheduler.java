package org.akazukin.intellij.background.tasks;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import org.akazukin.intellij.background.Config;
import org.akazukin.intellij.background.gui.Settings;

@UtilityClass
public final class BackgroundScheduler {

    private static final SetRandomBackgroundTask TASK = new SetRandomBackgroundTask();
    private static ScheduledExecutorService pool = null;

    public static void schedule() {
        Config.State state = Config.getInstance();
        int interval = state.getIntervalAmount();
        int timeUnit = state.getIntervalUnit();

        if (pool != null) {
            shutdown();
        }

        if (interval == 0) {
            return;
        }

        PropertiesComponent props = PropertiesComponent.getInstance();
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
