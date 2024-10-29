package org.akazukin.intellij.background;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import java.io.File;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import lombok.Getter;
import lombok.Setter;
import org.akazukin.intellij.background.gui.Settings;
import org.akazukin.intellij.background.tasks.BackgroundScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EditorBackgroundImage implements ProjectActivity {
    public static final String PLUGIN_NAME_SPACE = "Editor Background Image";
    public static final String PLUGIN_NAME = "EditorBackgroundImage";
    public static final String PLUGIN_ID = "editor_background_image";
    public static final String PLUGIN_VERSION = "1.0.0";

    @Getter
    @Setter
    private static File[] imageCache;

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        PropertiesComponent props = PropertiesComponent.getInstance();
        if (props.getBoolean(Settings.CHANGE_EVERY, false)) {
            BackgroundScheduler.schedule();
        }
        return null;
    }
}
