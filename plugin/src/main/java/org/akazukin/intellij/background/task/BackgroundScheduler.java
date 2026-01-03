package org.akazukin.intellij.background.task;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.bundle.BundleUtils;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.settings.Settings;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
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
    private static final long POOL_TERMINATE_TIMEOUT = 5;
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

        final SetRandomBackgroundTask randomBgTask = this.plugin.getTaskMgr()
            .getServiceByInterfaceClass(SetRandomBackgroundTask.class);
        log.info("Schedule " + randomBgTask.getTaskName());


        final ScheduledExecutorService pool
            = Executors.newSingleThreadScheduledExecutor();

        final Runnable task = () -> {
            log.debug("Changing background image by scheduler");
            try {
                for (int tries = 0, retries = state.getRetryTimes();
                     tries <= retries; tries++) {
                    if (randomBgTask.get()) {
                        return;
                    }

                    NotificationUtils.warning(
                        BundleUtils.getBundledMessage("messages.retry.title"),
                        BundleUtils.getBundledMessage("messages.retry.message",
                            retryInterval,
                            BundleUtils.getBundledMessage(
                                "settings.timeunit."
                                    + retryTimeUnit.name().toLowerCase())));
                    Thread.sleep(
                        retryTimeUnit.toMillis(retryInterval));
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                }
            } catch (final InterruptedException e) {
                synchronized (this) {
                    if (this.pool == pool) {
                        this.shutdown();
                    }
                }
                log.info("Interrupted while changing background image");
                throw new RuntimeException(e);
            } catch (final Throwable e) {
                log.error("Failed to change background image", e);
                throw e;
            }
            log.debug("Changed background image by scheduler");

            synchronized (this) {
                if (this.pool == pool) {
                    this.shutdown();
                }
            }
        };

        synchronized (this) {
            this.shutdown();
            this.pool = pool;
            this.pool.scheduleWithFixedDelay(task, delay,
                autoChangeInterval, autoChangeTimeUnit);
            log.debug("Scheduled " + randomBgTask.getTaskName());
        }
    }

    @SneakyThrows
    public synchronized void shutdown() {
        if (this.pool != null) {
            log.debug("Shutdown scheduled tasks");
            this.pool.shutdown();
            if (!this.pool.awaitTermination(
                POOL_TERMINATE_TIMEOUT, TimeUnit.SECONDS)) {
                this.pool.shutdownNow();
            }
            this.pool = null;
        }
    }
}
