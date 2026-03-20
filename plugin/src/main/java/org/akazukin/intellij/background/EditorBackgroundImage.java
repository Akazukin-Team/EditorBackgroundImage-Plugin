package org.akazukin.intellij.background;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.extensions.PluginId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.cache.CacheManager;
import org.akazukin.intellij.background.cache.FileCache;
import org.akazukin.intellij.background.cache.ICache;
import org.akazukin.intellij.background.cache.ICacheManager;
import org.akazukin.intellij.background.cache.UrlCache;
import org.akazukin.intellij.background.listener.WebpDynamicPluginListenerImpl;
import org.akazukin.intellij.background.settings.CachedSettings;
import org.akazukin.intellij.background.settings.Config;
import org.akazukin.intellij.background.task.BackgroundScheduler;
import org.akazukin.intellij.background.task.TaskManager;
import org.akazukin.intellij.background.task.tasks.CacheBackgroundImagesTask;
import org.akazukin.intellij.background.task.tasks.ITask;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
import org.akazukin.service.registry.SingleServiceRegistry;
import org.akazukin.snowflake.config.SnowFlakeConfig;
import org.akazukin.snowflake.generator.AtomicSnowFlake;
import org.akazukin.snowflake.generator.ISnowFlake;
import org.akazukin.util.utils.FileUtils;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Slf4j
public final class EditorBackgroundImage {
    public static final String PLUGIN_NAME_SPACE = "Editor Background Image";
    public static final String PLUGIN_NAME = "EditorBackgroundImage";
    public static final String PLUGIN_ID_STRING = "editor_background_image";
    public static final String ACT_PLUGIN_ID_STRING =
        "org.akazukin.editorBackgroundImage";

    public static final PluginId ACT_PLUGIN_ID =
        PluginId.getId(ACT_PLUGIN_ID_STRING);

    public static final Path TEMP_DIR = Path.of(PathManager.getTempPath(), EditorBackgroundImage.ACT_PLUGIN_ID_STRING);


    final CachedSettings cachedSettings = new CachedSettings();
    final BackgroundScheduler scheduler = new BackgroundScheduler(this);
    final TaskManager taskMgr;
    final ICacheManager cacheMgr;
    final ISnowFlake snowflake;
    boolean enabled;

    {
        {
            final SingleServiceRegistry<ITask<?>> reg = new SingleServiceRegistry<>((Class<ITask<?>>) (Object) ITask.class);
            reg.registerService(new CacheBackgroundImagesTask(this));
            reg.registerService(new SetRandomBackgroundTask(this));
            this.taskMgr = new TaskManager(reg, this);
        }

        {
            final SingleServiceRegistry<ICache> reg = new SingleServiceRegistry<>(ICache.class);
            reg.registerService(new UrlCache());
            reg.registerService(new FileCache());
            this.cacheMgr = new CacheManager(reg);
        }

        this.snowflake = new AtomicSnowFlake(new SnowFlakeConfig(1735689600000L, 0L, (byte) 0, (byte) 22), 0);
    }

    public void onEnable() {
        log.info("Enabling " + PLUGIN_NAME_SPACE);

        try {
            FileUtils.deleteDirectory(EditorBackgroundImage.TEMP_DIR.toFile());
        } catch (final FileNotFoundException ignored) {
        }

        {
            final boolean webpSupported = PluginManager.isPluginInstalled(WebpDynamicPluginListenerImpl.PLUGIN_ID);
            this.cachedSettings.setWebpSupport(webpSupported);
        }

        final Config.State state = Config.getInstance();
        if (state.isAutoChangeEnabled()) {
            synchronized (this.scheduler) {
                if (!this.scheduler.isScheduled()) {
                    this.taskMgr.getServiceByInterfaceClass(
                            SetRandomBackgroundTask.class)
                        .get();
                    this.scheduler.schedule();
                }
            }
        }
        log.info("Enabled " + PLUGIN_NAME_SPACE);

        this.enabled = true;
    }

    public void onDisable() {
        log.info("Disabling " + PLUGIN_NAME_SPACE);
        this.scheduler.shutdown();

        try {
            FileUtils.deleteDirectory(EditorBackgroundImage.TEMP_DIR.toFile());
        } catch (final FileNotFoundException ignored) {
        }
        log.info("Disabled " + PLUGIN_NAME_SPACE);

        this.enabled = false;
    }
}
