package com.steam.steamcore.story.dialogue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/** Text values support plain strings, '$translation.keys', and locale maps. */
public final class DialogueText {
    private static final String LOCALES_PREFIX = "@locales:";

    private DialogueText() {}

    public static String fromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) throw new IllegalArgumentException("Dialogue text is required");
        if (element.isJsonPrimitive()) return element.getAsString();
        if (!element.isJsonObject()) throw new IllegalArgumentException("Dialogue text must be a string or locale map");
        JsonObject locales = element.getAsJsonObject();
        if (locales.size() == 0) throw new IllegalArgumentException("Dialogue locale map cannot be empty");
        return LOCALES_PREFIX + locales;
    }

    public static JsonElement toJson(String value) {
        return value.startsWith(LOCALES_PREFIX)
                ? JsonParser.parseString(value.substring(LOCALES_PREFIX.length()))
                : new JsonPrimitive(value);
    }

    public static String resolve(String value, String languageCode) {
        if (value.startsWith("$") && value.length() > 1) {
            return net.minecraft.locale.Language.getInstance().getOrDefault(value.substring(1), value.substring(1));
        }
        if (!value.startsWith(LOCALES_PREFIX)) return value;

        JsonObject locales = JsonParser.parseString(value.substring(LOCALES_PREFIX.length())).getAsJsonObject();
        String language = languageCode == null ? "en_us" : languageCode.toLowerCase(java.util.Locale.ROOT);
        String translated = getString(locales, language);
        if (translated == null && language.contains("_")) translated = getString(locales, language.substring(0, language.indexOf('_')));
        if (translated == null) translated = getString(locales, "en_us");
        if (translated == null) translated = getString(locales, "en");
        if (translated == null) translated = locales.entrySet().iterator().next().getValue().getAsString();
        return translated;
    }

    private static String getString(JsonObject object, String key) {
        JsonElement value = object.get(key);
        return value != null && value.isJsonPrimitive() ? value.getAsString() : null;
    }
}
