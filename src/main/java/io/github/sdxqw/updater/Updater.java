package io.github.sdxqw.updater;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class Updater {
    private final String version;
    private final String url;
    private final Logger logger = Logger.getLogger(Updater.class.getName());

    /**
     * Constructor to create an Updater object with a version and a URL
     *
     * @param url     the URL of the version file
     * @param version the current version of the software
     */
    public Updater(String url, String version) {
        this.url = url;
        this.version = version;
    }

    /**
     * Method to check for updates
     *
     * @param version a BiConsumer to handle the result of the check
     *                        The first parameter is the latest version available, and the second parameter is the current version.
     *                        If the software is up-to-date, both parameters will be equal.
     */
    public void check(BiConsumer<String, String> version) {
        try {
            URL url = new URL(this.url);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String versionFromUrl = in.readLine();
            if (versionFromUrl.compareTo(this.version) > 0 && !versionFromUrl.equals(this.version))
                version.accept(versionFromUrl, this.version);
            else
                version.accept(this.version, this.version);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while checking for updates: " + e.getMessage());
        }
    }
}
