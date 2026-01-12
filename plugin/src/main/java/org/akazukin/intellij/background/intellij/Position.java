package org.akazukin.intellij.background.intellij;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.intellij.background.bundle.IBundlable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Position implements IBundlable {
    TOP_LEFT("top-left", "top_left", "top_left"),
    TOP_CENTER("top-center", "top_center", "top_center"),
    TOP_RIGHT("top-right", "top_right", "top_right"),
    MIDDLE_LEFT("middle-left", "middle_left", "middle_left"),
    MIDDLE_CENTER("middle-center", "middle_center", "center"),
    MIDDLE_RIGHT("middle-right", "middle_right", "middle_right"),
    BOTTOM_LEFT("bottom-left", "bottom_left", "bottom_left"),
    BOTTOM_CENTER("bottom-center", "bottom_center", "bottom_center"),
    BOTTOM_RIGHT("bottom-right", "bottom_right", "bottom_right");

    @Getter
    String name;
    String bundleId;
    @Getter
    String value;

    Position(final String name, final String bundleId, final String value) {
        this.name = name;
        this.bundleId = bundleId;
        this.value = value;
    }

    public static Position fromValue(final String value) {
        for (final Position position : Position.values()) {
            if (position.getValue().equals(value)) {
                return position;
            }
        }
        return null;
    }

    @Override
    public String getBundleId() {
        return "settings.position." + this.bundleId;
    }
}
