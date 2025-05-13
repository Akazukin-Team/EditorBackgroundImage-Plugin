package org.akazukin.intellij.background.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.akazukin.intellij.background.PluginHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of the ProjectActivity interface
 * used to handle actions triggered during the startup of an IntelliJ project.
 * This class ensures the activation of the necessary plugin parts.
 * <p>
 * The execute method invokes the PluginHandler's startup logic to initialize
 * or enable the associated plugin.
 */
public final class ProjectActivityImpl implements ProjectActivity {
    @Override
    public @Nullable Object execute(
        @NotNull final Project project,
        @NotNull final Continuation<? super Unit> continuation) {
        PluginHandler.onStartup();
        return null;
    }
}
