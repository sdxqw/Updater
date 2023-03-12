package io.github.sdxqw.updater;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter(AccessLevel.MODULE)
public class Updater {
    private final String version;
    private final String url;
    private final Logger logger = Logger.getLogger(Updater.class.getName());
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public Updater(String url, String version) {
        this.url = url;
        this.version = version;
    }

    public CompletableFuture<String> check() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String versionFromUrl = in.readLine();
                if (versionFromUrl.compareTo(version) > 0 && !versionFromUrl.equals(version)) {
                    return versionFromUrl;
                } else {
                    return null;
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error while checking for updates: " + e.getMessage());
                throw new UpdaterException("Error while checking for updates", e);
            }
        }, executor);
    }

    public void check(BiConsumer<String, String> versionConsumer) {
        check().thenAcceptAsync(newVersion -> {
            if (newVersion != null) {
                versionConsumer.accept(newVersion, version);
            } else {
                versionConsumer.accept(version, version);
            }
        }, executor).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Error while checking for updates: " + ex.getMessage());
            return null;
        });
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Error while shutting down executor: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static class UpdaterException extends RuntimeException {
        public UpdaterException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
