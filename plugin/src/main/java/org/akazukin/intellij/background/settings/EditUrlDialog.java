// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.akazukin.intellij.background.settings;

import com.intellij.openapi.ui.DialogWrapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.bundle.BundleUtils;
import org.akazukin.util.object.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditUrlDialog extends DialogWrapper {
    JPanel contentPane;
    JTextField urlField;
    JCheckBox checkBox;

    public EditUrlDialog() {
        this(null);
    }

    public EditUrlDialog(@Nullable final Pair<URL, Boolean> value) {
        super(false);
        this.setTitle(BundleUtils.getBundledMessage("dialog.url.title"));

        if (value != null) {
            this.urlField.setText(value.getKey().toExternalForm());
            this.checkBox.getModel().setSelected(value.getValue());
        }

        //this.installPropertySelectionListener();

        this.init();
    }

    public Pair<URL, Boolean> getValue() throws MalformedURLException {
        return new Pair<>(URI.create(this.urlField.getText()).toURL(), this.checkBox.isSelected());
    }

    /*
        private void installPropertySelectionListener() {
            this.urlField.addItemListener((ItemListener) e -> {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                final String key = (String) e.getItem();
                final String value = this.myAvailableProperties.get(key);
                if (value != null) {
                    this.myValueField.setText(value);
                }
            });
        }
    */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.contentPane;
    }

    @Override
    protected String getDimensionServiceKey() {
        return this.getClass().getName();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.urlField;
    }
}
