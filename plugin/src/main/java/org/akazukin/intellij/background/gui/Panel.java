package org.akazukin.intellij.background.gui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AddEditRemovePanel;
import com.intellij.ui.ClickListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Setter
public final class Panel extends AddEditRemovePanel<Pair<File, Boolean>> {
    public static final String INVALID_COLUMN_MESSAGE = "Invalid column index";
    private VirtualFile defaultFile = null;

    public Panel() {
        super(getTableModel(), new ArrayList<>(), "Backgrounds");
        getTable().setShowColumns(true);
        getTable().getColumnModel().getColumn(0).setPreferredWidth(575);


        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent event, int clickCount) {
                doClick(event.getButton());
                return true;
            }
        }.installOn(getTable());
    }

    private void doClick(int button) {
        int selected = getTable().getSelectedRow();
        if (selected >= 0) {
            Pair<File, Boolean> o = clickItem(getData().get(selected), button);
            if (o != null) {
                getData().set(selected, o);
            }

            ((AbstractTableModel) getTable().getModel()).fireTableRowsUpdated(selected, selected);
        }
    }

    @Nullable
    private Pair<File, Boolean> clickItem(Pair<File, Boolean> pair, int button) {
        if (getTable().getSelectedColumn() == 1) {
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
            public @NlsContexts.ColumnName String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return "Path";
                    case 1:
                        return "Enabled";
                    default:
                        throw new IllegalArgumentException(INVALID_COLUMN_MESSAGE);
                }
            }

            @Override
            public Object getField(Pair<File, Boolean> o, int columnIndex) {
                return columnIndex == 0 ? o.getFirst() : o.getSecond();
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return File.class;
                    case 1:
                        return Boolean.class;
                    default:
                        throw new IllegalArgumentException(INVALID_COLUMN_MESSAGE);
                }
            }
        };
    }

    @Override
    protected @Nullable Pair<File, Boolean> addItem() {
        VirtualFile virtualFile = FileChooser.chooseFile(new FileChooserDescriptor(true, true, false, false, false, false), null, defaultFile);
        if (virtualFile == null) {
            return null;
        }

        File file = new File(virtualFile.getPath());
        defaultFile = virtualFile;
        return new Pair<>(file, true);
    }

    @Override
    protected boolean removeItem(Pair<File, Boolean> o) {
        return true;
    }

    @Override
    protected @Nullable Pair<File, Boolean> editItem(Pair<File, Boolean> pair) {
        if (getTable().getSelectedColumn() == 0) {
            VirtualFile virtualFile = FileChooser.chooseFile(new FileChooserDescriptor(true, true, false, false, false, false), null, defaultFile);
            if (virtualFile == null) {
                return pair;
            }

            File file = new File(virtualFile.getPath());
            defaultFile = virtualFile;
            return new Pair<>(file, pair.getSecond());
        } else {
            //return new Pair<>(pair.getFirst(), !pair.getSecond());
            return pair;
        }
    }

    public Map<File, Boolean> getDataAsMap() {
        Map<File, Boolean> result = new LinkedHashMap<>();
        for (Pair<File, Boolean> p : getData()) {
            result.put(p.getFirst(), p.getSecond());
        }
        return result;
    }

    public void setDataFromMap(Map<File, Boolean> map) {
        List<Pair<File, Boolean>> result = new ArrayList<>();
        for (Map.Entry<File, Boolean> e : map.entrySet()) {
            result.add(Pair.create(e.getKey(), e.getValue()));
        }
        setData(result);
    }
}
