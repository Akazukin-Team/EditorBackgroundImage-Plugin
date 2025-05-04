package org.akazukin.intellij.background.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.PluginHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public final class ProjectActivityImpl implements ProjectActivity {
    @Override
    public @Nullable Object execute(
        @NotNull final Project project,
        @NotNull final Continuation<? super Unit> continuation) {
        PluginHandler.onStartup();
        return null;
    }
}
