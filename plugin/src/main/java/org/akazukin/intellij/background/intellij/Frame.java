package org.akazukin.intellij.background.intellij;

import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum Frame {
    EDITOR("Editor", IdeBackgroundUtil.EDITOR_PROP),
    FRAME("Frame", IdeBackgroundUtil.FRAME_PROP);

    String name;
    String prop;

    Frame(final String name, final String prop) {
        this.name = name;
        this.prop = prop;
    }
}
