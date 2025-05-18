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
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
import org.akazukin.intellij.background.utils.BundleUtils;
import org.akazukin.intellij.background.utils.NotificationUtils;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The class manages background operations for scheduling
 * and executing tasks related to the Editor Background Image plugin. It
 * schedules tasks to set random background images and allows retrying the task
 * execution in case of failure, based on configuration settings.
 * <br>
 * This class ensures the scheduling of tasks adheres to the configured
 * intervals and automatically shuts down scheduled tasks when required.
 * <p>
 * Thread-safety is maintained by synchronizing critical operations.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public final class BackgroundScheduler {
    private static final long POOl_TERMINATE_TIMEOUT = 5;
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
            .getServiceByImplementation(SetRandomBackgroundTask.class)
            .getTaskName());


        final ScheduledExecutorService pool
            = Executors.newSingleThreadScheduledExecutor();

        final Runnable task = () -> {
            try {
                for (int tries = 0, retries = state.getRetryTimes();
                     tries <= retries; tries++) {
                    if (BackgroundScheduler.this.plugin
                        .getTaskMgr()
                        .getServiceByImplementation(
                            SetRandomBackgroundTask.class).get()) {
                        return;
                    }

                    NotificationUtils.warning(
                        BundleUtils.message("messages.retry.title"),
                        BundleUtils.message("messages.retry.message",
                            retryInterval,
                            BundleUtils.message(
                                "settings.timeunit."
                                    + retryTimeUnit.name().toLowerCase())));
                    Thread.sleep(
                        retryTimeUnit.toMillis(retryInterval));
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                }
            } catch (final InterruptedException e) {
                synchronized (BackgroundScheduler.this) {
                    if (BackgroundScheduler.this.pool == pool) {
                        BackgroundScheduler.this.shutdown();
                    }
                }
                throw new RuntimeException(e);
            }

            synchronized (BackgroundScheduler.this) {
                if (BackgroundScheduler.this.pool == pool) {
                    BackgroundScheduler.this.shutdown();
                }
            }
        };

        synchronized (this) {
            this.shutdown();
            this.pool = pool;
            this.pool.scheduleWithFixedDelay(task, delay,
                autoChangeInterval, autoChangeTimeUnit);
            log.info("Scheduled " + this.plugin.getTaskMgr()
                .getServiceByImplementation(SetRandomBackgroundTask.class)
                .getTaskName());
        }
    }

    @SneakyThrows
    public synchronized void shutdown() {
        if (this.pool != null) {
            log.info("Shutdown scheduled tasks " + this.plugin.getTaskMgr()
                .getServiceByImplementation(SetRandomBackgroundTask.class)
                .getTaskName());

            this.pool.shutdown();
            if (!this.pool.awaitTermination(
                POOl_TERMINATE_TIMEOUT, TimeUnit.SECONDS)) {
                this.pool.shutdownNow();
            }
            this.pool = null;
        }
    }
}
