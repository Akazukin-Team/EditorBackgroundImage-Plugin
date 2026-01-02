package org.akazukin.intellij.background.intellij;

import lombok.Getter;

import java.io.File;

@Getter
public class BackgroundData {
    Frame frame;
    File file;
    byte opacity;
    Position pos;
    Adjust adjust;

    public BackgroundData(final Frame frame, final File file) {
        this(frame, file, (byte) 15, Position.MIDDLE_CENTER, Adjust.STRETCH);
    }

    public BackgroundData(final Frame frame, final File file, final byte opacity, final Position pos, final Adjust adjust) {
        this.frame = frame;
        this.file = file;
        this.opacity = opacity;
        this.pos = pos;
        this.adjust = adjust;
    }
}
