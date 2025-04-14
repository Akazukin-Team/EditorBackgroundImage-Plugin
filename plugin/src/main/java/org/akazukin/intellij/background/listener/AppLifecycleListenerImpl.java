package org.akazukin.intellij.background.listener;

import com.intellij.ide.AppLifecycleListener;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.PluginHandler;

@Slf4j
public class AppLifecycleListenerImpl implements AppLifecycleListener {
    @Override
    public void appStarted() {
        PluginHandler.init();
    }
}
