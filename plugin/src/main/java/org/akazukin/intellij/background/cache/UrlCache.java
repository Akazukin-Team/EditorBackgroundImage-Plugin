package org.akazukin.intellij.background.cache;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.intellij.background.EditorBackgroundImage;
import org.akazukin.intellij.background.PluginHandler;
import org.akazukin.intellij.background.utils.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public final class UrlCache implements ICache {
    Map<String, FileData> cache = new HashMap<>();
    Map<File, byte[]> cacheFiles = new HashMap<>();

    public synchronized void analyzeAndCache(final URL url, final boolean enabled, final boolean webpSupport) {
        {
            final FileData data = this.cache.get(url.toExternalForm());
            if (data != null
                && data.isEnabled() == enabled
                && data.getData().isWebpSupport() == webpSupport) {
                return;
            }
        }

        try {
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(10000);
            con.setDoInput(true);
            con.setDoOutput(false);

            try {
                con.connect();

                if (!FileUtils.isValidType(con.getContentType(), webpSupport)) {
                    return;
                }

                final byte[] bytes = con.getInputStream().readAllBytes();
                final byte[] fileHash = FileUtils.calcFileHash(bytes);

                final File file;
                final Set<File> invalids = new HashSet<>();
                final Optional<File> opt = this.cacheFiles.entrySet().stream()
                    .filter(e -> Arrays.equals(e.getValue(), fileHash))
                    .map(Map.Entry::getKey)
                    .filter(f -> {
                        try (final FileInputStream fis = new FileInputStream(f)) {
                            return Arrays.equals(fis.readAllBytes(), bytes);
                        } catch (final IOException ignored) {
                            invalids.add(f);
                            return false;
                        }
                    })
                    .findFirst();
                invalids.forEach(this.cacheFiles::remove);

                if (opt.isPresent()) {
                    file = opt.get();
                } else {
                    final long fileId = PluginHandler.getPlugin().getSnowflake().nextId();
                    file = EditorBackgroundImage.TEMP_DIR.resolve("images/" + fileId + ".tmp").toFile();

                    file.getParentFile().mkdirs();
                    if (!file.createNewFile()) {
                        throw new IOException("Failed to create file");
                    }
                    try (final FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(bytes);
                    }

                    this.cacheFiles.put(file, fileHash);
                }

                this.cache(url, file, enabled, webpSupport);
            } finally {
                con.disconnect();
            }
        } catch (final IOException e) {
            log.warn("Failed to download image from {}", url, e);

            this.cacheInvalids(url, enabled, webpSupport);
        }
    }

    public synchronized void cache(final URL url, final File file, final boolean enabled, final boolean webpSupport) {
        this.cache(url, file, enabled, webpSupport, FileUtils.isValidImage(file, webpSupport));
    }

    public synchronized void cache(final URL url, final File file, final boolean enabled, final boolean webpSupport, final boolean valid) {
        this.cache.put(url.toExternalForm(), new FileData(enabled, new FileData.AnalyzeData(webpSupport, valid, file)));
    }

    public synchronized void cacheInvalids(final URL url, final boolean enabled, final boolean webpSupport) {
        this.cache(url, null, enabled, webpSupport, false);
    }

    @Override
    public synchronized void clear() {
        this.cache.clear();
    }

    @Override
    public synchronized File[] getValidFiles() {
        return this.cache.values().stream()
            .filter(fileData -> fileData.getData().isValid())
            .map(fileData -> fileData.getData().getFile())
            .toArray(File[]::new);
    }

    @Override
    public synchronized File[] getValidAndEnabledFiles() {
        return this.cache.values().stream()
            .filter(d -> d.isEnabled() && d.getData().isValid())
            .map(d -> d.getData().getFile())
            .toArray(File[]::new);
    }

    @Override
    public int getValidCount() {
        return (int) this.cache.values().stream()
            .filter(fileData -> fileData.getData().isValid())
            .count();
    }

    @Override
    public int getValidAndEnabledCount() {
        return (int) this.cache.values().stream()
            .filter(e -> e.isEnabled() && e.getData().isValid())
            .count();
    }

    @Nullable
    public File getFiles(final URL url) {
        final FileData cache = this.cache.get(url.toExternalForm());
        return cache != null && cache.data != null ? cache.data.getFile() : null;
    }
}
