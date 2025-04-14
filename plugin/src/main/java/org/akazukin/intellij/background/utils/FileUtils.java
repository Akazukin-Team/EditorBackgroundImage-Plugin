package org.akazukin.intellij.background.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public final class FileUtils {
    public static final File[] EMPTY_FILES = new File[0];
    private static final MimetypesFileTypeMap FILE_TYPE_MAP
        = new MimetypesFileTypeMap();

    public static File[] collectFiles(
        @NotNull final File directory, final int depth) {
        if (directory.isFile()) {
            throw new IllegalArgumentException("directory is not a file");
        }
        if (depth < 0) {
            throw new IllegalArgumentException("depth is not positive");
        }

        final File[] files = directory.listFiles();
        if (depth == 0) {
            return files;
        }

        final List<File> files2 = new ArrayList<>();
        for (final File file : files) {
            if (file.isDirectory()) {
                files2.addAll(Arrays.asList(collectFiles(file, depth - 1)));
            } else {
                files2.add(file);
            }
        }

        return files2.toArray(EMPTY_FILES);
    }

    public static boolean isValidImage(final File file) {
        if (!(file.exists() && file.isFile() && file.canRead())) {
            return false;
        }
        final String contentType = FILE_TYPE_MAP.getContentType(file);
        return contentType.startsWith("image/")
            || contentType.equals("application/octet-stream");
    }
}
