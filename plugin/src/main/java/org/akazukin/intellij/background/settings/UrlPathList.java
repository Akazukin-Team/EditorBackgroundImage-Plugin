package org.akazukin.intellij.background.settings;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.AddEditRemovePanel;
import com.intellij.ui.ClickListener;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.bundle.BundleUtils;
import org.akazukin.util.object.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;
import java.net.URL;
import java.util.ArrayList;

/**
 * The PathList class provides a graphical interface for managing a list
 * of file paths with their respective boolean statuses (enabled/disabled).
 * It extends AddEditRemovePanel to allow adding, editing, and removing items.
 * The class integrates functionalities such as file selection, item toggle, and data mapping.
 */
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class UrlPathList extends AddEditRemovePanel<Pair<URL, Boolean>> {
    public static final String INVALID_COLUMN_MESSAGE = "Invalid column index";
    @Serial
    private static final long serialVersionUID = 1L;

    public UrlPathList() {
        super(getTableModel(), new ArrayList<>(),
            BundleUtils.getBundledMessage("settings.background.file.title"));
        final var table = this.getTable();
        table.setShowColumns(true);
        table.getColumnModel().getColumn(1).setMaxWidth(75);

        new ClickListener() {
            @Override
            public boolean onClick(
                @NotNull final MouseEvent event, final int clickCount) {
                UrlPathList.this.doClick(event.getButton());
                return true;
            }
        }.installOn(table);
    }

    private void doClick(final int button) {
        final var table = this.getTable();
        final int selected = table.getSelectedRow();
        if (selected >= 0) {
            final var data = this.getData();
            final Pair<URL, Boolean> o = this.clickItem(data.get(selected), button);
            if (o != null) {
                data.set(selected, o);
            }

            ((AbstractTableModel) table.getModel())
                .fireTableRowsUpdated(selected, selected);
        }
    }

    @Nullable
    private Pair<URL, Boolean> clickItem(
        final Pair<URL, Boolean> pair, final int button) {
        if (this.getTable().getSelectedColumn() == 1) {
            return new Pair<>(pair.getKey(), !pair.getValue());
        }
        return pair;
    }

    private static TableModel<Pair<URL, Boolean>> getTableModel() {
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
                    case 0 -> BundleUtils.getBundledMessage("settings.background.url.path");
                    case 1 -> BundleUtils.getBundledMessage("settings.backgrounds.enable");
                    default -> throw new IllegalArgumentException(
                        UrlPathList.INVALID_COLUMN_MESSAGE);
                };
            }

            @Override
            public Object getField(
                final Pair<URL, Boolean> o, final int columnIndex) {
                return columnIndex == 0 ? o.getKey() : o.getValue();
            }

            @Override
            public Class<?> getColumnClass(final int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> File.class;
                    case 1 -> Boolean.class;
                    default -> throw new IllegalArgumentException(
                        UrlPathList.INVALID_COLUMN_MESSAGE);
                };
            }
        };
    }

    @SneakyThrows
    @Override
    protected @Nullable Pair<URL, Boolean> addItem() {
        final EditUrlDialog d = new EditUrlDialog();
        if (!d.showAndGet()) {
            return null;
        }
        return d.getValue();
    }

    @Override
    protected boolean removeItem(final Pair<URL, Boolean> o) {
        return true;
    }

    @Override
    @Nullable
    @SneakyThrows
    protected Pair<URL, Boolean> editItem(
        final Pair<URL, Boolean> pair) {
        if (this.getTable().getSelectedColumn() != 0) {
            return pair;
        }

        final EditUrlDialog d = new EditUrlDialog(pair);
        if (!d.showAndGet()) {
            return null;
        }
        return d.getValue();
    }
}
