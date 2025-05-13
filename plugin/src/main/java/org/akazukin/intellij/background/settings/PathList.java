package org.akazukin.intellij.background.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AddEditRemovePanel;
import com.intellij.ui.ClickListener;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.utils.BundleUtils;
import org.akazukin.intellij.background.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The PathList class provides a graphical interface for managing a list
 * of file paths with their respective boolean statuses (enabled/disabled).
 * It extends AddEditRemovePanel to allow adding, editing, and removing items.
 * The class integrates functionalities such as file selection, item toggle, and data mapping.
 */
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class PathList extends AddEditRemovePanel<Pair<File, Boolean>> {
    public static final String INVALID_COLUMN_MESSAGE = "Invalid column index";
    private static final FileChooserDescriptor CHOOSER =
        new FileChooserDescriptor(true, true, false, false, false, false);

    static {
        CHOOSER.withFileFilter(f -> FileUtils.isValidImage(new File(f.getPath())));
    }

    VirtualFile defaultFile = null;

    public PathList() {
        super(getTableModel(), new ArrayList<>(),
            BundleUtils.message("settings.backgrounds.title"));
        this.getTable().setShowColumns(true);
        this.getTable().getColumnModel()
            .getColumn(1).setMaxWidth(75);


        new ClickListener() {
            @Override
            public boolean onClick(
                @NotNull final MouseEvent event, final int clickCount) {
                PathList.this.doClick(event.getButton());
                return true;
            }
        }.installOn(this.getTable());
    }

    private void doClick(final int button) {
        final int selected = this.getTable().getSelectedRow();
        if (selected >= 0) {
            final Pair<File, Boolean> o =
                this.clickItem(this.getData().get(selected), button);
            if (o != null) {
                this.getData().set(selected, o);
            }

            ((AbstractTableModel) this.getTable().getModel())
                .fireTableRowsUpdated(selected, selected);
        }
    }

    @Nullable
    private Pair<File, Boolean> clickItem(
        final Pair<File, Boolean> pair, final int button) {
        if (this.getTable().getSelectedColumn() == 1) {
            return new Pair<>(pair.getFirst(), !pair.getSecond());
        }
        return pair;
    }

    private static TableModel<Pair<File, Boolean>> getTableModel() {
        return new TableModel<>() {
            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            @NotNull
            public @NlsContexts.ColumnName String getColumnName(
                final int columnIndex) {
                final String id;
                switch (columnIndex) {
                    case 0:
                        id = "path";
                        break;
                    case 1:
                        id = "enable";
                        break;
                    default:
                        throw new IllegalArgumentException(
                            PathList.INVALID_COLUMN_MESSAGE);
                }
                return BundleUtils.message("settings.backgrounds." + id);
            }

            @Override
            public Object getField(
                final Pair<File, Boolean> o, final int columnIndex) {
                return columnIndex == 0 ? o.getFirst() : o.getSecond();
            }

            @Override
            public Class<?> getColumnClass(final int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return File.class;
                    case 1:
                        return Boolean.class;
                    default:
                        throw new IllegalArgumentException(
                            PathList.INVALID_COLUMN_MESSAGE);
                }
            }
        };
    }

    @Override
    protected @Nullable Pair<File, Boolean> addItem() {
        final VirtualFile virtualFile =
            FileChooser.chooseFile(CHOOSER, null, this.defaultFile);

        if (virtualFile == null) {
            return null;
        }

        this.defaultFile = virtualFile;
        return new Pair<>(new File(virtualFile.getPath()), true);
    }

    @Override
    protected boolean removeItem(final Pair<File, Boolean> o) {
        return true;
    }

    @Override
    protected @Nullable Pair<File, Boolean> editItem(
        final Pair<File, Boolean> pair) {
        if (this.getTable().getSelectedColumn() == 0) {
            final VirtualFile virtualFile =
                FileChooser.chooseFile(CHOOSER, null, this.defaultFile);

            if (virtualFile == null) {
                return pair;
            }

            this.defaultFile = virtualFile;
            return new Pair<>(
                new File(virtualFile.getPath()), pair.getSecond());
        }
        return pair;
    }

    public Map<File, Boolean> getDataAsMap() {
        final Map<File, Boolean> result = new LinkedHashMap<>();
        for (final Pair<File, Boolean> p : this.getData()) {
            result.put(p.getFirst(), p.getSecond());
        }
        return result;
    }

    public void setDataFromMap(final Map<File, Boolean> map) {
        final List<Pair<File, Boolean>> result = new ArrayList<>();
        for (final Map.Entry<File, Boolean> e : map.entrySet()) {
            result.add(Pair.create(e.getKey(), e.getValue()));
        }
        this.setData(result);
    }
}
