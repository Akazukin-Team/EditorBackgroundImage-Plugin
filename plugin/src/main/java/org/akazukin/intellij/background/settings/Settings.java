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
import org.akazukin.intellij.background.bundle.BundleUtils;
import org.akazukin.intellij.background.bundle.Bundled;
import org.akazukin.intellij.background.intellij.Adjust;
import org.akazukin.intellij.background.intellij.Position;
import org.akazukin.intellij.background.task.tasks.CacheBackgroundImagesTask;
import org.akazukin.intellij.background.task.tasks.SetRandomBackgroundTask;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The Settings class provides configuration management and user interface elements
 * for controlling the behavior of the Editor Background Image plugin.
 * It includes options for automatic background change, retry settings,
 * and hierarchical exploration settings.
 * This class implements the Configurable interface for integration with the IDE's settings system.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Settings implements Configurable {
    public static final TimeUnit[] TIME_UNITS = {
        TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS
    };

    private static final int MAX_INTERVAL = 360;
    private static final int MAX_RETRIES = 50;
    private static final int MAX_DEPTH = 10;
    private static final int MAX_OPACITY = 100;
    private static final int MIN_OPACITY = 0;

    JPanel rootPanel;
    JCheckBox autoChangeEnableButton;
    JSpinner autoChangeIntervalSpinner;
    ComboBox<String> autoChangeTimeUnitBox;
    JCheckBox synchImgButton;
    JCheckBox editorButton;
    JCheckBox frameButton;
    JCheckBox hierarchicalButton;
    JSpinner hierarchicalSpinner;
    PathList backgroundsListPanel;
    ComboBox<String> retryTimeUnitBox;
    JSpinner retryIntervalSpinner;
    JCheckBox retryEnableButton;
    JSpinner retryTimesSpinner;
    JSpinner editorOpacity;
    JSpinner frameOpacity;
    ComboBox<Bundled<Adjust>> frameAdjust;
    ComboBox<Bundled<Adjust>> editorAdjust;
    ComboBox<Bundled<Position>> editorPos;
    ComboBox<Bundled<Position>> framePos;

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


        for (final TimeUnit e : TIME_UNITS) {
            final String msg = BundleUtils.getBundledMessage(
                "settings.timeunit." + e.name().toLowerCase());
            this.autoChangeTimeUnitBox.addItem(msg);
            this.retryTimeUnitBox.addItem(msg);
        }

        this.editorOpacity
            .setModel(new SpinnerNumberModel(MIN_OPACITY, MIN_OPACITY, MAX_OPACITY, 1));
        this.frameOpacity
            .setModel(new SpinnerNumberModel(MIN_OPACITY, MIN_OPACITY, MAX_OPACITY, 1));

        for (final Position e : Position.values()) {
            final Bundled<Position> msg = BundleUtils.getBundledMessage(e);
            this.editorPos.addItem(msg);
            this.framePos.addItem(msg);
        }

        for (final Adjust e : Adjust.values()) {
            final Bundled<Adjust> msg = BundleUtils.getBundledMessage(e);
            this.editorAdjust.addItem(msg);
            this.frameAdjust.addItem(msg);
        }


        this.hierarchicalButton.addActionListener(e ->
            this.hierarchicalSpinner
                .setEnabled(this.hierarchicalButton.isSelected()));
        this.hierarchicalSpinner
            .setModel(new SpinnerNumberModel(3, 1, MAX_DEPTH, 1));


        return this.rootPanel;
    }

    @Override
    public boolean isModified() {
        final Config.State state = Config.getInstance();

        final Set<Pair<File, Boolean>> bgImgs =
            state.getImages().entrySet().stream()
                .map(e ->
                    Pair.pair(new File(e.getKey()), e.getValue()))
                .collect(Collectors.toSet());

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
                || state.getEditorOpacity()
                != ((SpinnerNumberModel) this.editorOpacity.getModel()).getNumber().byteValue()
                || state.getEditorPos() != this.editorPos.getItem().getValue()
                || state.getEditorAdjust() != this.editorAdjust.getItem().getValue()

                || state.isChangeFrame() != this.frameButton.isSelected()
                || state.getFrameOpacity()
                != ((SpinnerNumberModel) this.frameOpacity.getModel()).getNumber().byteValue()
                || state.getFramePos() != this.framePos.getItem().getValue()
                || state.getFrameAdjust() != this.frameAdjust.getItem().getValue()


                || state.isSynchronizeImages()
                != this.synchImgButton.isSelected()


                || state.isHierarchicalExplore()
                != this.hierarchicalButton.isSelected()

                || state.getHierarchicalDepth()
                != ((SpinnerNumberModel) this.hierarchicalSpinner.getModel())
                .getNumber().intValue()


                || !bgImgs.equals(new HashSet<>(this.backgroundsListPanel.getData()));
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


        state.setSynchronizeImages(this.synchImgButton.isSelected());


        state.setChangeEditor(this.editorButton.isSelected());
        state.setEditorOpacity(
            ((SpinnerNumberModel) this.editorOpacity.getModel())
                .getNumber().byteValue());
        state.setEditorPos(((Bundled<Position>) this.editorPos.getSelectedItem()).getValue());
        state.setEditorAdjust(((Bundled<Adjust>) this.editorAdjust.getSelectedItem()).getValue());

        state.setChangeFrame(this.frameButton.isSelected());
        state.setFrameOpacity(
            ((SpinnerNumberModel) this.frameOpacity.getModel())
                .getNumber().byteValue());
        state.setFramePos(((Bundled<Position>) this.framePos.getSelectedItem()).getValue());
        state.setFrameAdjust(((Bundled<Adjust>) this.frameAdjust.getSelectedItem()).getValue());


        state.setHierarchicalExplore(this.hierarchicalButton.isSelected());
        state.setHierarchicalDepth(
            ((SpinnerNumberModel) this.hierarchicalSpinner.getModel())
                .getNumber().intValue());


        final Set<Pair<File, Boolean>> bgImgs =
            state.getImages().entrySet().stream()
                .map(e ->
                    Pair.pair(new File(e.getKey()), e.getValue()))
                .collect(Collectors.toSet());
        if (!bgImgs.equals(new HashSet<>(this.backgroundsListPanel.getData()))) {
            if (PluginHandler.isInitialized()) {
                PluginHandler.getPlugin().getTaskMgr()
                    .getServiceByInterfaceClass(CacheBackgroundImagesTask.class).get();
            }
        }

        @SuppressWarnings("unchecked") final Map.Entry<String, Boolean>[] panelRes = this.backgroundsListPanel.getData().stream()
            .map(e -> Map.entry(e.first.getAbsolutePath(), e.second))
            .toArray(Map.Entry[]::new);
        state.setImages(Map.ofEntries(panelRes));

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
        this.editorOpacity.setValue(state.getEditorOpacity());
        for (int i = 0; i < this.editorPos.getModel().getSize(); i++) {
            final Bundled<Position> e = this.editorPos.getItemAt(i);
            if (e.getValue() == state.getEditorPos()) {
                this.editorPos.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < this.editorAdjust.getModel().getSize(); i++) {
            final Bundled<Adjust> e = this.editorAdjust.getItemAt(i);
            if (e.getValue() == state.getEditorAdjust()) {
                this.editorAdjust.setSelectedIndex(i);
                break;
            }
        }

        this.frameButton.setSelected(state.isChangeFrame());
        this.frameOpacity.setValue(state.getFrameOpacity());
        for (int i = 0; i < this.framePos.getModel().getSize(); i++) {
            final Bundled<Position> e = this.framePos.getItemAt(i);
            if (e.getValue() == state.getFramePos()) {
                this.framePos.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < this.frameAdjust.getModel().getSize(); i++) {
            final Bundled<Adjust> e = this.frameAdjust.getItemAt(i);
            if (e.getValue() == state.getFrameAdjust()) {
                this.frameAdjust.setSelectedIndex(i);
                break;
            }
        }


        this.synchImgButton.setSelected(state.isSynchronizeImages());


        this.hierarchicalButton.setSelected(state.isHierarchicalExplore());

        this.hierarchicalSpinner.setValue(state.getHierarchicalDepth());
        this.hierarchicalSpinner.setEnabled(
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
            || PluginHandler.getPlugin().getCachedSettings().getImageCache().length == 0) {
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
                    .getServiceByInterfaceClass(SetRandomBackgroundTask.class).get();
            }

            if (this.editorButton.isSelected()
                || this.frameButton.isSelected()) {
                PluginHandler.getPlugin().getScheduler().schedule();
            }
        }
    }
}
