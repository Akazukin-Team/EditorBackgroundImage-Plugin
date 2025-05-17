package org.akazukin.intellij.background.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for file operations and validation.
 */
@UtilityClass
public final class FileUtils {
    public static final File[] EMPTY_FILES = new File[0];

    public static final String EXC_NEGA_DEPTH = "The depth is must be positive";
    public static final String EXC_DIR_IS_FILE = "The directory is not a file";

    private static final MimetypesFileTypeMap FILE_TYPE_MAP
        = new MimetypesFileTypeMap();

    /**
     * Collects files from a given directory up to a specified depth.
     * The method performs recursive traversal of subdirectories if the specified depth is greater than zero.
     *
     * @param directory The directory from which files are collected.
     *                  Must not be {@code null} and must point to a directory.
     * @param depth     The maximum depth for recursive directory traversal.
     *                  A depth of {@code 0} only collects files in the given directory without recursion.
     *                  Depth must not be negative.
     * @return An array of {@link File} objects representing the collected files.
     * If no files are found, an empty array is returned.
     * @throws IllegalStateException    If the given {@code directory} is not a directory but a file.
     * @throws IllegalArgumentException If the specified {@code depth} is negative.
     */
    public static File[] collectFiles(
        @NotNull final File directory, final int depth) {
        if (directory.isFile()) {
            throw new IllegalStateException(EXC_DIR_IS_FILE);
        }
        if (depth < 0) {
            throw new IllegalArgumentException(EXC_NEGA_DEPTH);
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

    /**
     * Validates if the provided file is a readable and supported image file.
     *
     * @param file The file to be validated.
     *             Must not be {@code null} and must represent a readable file.
     * @return {@code true} if the file exists, is readable, and has a MIME type
     * that starts with "image/" or equals "application/octet-stream".
     * Otherwise, returns {@code false}.
     */
    public static boolean isValidImage(@NotNull final File file) {
        if (!(file.exists() && file.isFile() && file.canRead())) {
            return false;
        }
        final String contentType = FILE_TYPE_MAP.getContentType(file);
        return contentType.startsWith("image/")
            || contentType.equals("application/octet-stream");
    }
}
