package ru.kelcuprum.alina.music.playlist;

import com.google.gson.JsonObject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ru.kelcuprum.alina.music.sources.waterplayer.WaterPlayerPlaylist;
import java.util.List;

public class WebPlaylist {
    public String id;
    public String url;
    public Playlist playlist;

    public WebPlaylist(JsonObject data) throws Exception {
        if (!isValid(data)) throw new Exception("Incorrect web playlist format");
        id = data.has("id") ? data.get("id").getAsString() : "Example title";
        url = data.has("url") ? data.get("url").getAsString() : "";
        playlist = new Playlist(data.getAsJsonObject("data"));
    }

    public static boolean isValid(JsonObject data) {
        return data.has("id") && data.has("url") && data.has("data") && Playlist.isValid(data.getAsJsonObject("data"));
    }

    public List<AudioTrack> getTracks(AudioPlayerManager playerManager){
        return new WaterPlayerPlaylist(playerManager, playlist).getTracks();
    }
}
