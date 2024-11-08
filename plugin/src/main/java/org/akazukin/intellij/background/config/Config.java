package org.akazukin.intellij.background.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.PluginHandler;
import org.jetbrains.annotations.NotNull;

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
public final class Config implements PersistentStateComponent<Config.State>, Disposable {
    State state;

    @NotNull
    public static Config.State getInstance() {
        return ApplicationManager.getApplication().getService(Config.class).getState();
    }

    @Override
    public void loadState(@NotNull final State state) {
        this.state = state;
    }

    @Override
    public void initializeComponent() {
        if (state == null) {
            state = new State();
        }
    }

    @Override
    public void dispose() {
        state = null;

        PluginHandler.onUnload();
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class State {
        boolean changes;

        int intervalAmount;
        int intervalUnit = 1;

        boolean changeEditor;
        boolean changeFrame;

        boolean synchronizeImages;

        boolean hierarchicalExplore = false;
        int hierarchicalAmount = 3;

        Map<String, Boolean> images = new LinkedHashMap<>();
    }
}
