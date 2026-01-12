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
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.intellij.Adjust;
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
        return ApplicationManager.getApplication()
            .getService(Config.class).getState();
    }

    @Override
    public void loadState(@NotNull final State state) {
        this.state = state;
    }

    @Override
    public void initializeComponent() {
        if (this.state == null) {
            this.state = new State();
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
    }
}
