package ru.kelcuprum.alina;

import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;

public class Configuration {
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
    static final Path configFile = Path.of("./config.json");
    public static void save(){
        JSONObject jsonConfig = new JSONObject();
        jsonConfig.put("TOKEN", TOKEN)
                .put("CURRENT_VOLUME", CURRENT_VOLUME)
                .put("YANDEX_MUSIC_TOKEN", YANDEX_MUSIC_TOKEN)
                .put("DEEZER_DECRYPTION_KEY", DEEZER_DECRYPTION_KEY)
                .put("FLOWERY_TTS_VOICE", FLOWERY_TTS_VOICE)
                .put("SPOTIFY", new JSONObject()
                        .put("CLIENT_ID", SPOTIFY_CLIENT_ID)
                        .put("CLIENT_SECRET", SPOTIFY_CLIENT_SECRET)
                        .put("COUNTRY_CODE", SPOTIFY_COUNTRY_CODE))
                .put("APPLE_MUSIC", new JSONObject()
                        .put("MEDIA_API_TOKEN", APPLE_MUSIC_MEDIA_API_TOKEN)
                        .put("COUNTRY_CODE", APPLE_MUSIC_COUNTRY_CODE));
        try {
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, jsonConfig.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void load(){
        try{
            JSONObject jsonConfig = new JSONObject(Files.readString(configFile));
            for (String key : jsonConfig.keySet()) {
                switch (key.toUpperCase()) {
                    case "TOKEN" -> TOKEN = jsonConfig.getString(key);
                    case "CURRENT_VOLUME" -> CURRENT_VOLUME = jsonConfig.getInt(key);
                    case "YANDEX_MUSIC_TOKEN" -> YANDEX_MUSIC_TOKEN = jsonConfig.getString(key);
                    case "DEEZER_DECRYPTION_KEY" -> DEEZER_DECRYPTION_KEY = jsonConfig.getString(key);
                    case "FLOWERY_TTS_VOICE" -> FLOWERY_TTS_VOICE = jsonConfig.getString(key);
                    case "SPOTIFY" -> {
                        JSONObject jsonConfigSpotify = jsonConfig.getJSONObject(key);
                        for (String keyS : jsonConfigSpotify.keySet()) {
                            switch (keyS.toUpperCase()) {
                                case "CLIENT_ID" -> SPOTIFY_CLIENT_ID = jsonConfigSpotify.getString(keyS);
                                case "CLIENT_SECRET" -> SPOTIFY_CLIENT_SECRET = jsonConfigSpotify.getString(keyS);
                                case "COUNTRY_CODE" -> SPOTIFY_COUNTRY_CODE = jsonConfigSpotify.getString(keyS);
                            }
                        }
                    }
                    case "APPLE_MUSIC" -> {
                        JSONObject jsonConfigAppleMusic = jsonConfig.getJSONObject(key);
                        for (String keyAM : jsonConfigAppleMusic.keySet()) {
                            switch (keyAM.toUpperCase()) {
                                case "MEDIA_API_TOKEN" -> APPLE_MUSIC_MEDIA_API_TOKEN = jsonConfigAppleMusic.getString(keyAM);
                                case "COUNTRY_CODE" -> APPLE_MUSIC_COUNTRY_CODE = jsonConfigAppleMusic.getString(keyAM);
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            save();
        }
    }
}
