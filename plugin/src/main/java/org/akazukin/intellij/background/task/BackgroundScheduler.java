package org.akazukin.intellij.background.task;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.settings.Settings;
import org.akazukin.intellij.background.utils.BundleUtils;
import org.akazukin.intellij.background.utils.NotificationUtils;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public final class BackgroundScheduler {
    final EditorBackgroundImage plugin;
    @Nullable
    ScheduledExecutorService pool;

    public boolean isScheduled() {
        return this.pool != null;
    }

    public void schedule() {
        final Config.State state = Config.getInstance();
        final int autoChangeInterval = state.getAutoChangeIntervalAmount();
        final TimeUnit autoChangeTimeUnit
            = Settings.TIME_UNITS[state.getAutoChangeIntervalUnit()];

        if (autoChangeInterval == 0) {
            return;
        }

        final PropertiesComponent props = PropertiesComponent.getInstance();
        final int delay = props.isValueSet(IdeBackgroundUtil.EDITOR_PROP)
            ? autoChangeInterval : 0;


        final int retryInterval = state.getRetryIntervalAmount();
        final TimeUnit retryTimeUnit
            = Settings.TIME_UNITS[state.getRetryIntervalUnit()];


        log.info("Schedule " + this.plugin.getTaskMgr()
            .getTask(SetRandomBackgroundTask.class).getTaskName());


        final ScheduledExecutorService pool
            = Executors.newSingleThreadScheduledExecutor();

        final Runnable task = () -> {
            if (BackgroundScheduler.this.plugin.getTaskMgr()
                .getTask(SetRandomBackgroundTask.class).get()) {
                return;
            }

            for (int fails = 0, end = state.getRetryTimes();
                 fails < end; ) {
                try {
                    NotificationUtils.warning(
                        BundleUtils.message("messages.retry.title"),
                        BundleUtils.message("messages.retry.message",
                            retryInterval,
                            BundleUtils.message(
                                "settings.timeunit."
                                    + retryTimeUnit.name().toLowerCase())));
                    Thread.sleep(
                        retryTimeUnit.toMillis(retryInterval));
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (!BackgroundScheduler.this.plugin.getTaskMgr()
                    .getTask(SetRandomBackgroundTask.class).get()) {
                    fails++;

                    if (fails == end) {
                        synchronized (BackgroundScheduler.this) {
                            if (BackgroundScheduler.this.pool == pool) {
                                BackgroundScheduler.this.shutdown();
                            }
                        }
                    }
                }
            }
        };

        synchronized (this) {
            this.shutdown();
            this.pool = pool;
            this.pool.scheduleWithFixedDelay(task, delay,
                autoChangeInterval, autoChangeTimeUnit);
        }
    }

    @SneakyThrows
    public synchronized void shutdown() {
        if (this.pool != null) {
            log.info("Shutdown scheduled tasks " + this.plugin.getTaskMgr()
                .getTask(SetRandomBackgroundTask.class).getTaskName());

            this.pool.shutdownNow();
            this.pool.awaitTermination(10, TimeUnit.SECONDS);
            this.pool = null;
        }
    }
}
