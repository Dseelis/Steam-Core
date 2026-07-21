package com.steam.steamcore.client.util;

import com.steam.steamcore.SteamCore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

// Asynchronously fetches changelog and release info from GitHub.
// Results are cached for the current game session.

public class GitHubDataFetcher {

    private static final String GITHUB_REPO    = "Dseelis/Steamcreate2";
    private static final String CHANGELOG_URL  = "https://raw.githubusercontent.com/" + GITHUB_REPO + "/dev/CHANGELOG.md";
    private static final String RELEASES_URL   = "https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest";

    private static final int TIMEOUT_MS = 5000;

    // Cached state

    public enum Status { LOADING, DONE, ERROR }

    private static volatile String cachedChangelog    = null;
    private static volatile String cachedLatestTag    = null;
    private static volatile String changelogError     = null;
    private static volatile String releaseError       = null;
    private static volatile Status changelogStatus    = Status.LOADING;
    private static volatile Status releaseStatus      = Status.LOADING;
    private static volatile boolean fetchStarted      = false;

    // Public API

// Starts the async fetch if not already started. Safe to call multiple times.
    public static synchronized void startFetchIfNeeded() {
        if (fetchStarted) return;
        fetchStarted = true;

        CompletableFuture.runAsync(GitHubDataFetcher::fetchChangelog);
        CompletableFuture.runAsync(GitHubDataFetcher::fetchLatestRelease);
    }

    // Reset cache so next call to startFetchIfNeeded will re-fetch.
    public static synchronized void reset() {
        fetchStarted    = false;
        cachedChangelog = null;
        cachedLatestTag = null;
        changelogError  = null;
        releaseError    = null;
        changelogStatus = Status.LOADING;
        releaseStatus   = Status.LOADING;
    }

    public static Status getChangelogStatus()  { return changelogStatus; }
    public static Status getReleaseStatus()    { return releaseStatus; }

    // Returns the raw Markdown changelog, or null if not yet loaded.
    public static String getChangelog()        { return cachedChangelog; }

    // Returns error message for changelog, or null.
    public static String getChangelogError()   { return changelogError; }

     // Returns the latest GitHub tag name (e.g. "steamcore-1.1.5b-neoforge-1.21.1"),
     // or null if not yet loaded.
    public static String getLatestTag()        { return cachedLatestTag; }

     // Compares the current mod version with the latest GitHub release tag.
     // Returns true if an update seems available (tag contains a newer version string).
    public static boolean isUpdateAvailable() {
        if (cachedLatestTag == null) return false;
        String curVer = SteamCore.getPackVersion();
        return !cachedLatestTag.contains(curVer)
                && !cachedLatestTag.contains(SteamCore.MODID + "-" + curVer);
    }

    // Internal fetch methods

    private static void fetchChangelog() {
        try {
            String content = httpGet(CHANGELOG_URL);
            cachedChangelog = content;
            changelogStatus = Status.DONE;
        } catch (Exception e) {
            SteamCore.LOGGER.warn("[SteamCore] Failed to fetch changelog: {}", e.getMessage());
            changelogError  = e.getMessage();
            changelogStatus = Status.ERROR;
        }
    }

    private static void fetchLatestRelease() {
        try {
            String json = httpGet(RELEASES_URL);
            // Parse "tag_name" from JSON without pulling in a JSON library
            String tag = parseJsonString(json, "tag_name");
            if (tag == null) tag = parseJsonString(json, "name");
            cachedLatestTag = tag;
            releaseStatus   = Status.DONE;
        } catch (Exception e) {
            SteamCore.LOGGER.warn("[SteamCore] Failed to fetch latest release: {}", e.getMessage());
            releaseError  = e.getMessage();
            releaseStatus = Status.ERROR;
        }
    }

    private static String httpGet(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestProperty("User-Agent", "SteamCore-Mod/" + SteamCore.getPackVersion());
        conn.setRequestProperty("Accept", "application/vnd.github+json");

        int code = conn.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    // Minimal JSON string field extractor
    private static String parseJsonString(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + search.length());
        if (colon == -1) return null;
        int start = json.indexOf('"', colon + 1);
        if (start == -1) return null;
        int end = json.indexOf('"', start + 1);
        if (end == -1) return null;
        return json.substring(start + 1, end);
    }
}
