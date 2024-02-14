package ru.kelcuprum.alina.config;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UserConfig {
    public static String TOKEN = "";
    public static int CURRENT_VOLUME = 2;
    // Yandex Music
    public static String YANDEX_MUSIC_TOKEN = "";
    // Spotify
    public static String SPOTIFY_CLIENT_ID = "";
    public static String SPOTIFY_CLIENT_SECRET = "";
    public static String SPOTIFY_COUNTRY_CODE = "us";
    // Apple Music
    public static String APPLE_MUSIC_MEDIA_API_TOKEN = "";
    public static String APPLE_MUSIC_COUNTRY_CODE = "us";
    // Deezer
    public static String DEEZER_DECRYPTION_KEY = "";
    // Flowery TTS
    public static String FLOWERY_TTS_VOICE = "Alena";
    public static Path configFile = Path.of("./config.json");

    /**
     * Сохранение конфигурации
     */
    public static void save() {
        JsonObject jsonConfig = new JsonObject();
        jsonConfig.addProperty("CURRENT_VOLUME", CURRENT_VOLUME);
        jsonConfig.addProperty("TOKEN", TOKEN);
        jsonConfig.addProperty("YANDEX_MUSIC_TOKEN", YANDEX_MUSIC_TOKEN);
        jsonConfig.addProperty("DEEZER_DECRYPTION_KEY", DEEZER_DECRYPTION_KEY);
        jsonConfig.addProperty("FLOWERY_TTS_VOICE", FLOWERY_TTS_VOICE);
        jsonConfig.addProperty("SPOTIFY_CLIENT_ID", SPOTIFY_CLIENT_ID);
        jsonConfig.addProperty("SPOTIFY_CLIENT_SECRET", SPOTIFY_CLIENT_SECRET);
        jsonConfig.addProperty("SPOTIFY_COUNTRY_CODE", SPOTIFY_COUNTRY_CODE);
        jsonConfig.addProperty("APPLE_MUSIC_MEDIA_API_TOKEN", APPLE_MUSIC_MEDIA_API_TOKEN);
        jsonConfig.addProperty("APPLE_MUSIC_COUNTRY_CODE", APPLE_MUSIC_COUNTRY_CODE);
        try {
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, jsonConfig.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Загрузка файла конфигов
     */
    public static void load() {
        try {
            JsonObject jsonConfig = GsonHelper.parse(Files.readString(configFile));
            for (String key : jsonConfig.keySet()) {
                switch (key.toUpperCase()) {
                    case "CURRENT_VOLUME" -> CURRENT_VOLUME = jsonConfig.get(key).getAsInt();
                    case "TOKEN" -> TOKEN = jsonConfig.get(key).getAsString();
                    case "YANDEX_MUSIC_TOKEN" -> YANDEX_MUSIC_TOKEN = jsonConfig.get(key).getAsString();
                    case "DEEZER_DECRYPTION_KEY" -> DEEZER_DECRYPTION_KEY = jsonConfig.get(key).getAsString();
                    case "FLOWERY_TTS_VOICE" -> FLOWERY_TTS_VOICE = jsonConfig.get(key).getAsString();
                    case "SPOTIFY_CLIENT_ID" -> SPOTIFY_CLIENT_ID = jsonConfig.get(key).getAsString();
                    case "SPOTIFY_CLIENT_SECRET" -> SPOTIFY_CLIENT_SECRET = jsonConfig.get(key).getAsString();
                    case "SPOTIFY_COUNTRY_CODE" -> SPOTIFY_COUNTRY_CODE = jsonConfig.get(key).getAsString();
                    case "APPLE_MUSIC_MEDIA_API_TOKEN" ->
                            APPLE_MUSIC_MEDIA_API_TOKEN = jsonConfig.get(key).getAsString();
                    case "APPLE_MUSIC_COUNTRY_CODE" -> APPLE_MUSIC_COUNTRY_CODE = jsonConfig.get(key).getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            save();
        }

    }
}
