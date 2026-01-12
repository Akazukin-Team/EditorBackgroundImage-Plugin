package org.akazukin.intellij.background.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AddEditRemovePanel;
import com.intellij.ui.ClickListener;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.bundle.BundleUtils;
import org.akazukin.intellij.background.utils.FileUtils;
import org.akazukin.util.object.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;
import java.util.ArrayList;

/**
 * The PathList class provides a graphical interface for managing a list
 * of file paths with their respective boolean statuses (enabled/disabled).
 * It extends AddEditRemovePanel to allow adding, editing, and removing items.
 * The class integrates functionalities such as file selection, item toggle, and data mapping.
 */
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class FilePathList extends AddEditRemovePanel<Pair<File, Boolean>> {
    public static final String INVALID_COLUMN_MESSAGE = "Invalid column index";
    @Serial
    private static final long serialVersionUID = 1L;
    private static final FileChooserDescriptor CHOOSER =
        new FileChooserDescriptor(true, true, false, false, false, false);

    static {
        CHOOSER.withFileFilter(f -> FileUtils.isValidImage(new File(f.getPath()), false));
    }

    VirtualFile defaultFile;

    public FilePathList() {
        super(getTableModel(), new ArrayList<>(),
            BundleUtils.getBundledMessage("settings.background.file.title"));
        final var table = this.getTable();
        table.setShowColumns(true);
        table.getColumnModel().getColumn(1).setMaxWidth(75);

        new ClickListener() {
            @Override
            public boolean onClick(
                @NotNull final MouseEvent event, final int clickCount) {
                FilePathList.this.doClick(event.getButton());
                return true;
            }
        }.installOn(table);
    }

    private void doClick(final int button) {
        final var table = this.getTable();
        final int selected = table.getSelectedRow();
        if (selected >= 0) {
            final var data = this.getData();
            final Pair<File, Boolean> o = this.clickItem(data.get(selected), button);
            if (o != null) {
                data.set(selected, o);
            }

            ((AbstractTableModel) table.getModel())
                .fireTableRowsUpdated(selected, selected);
        }
    }

    @Nullable
    private Pair<File, Boolean> clickItem(
        final Pair<File, Boolean> pair, final int button) {
        if (this.getTable().getSelectedColumn() == 1) {
            return new Pair<>(pair.getKey(), !pair.getValue());
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
                return switch (columnIndex) {
                    case 0 -> BundleUtils.getBundledMessage("settings.background.file.path");
                    case 1 -> BundleUtils.getBundledMessage("settings.backgrounds.enable");
                    default -> throw new IllegalArgumentException(
                        FilePathList.INVALID_COLUMN_MESSAGE);
                };
            }

            @Override
            public Object getField(
                final Pair<File, Boolean> o, final int columnIndex) {
                return columnIndex == 0 ? o.getKey() : o.getValue();
            }

            @Override
            public Class<?> getColumnClass(final int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> File.class;
                    case 1 -> Boolean.class;
                    default -> throw new IllegalArgumentException(
                        FilePathList.INVALID_COLUMN_MESSAGE);
                };
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
        if (this.getTable().getSelectedColumn() != 0) {
            return pair;
        }

        final VirtualFile virtualFile =
            FileChooser.chooseFile(CHOOSER, null, this.defaultFile);

        if (virtualFile == null) {
            return pair;
        }

        this.defaultFile = virtualFile;
        return new Pair<>(new File(virtualFile.getPath()), pair.getValue());
    }
}
