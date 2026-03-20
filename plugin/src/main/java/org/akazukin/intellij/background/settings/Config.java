package org.akazukin.intellij.background.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.intellij.Adjust;
import org.akazukin.intellij.background.intellij.BackgroundData;
import org.akazukin.intellij.background.intellij.BackgroundManager;
import org.akazukin.intellij.background.intellij.Frame;
import org.akazukin.intellij.background.intellij.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the configuration settings for the EditorBackgroundImage plugin.
 * This configuration is persistent across application restarts
 * and is stored in an XML file.
 */
@Service
@State(name = EditorBackgroundImage.PLUGIN_NAME + "Config", storages = @Storage(
    roamingType = RoamingType.PER_OS,
    value = EditorBackgroundImage.PLUGIN_NAME + "Config.xml"
))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Slf4j
public final class Config
    implements PersistentStateComponent<Config.State>, Disposable {
    @Nullable
    State state;

    /**
     * Retrieves the persistent configuration state instance associated with the EditorBackgroundImage plugin.
     * This state contains configurable settings relevant to the plugin's behavior and functionality.
     *
     * @return the {@link Config.State} instance representing the saved configuration of the plugin.
     * The state object can be modified or queried to adjust the plugin's settings.
     */
    @NotNull
    public static Config.State getInstance() {
        return getComponent().getState();
    }

    /**
     * Retrieves the {@link Config} component instance, which serves as the core
     * configuration service for the EditorBackgroundImage plugin.
     * This method resolves the application-level service through the {@link ApplicationManager}.
     *
     * @return the {@link Config} instance representing the plugin's main service component.
     * The returned instance is used to access the plugin's state or manage its lifecycle.
     */
    @NotNull
    public static Config getComponent() {
        return ApplicationManager.getApplication()
            .getService(Config.class);
    }

    @Override
    public void loadState(@NotNull final State state) {
        this.state = state;
    }

    @Override
    public synchronized void initializeComponent() {
        if (this.state == null) {
            this.state = new State();
        }
    }

    public synchronized void apply() {
        for (final Frame f : Frame.values()) {
            final BackgroundData data = BackgroundManager.getBackground(f);
            if (data == null) {
                continue;
            }
            log.info("Applying background for {}", f.getName());

            final int opacity;
            final Adjust adjust;
            final Position pos;
            if (f == Frame.EDITOR) {
                opacity = this.state.getEditorOpacity();
                adjust = this.state.getEditorAdjust();
                pos = this.state.getEditorPos();
            } else {
                opacity = this.state.getFrameOpacity();
                adjust = this.state.getFrameAdjust();
                pos = this.state.getFramePos();
            }

            BackgroundManager.setBackground(f, data.getFile(), (byte) opacity, adjust, pos);
        }
    }

    @Override
    public void dispose() {
        this.state = null;
    }

    @Data
    public static class State {
        boolean autoChangeEnabled = true;

        int autoChangeIntervalAmount = 1;
        int autoChangeIntervalUnit = 1;


        boolean retryEnabled = true;

        int retryTimes = 10;

        int retryIntervalAmount = 30;
        int retryIntervalUnit;


        boolean changeEditor = true;
        int editorOpacity = 15;
        Position editorPos = Position.MIDDLE_CENTER;
        Adjust editorAdjust = Adjust.STRETCH;

        boolean changeFrame = true;
        int frameOpacity = 15;
        Position framePos = Position.MIDDLE_CENTER;
        Adjust frameAdjust = Adjust.STRETCH;

        boolean synchronizeImages = true;

        boolean hierarchicalExplore;
        int hierarchicalDepth = 3;

        Map<String, Boolean> images = new LinkedHashMap<>();
        Map<String, Boolean> imageUrls = new LinkedHashMap<>();
    }
}
