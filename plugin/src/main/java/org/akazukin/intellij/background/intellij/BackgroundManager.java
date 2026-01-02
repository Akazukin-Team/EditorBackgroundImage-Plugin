package org.akazukin.intellij.background.intellij;

import com.intellij.ide.util.PropertiesComponent;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class BackgroundManager {
    public BackgroundData getBackground(final Frame frame) {
        final String prop = frame.getProp();
        final String values = PropertiesComponent.getInstance().getValue(prop);
        if (values == null) {
            return null;
        }
        final String[] split = values.split(",");
        if (split.length == 1) {
            return new BackgroundData(frame, new File(split[0]));
        }
        return new BackgroundData(frame, new File(split[0]), (byte) Integer.parseInt(split[1]), Position.fromValue(split[3]), Adjust.fromValue(split[2]));
    }

    public void setBackground(final Frame frane, final File path, final byte opacity, final Adjust adjust, final Position pos) {
        PropertiesComponent.getInstance().setValue(frane.getProp(), path.getAbsolutePath() + "," + opacity + "," + adjust.getValue() + "," + pos.getValue());
    }
}
