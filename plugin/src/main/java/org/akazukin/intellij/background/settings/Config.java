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
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@State(
    name = EditorBackgroundImage.PLUGIN_NAME + "Config",
    storages = {@Storage(
        roamingType = RoamingType.PER_OS,
        value = EditorBackgroundImage.PLUGIN_NAME + "Config.xml"
    )}
)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Config
    implements PersistentStateComponent<Config.State>, Disposable {
    State state;

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
        int retryIntervalUnit = 0;


        boolean changeEditor = true;
        boolean changeFrame = true;

        boolean synchronizeImages = true;

        boolean hierarchicalExplore = false;
        int hierarchicalDepth = 3;

        Map<String, Boolean> images = new LinkedHashMap<>();
    }
}
