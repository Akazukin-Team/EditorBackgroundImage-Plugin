package org.akazukin.intellij.background.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.task.tasks.CacheBackgroundImagesTask;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
import org.akazukin.intellij.background.utils.BundleUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The Settings class provides configuration management and user interface elements
 * for controlling the behavior of the Editor Background Image plugin.
 * It includes options for automatic background change, retry settings,
 * and hierarchical exploration settings.
 * This class implements the Configurable interface for integration with the IDE's settings system.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Settings implements Configurable {
    public static final TimeUnit[] TIME_UNITS = new TimeUnit[]{
        TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS
    };

    private static final int MAX_INTERVAL = 360;
    private static final int MAX_RETRIES = 50;
    private static final int MAX_DEPTH = 10;

    JPanel rootPanel;
    JCheckBox autoChangeEnableButton;
    JSpinner autoChangeIntervalSpinner;
    ComboBox<String> autoChangeTimeUnitBox;
    JCheckBox synchronizeImageButton;
    JCheckBox editorButton;
    JCheckBox frameButton;
    JCheckBox hierarchicalButton;
    JSpinner hierarchialSpinner;
    PathList backgroundsListPanel;
    private JPanel autoChangePanel;
    private ComboBox<String> retryTimeUnitBox;
    private JSpinner retryIntervalSpinner;
    private JCheckBox retryEnableButton;
    private JSpinner retryTimesSpinner;

    @Override
    public String getDisplayName() {
        return EditorBackgroundImage.PLUGIN_NAME_SPACE;
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        if (PluginHandler.isInitialized()) {
            PluginHandler.getPlugin().getScheduler().shutdown();
        }

        this.autoChangeEnableButton.addActionListener(e -> {
            this.autoChangeIntervalSpinner
                .setEnabled(this.autoChangeEnableButton.isSelected());
            this.autoChangeTimeUnitBox
                .setEnabled(this.autoChangeEnableButton.isSelected());
        });
        this.autoChangeIntervalSpinner
            .setModel(new SpinnerNumberModel(1, 1, MAX_INTERVAL, 2));


        this.retryEnableButton.addActionListener(e -> {
            this.retryTimesSpinner
                .setEnabled(this.retryEnableButton.isSelected());
            this.retryIntervalSpinner
                .setEnabled(this.retryEnableButton.isSelected());
            this.retryTimeUnitBox
                .setEnabled(this.retryEnableButton.isSelected());
        });
        this.retryTimesSpinner
            .setModel(new SpinnerNumberModel(1, 1, MAX_RETRIES, 1));
        this.retryIntervalSpinner
            .setModel(new SpinnerNumberModel(1, 1, MAX_INTERVAL, 2));


        for (final TimeUnit timeUnit : TIME_UNITS) {
            final String msg = BundleUtils.message(
                "settings.timeunit." + timeUnit.name().toLowerCase());
            this.autoChangeTimeUnitBox.addItem(msg);
            this.retryTimeUnitBox.addItem(msg);
        }

        this.hierarchicalButton.addActionListener(e ->
            this.hierarchialSpinner
                .setEnabled(this.hierarchicalButton.isSelected()));
        this.hierarchialSpinner
            .setModel(new SpinnerNumberModel(3, 1, MAX_DEPTH, 1));


        return this.rootPanel;
    }

    @Override
    public boolean isModified() {
        final Config.State state = Config.getInstance();

        final List<Pair<File, Boolean>> bgImgs =
            state.getImages().entrySet().stream()
                .map(e ->
                    Pair.pair(new File(e.getKey()), e.getValue())).toList();

        return
            state.isAutoChangeEnabled()
                != this.autoChangeEnableButton.isSelected()

                || state.getAutoChangeIntervalAmount()
                != ((SpinnerNumberModel) this.autoChangeIntervalSpinner
                .getModel()).getNumber().intValue()

                || state.getAutoChangeIntervalUnit()
                != this.autoChangeTimeUnitBox.getSelectedIndex()


                || state.isRetryEnabled() != this.retryEnableButton.isSelected()

                || state.getRetryTimes()
                != ((SpinnerNumberModel) this.retryTimesSpinner.getModel())
                .getNumber().intValue()

                || state.getRetryIntervalAmount()
                != ((SpinnerNumberModel) this.retryIntervalSpinner.getModel())
                .getNumber().intValue()

                || state.getRetryIntervalUnit() != this.retryTimeUnitBox
                .getSelectedIndex()


                || state.isChangeEditor() != this.editorButton.isSelected()
                || state.isChangeFrame() != this.frameButton.isSelected()


                || state.isSynchronizeImages()
                != this.synchronizeImageButton.isSelected()


                || state.isHierarchicalExplore()
                != this.hierarchicalButton.isSelected()

                || state.getHierarchicalDepth()
                != ((SpinnerNumberModel) this.hierarchialSpinner.getModel())
                .getNumber().intValue()


                || !new HashSet<>(bgImgs)
                .containsAll(this.backgroundsListPanel.getData())

                || !new HashSet<>(this.backgroundsListPanel.getData())
                .containsAll(bgImgs);
    }

    @Override
    public void apply() {
        final Config.State state = Config.getInstance();


        state.setAutoChangeEnabled(this.autoChangeEnableButton.isSelected());
        state.setAutoChangeIntervalAmount(
            ((SpinnerNumberModel) this.autoChangeIntervalSpinner.getModel())
                .getNumber().intValue());
        state.setAutoChangeIntervalUnit(
            this.autoChangeTimeUnitBox.getSelectedIndex());


        state.setRetryEnabled(this.retryEnableButton.isSelected());
        state.setRetryTimes(
            ((SpinnerNumberModel) this.retryTimesSpinner.getModel())
                .getNumber().intValue());
        state.setRetryIntervalAmount(
            ((SpinnerNumberModel) this.retryIntervalSpinner.getModel())
                .getNumber().intValue());
        state.setRetryIntervalUnit(
            this.retryTimeUnitBox.getSelectedIndex());


        state.setSynchronizeImages(this.synchronizeImageButton.isSelected());


        state.setChangeEditor(this.editorButton.isSelected());
        state.setChangeFrame(this.frameButton.isSelected());


        state.setHierarchicalExplore(this.hierarchicalButton.isSelected());
        state.setHierarchicalDepth(
            ((SpinnerNumberModel) this.hierarchialSpinner.getModel())
                .getNumber().intValue());


        final List<Pair<File, Boolean>> bgImgs =
            state.getImages().entrySet().stream()
                .map(e ->
                    Pair.pair(new File(e.getKey()), e.getValue())).toList();
        if (!new HashSet<>(bgImgs)
            .containsAll(this.backgroundsListPanel.getData())

            || !new HashSet<>(this.backgroundsListPanel.getData())
            .containsAll(bgImgs)) {

            if (PluginHandler.isInitialized()) {
                PluginHandler.getPlugin().getTaskMgr()
                    .getServiceByImplementation(CacheBackgroundImagesTask.class).get();
            }
        }
        state.setImages(
            Map.ofEntries(this.backgroundsListPanel.getData().stream()
                .map(e -> Map.entry(e.first.getAbsolutePath(), e.second))
                .toArray(Map.Entry[]::new))
        );

        this.autoChangeIntervalSpinner
            .setEnabled(this.autoChangeEnableButton.isSelected());
        this.autoChangeTimeUnitBox
            .setEnabled(this.autoChangeEnableButton.isSelected());

        this.retryTimesSpinner
            .setEnabled(this.retryEnableButton.isSelected());
        this.retryIntervalSpinner
            .setEnabled(this.retryEnableButton.isSelected());
        this.retryTimeUnitBox
            .setEnabled(this.retryEnableButton.isSelected());
    }

    @Override
    public void reset() {
        final Config.State state = Config.getInstance();

        this.autoChangeEnableButton.setSelected(state.isAutoChangeEnabled());

        this.autoChangeIntervalSpinner
            .setValue(state.getAutoChangeIntervalAmount());
        this.autoChangeIntervalSpinner
            .setEnabled(this.autoChangeEnableButton.isSelected());

        this.autoChangeTimeUnitBox
            .setSelectedIndex(state.getAutoChangeIntervalUnit());
        this.autoChangeTimeUnitBox
            .setEnabled(this.autoChangeEnableButton.isSelected());


        this.retryEnableButton.setSelected(state.isRetryEnabled());

        this.retryTimesSpinner
            .setValue(state.getRetryTimes());
        this.retryTimesSpinner
            .setEnabled(this.retryEnableButton.isSelected());

        this.retryIntervalSpinner
            .setValue(state.getRetryIntervalAmount());
        this.retryIntervalSpinner
            .setEnabled(this.retryEnableButton.isSelected());

        this.retryTimeUnitBox
            .setSelectedIndex(state.getRetryIntervalUnit());
        this.retryTimeUnitBox
            .setEnabled(this.retryEnableButton.isSelected());


        this.editorButton.setSelected(state.isChangeEditor());
        this.frameButton.setSelected(state.isChangeFrame());


        this.synchronizeImageButton.setSelected(state.isSynchronizeImages());


        this.hierarchicalButton.setSelected(state.isHierarchicalExplore());

        this.hierarchialSpinner.setValue(state.getHierarchicalDepth());
        this.hierarchialSpinner.setEnabled(
            this.hierarchicalButton.isSelected());


        final List<Pair<File, Boolean>> bgImgs =
            new ArrayList<>(state.getImages().entrySet().stream()
                .map(e ->
                    Pair.pair(new File(e.getKey()), e.getValue()))
                .toList());
        this.backgroundsListPanel.setData(bgImgs);
    }

    @Override
    public void disposeUIResources() {
        if (!PluginHandler.isLoaded()
            || !PluginHandler.isEnabled()
            || PluginHandler.getPlugin().getImageCache() == null
            || PluginHandler.getPlugin().getImageCache().length == 0) {
            return;
        }

        if (this.autoChangeEnableButton.isSelected()) {
            final PropertiesComponent props = PropertiesComponent.getInstance();
            if ((
                this.editorButton.isSelected()
                    && !props.isValueSet(IdeBackgroundUtil.EDITOR_PROP))
                || (
                this.frameButton.isSelected()
                    && !props.isValueSet(IdeBackgroundUtil.FRAME_PROP))) {

                PluginHandler.getPlugin().getTaskMgr()
                    .getServiceByImplementation(SetRandomBackgroundTask.class).get();
            }

            if (this.editorButton.isSelected()
                || this.frameButton.isSelected()) {
                PluginHandler.getPlugin().getScheduler().schedule();
            }
        }
    }
}
