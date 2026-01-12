package org.akazukin.intellij.background.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Getter
@Setter
public final class FileData {
    boolean enabled;
    AnalyzeData data;

    public FileData(final boolean enabled, final AnalyzeData data) {
        this.enabled = enabled;
        this.data = data;
    }

    @FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
    @Getter
    public static class AnalyzeData {
        boolean webpSupport;
        boolean valid;
        @Nullable
        File file;

        public AnalyzeData(final boolean webpSupport, final boolean valid, @Nullable final File file) {
            this.webpSupport = webpSupport;
            this.valid = valid;
            this.file = file;
        }
    }
}
