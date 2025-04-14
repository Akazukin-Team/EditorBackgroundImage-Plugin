package org.akazukin.intellij.background.gui;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.config.Config;
import org.akazukin.intellij.background.task.CacheBackgroundImagesTask;
import org.akazukin.intellij.background.task.SetRandomBackgroundTask;
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

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Settings implements Configurable {
    public static final TimeUnit[] TIME_UNITS = new TimeUnit[]{
        TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS
    };

    private static final int MAX_TIME = 360;
    private static final int MAX_DEPTH = 10;

    JPanel rootPanel;
    JCheckBox changeEveryButton;
    JSpinner intervalSpinner;
    ComboBox<String> timeUnitBox;
    JCheckBox synchronizeImageButton;
    JCheckBox editorButton;
    JCheckBox frameButton;
    JCheckBox hierarchicalButton;
    JSpinner hierarchialSpinner;
    Panel backgroundsListPanel;

    @Override
    public String getDisplayName() {
        return EditorBackgroundImage.PLUGIN_NAME_SPACE;
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        if (PluginHandler.isLoaded()) {
            PluginHandler.getPlugin().getScheduler().shutdown();
        }

        this.intervalSpinner
            .setModel(new SpinnerNumberModel(0, 0, MAX_TIME, 2));
        this.changeEveryButton.addActionListener(e -> {
            this.intervalSpinner
                .setEnabled(this.changeEveryButton.isSelected());
            this.timeUnitBox.setEnabled(this.changeEveryButton.isSelected());
        });

        for (final TimeUnit timeUnit : TIME_UNITS) {
            this.timeUnitBox.addItem(BundleUtils.message(
                "settings.change.timeunit." + timeUnit.name().toLowerCase()));
        }


        this.editorButton.setText(IdeBundle.message("toggle.editor.and.tools"));
        this.frameButton.setText(IdeBundle.message("toggle.empty.frame"));


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
            state.getIntervalAmount()
                != ((SpinnerNumberModel) this.intervalSpinner.getModel())
                .getNumber().intValue()


                || state.getIntervalUnit() != this.timeUnitBox
                .getSelectedIndex()


                || state.isChangeEditor() != this.editorButton.isSelected()
                || state.isChangeFrame() != this.frameButton.isSelected()


                || state.isChanges() != this.changeEveryButton.isSelected()

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

        state.setIntervalAmount(
            ((SpinnerNumberModel) this.intervalSpinner.getModel())
                .getNumber().intValue());
        state.setChanges(this.changeEveryButton.isSelected());

        state.setSynchronizeImages(this.synchronizeImageButton.isSelected());
        state.setIntervalUnit(this.timeUnitBox.getSelectedIndex());

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

            if (PluginHandler.isLoaded()) {
                PluginHandler.getPlugin().getTaskMgr()
                    .getTask(CacheBackgroundImagesTask.class).get();
            }
        }
        state.setImages(
            Map.ofEntries(this.backgroundsListPanel.getData().stream()
                .map(e -> Map.entry(e.first.getAbsolutePath(), e.second))
                .toArray(Map.Entry[]::new))
        );

        this.intervalSpinner.setEnabled(this.changeEveryButton.isSelected());
        this.timeUnitBox.setEnabled(this.changeEveryButton.isSelected());
    }

    @Override
    public void reset() {
        final Config.State state = Config.getInstance();

        this.changeEveryButton.setSelected(state.isChanges());

        this.intervalSpinner.setValue(state.getIntervalAmount());
        this.intervalSpinner.setEnabled(this.changeEveryButton.isSelected());


        this.timeUnitBox.setSelectedIndex(state.getIntervalUnit());
        this.timeUnitBox.setEnabled(this.changeEveryButton.isSelected());


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
            || PluginHandler.getPlugin().getImageCache() == null) {
            return;
        }

        if (this.changeEveryButton.isSelected()) {
            final PropertiesComponent props = PropertiesComponent.getInstance();
            if ((
                this.editorButton.isSelected()
                    && !props.isValueSet(IdeBackgroundUtil.EDITOR_PROP))
                || (
                this.frameButton.isSelected()
                    && !props.isValueSet(IdeBackgroundUtil.FRAME_PROP))) {

                PluginHandler.getPlugin().getTaskMgr()
                    .getTask(SetRandomBackgroundTask.class).get();
            }

            if (this.editorButton.isSelected() || this.frameButton.isSelected()) {
                PluginHandler.getPlugin().getScheduler().schedule();
            }
        }
    }
}
