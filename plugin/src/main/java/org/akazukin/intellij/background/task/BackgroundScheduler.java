package org.akazukin.intellij.background.task;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.gui.Settings;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public final class BackgroundScheduler {
    final EditorBackgroundImage plugin;
    ScheduledExecutorService pool = null;

    public void schedule() {
        final Config.State state = Config.getInstance();
        final int interval = state.getIntervalAmount();
        final int timeUnit = state.getIntervalUnit();

        if (interval == 0) {
            return;
        }

        final PropertiesComponent props = PropertiesComponent.getInstance();
        final int delay = props.isValueSet(IdeBackgroundUtil.EDITOR_PROP)
            ? interval : 0;
        final TimeUnit timeUnitEnum = Settings.TIME_UNITS[timeUnit];

        synchronized (this) {
            this.shutdown();

            log.info("Schedule " + this.plugin.getTaskMgr()
                .getTask(SetRandomBackgroundTask.class).getTaskName());

            this.pool = Executors.newScheduledThreadPool(1);
            this.pool.scheduleWithFixedDelay(() -> {
                if (!this.plugin.getTaskMgr()
                    .getTask(SetRandomBackgroundTask.class).get()) {
                    this.shutdown();
                }
            }, delay, interval, timeUnitEnum);
        }
    }

    public synchronized void shutdown() {
        if (this.pool != null && !this.pool.isTerminated()) {
            log.info("Shutdown scheduled tasks " + this.plugin.getTaskMgr()
                .getTask(SetRandomBackgroundTask.class).getTaskName());

            this.pool.shutdownNow();
            this.pool = null;
        }
    }
}
