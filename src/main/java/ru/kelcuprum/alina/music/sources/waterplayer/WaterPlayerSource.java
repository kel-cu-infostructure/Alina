package ru.kelcuprum.alina.music.sources.waterplayer;

import com.google.gson.JsonObject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.music.playlist.Playlist;
import ru.kelcuprum.alina.music.playlist.WebPlaylist;

import java.io.DataInput;
import java.io.DataOutput;

import static ru.kelcuprum.alina.WebAPI.getJsonObject;

public class WaterPlayerSource implements AudioSourceManager {

    @Override
    public @NotNull String getSourceName() {
        return "wplayer";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        var identifier = reference.identifier;
        if(identifier.startsWith("http://") || identifier.startsWith("https://")){
            try{
                Playlist playlist = getPlaylist(identifier);
                return new WaterPlayerPlaylist(manager, playlist);
            } catch (Exception ex){
                Alina.log("ERROR: "+(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage()), Level.DEBUG);
            }
        }
        if(identifier.startsWith(getSourceName()+":")){
            String id = identifier.replace(getSourceName()+":", "");
            String url = String.format("https://wplayer.ru/playlist/%s", id);
            try {
                return new WaterPlayerPlaylist(manager, getPlaylist(url));
            } catch (Exception ex) {
                Alina.log("ERROR: "+(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage()), Level.DEBUG);
            }
        }
        return null;
    }
    public static Playlist getPlaylist(String url) throws Exception {
        try {
            JsonObject data = getJsonObject(url);
            if(data.has("error")){
                throw new Exception(data.getAsJsonObject("error").get("message").getAsString());
            }
            WebPlaylist playlist = new WebPlaylist(data);
            return playlist.playlist;
        } catch (Exception e){
            Alina.log(e.getMessage() == null ? e.getClass().getName() : e.getMessage(), Level.ERROR);
            throw new Exception("External error: "+(e.getMessage() == null ? e.getClass().getName() : e.getMessage()));
        }
    }


    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {

    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
