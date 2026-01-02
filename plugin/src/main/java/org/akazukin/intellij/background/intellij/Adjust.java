package org.akazukin.intellij.background.intellij;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.bundle.IBundlable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Adjust implements IBundlable {
    NONE("plain", "plain", "none"),
    STRETCH("scale", "scale", "stretch"),
    TILE("tile", "tile", "tile");

    @Getter
    String name;
    @Getter
    String value;
    String bundleId;

    Adjust(final String name, final String value, final String bundleId) {
        this.name = name;
        this.value = value;
        this.bundleId = bundleId;
    }

    public static Adjust fromValue(final String value) {
        for (final Adjust adjust : Adjust.values()) {
            if (adjust.getValue().equals(value)) {
                return adjust;
            }
        }
        return NONE;
    }

    @Override
    public String getBundleId() {
        return "settings.adjust." + this.bundleId;
    }
}
