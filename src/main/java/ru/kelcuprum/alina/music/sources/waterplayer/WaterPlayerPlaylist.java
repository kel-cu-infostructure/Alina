package ru.kelcuprum.alina.music.sources.waterplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.event.Level;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.music.playlist.Playlist;

import java.util.ArrayList;
import java.util.List;

public class WaterPlayerPlaylist implements AudioPlaylist {
    protected final Playlist playlist;
    List<AudioTrack> tracks;

    public WaterPlayerPlaylist(AudioPlayerManager player, Playlist playlist) {
        tracks = new ArrayList<>();
        for (String url : playlist.urls) {
            player.loadItemSync(url, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    tracks.add(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    tracks.addAll(playlist.getTracks());
                }

                @Override
                public void noMatches() {
                    Alina.log("Nothing Found by " + url, Level.WARN);
                }

                @Override
                public void loadFailed(FriendlyException ex) {
                    Alina.log("ERROR: "+(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage()), Level.DEBUG);
                }
            });
        }
        this.playlist = playlist;
    }

    @Override
    public String getName() {
        return playlist.title;
    }

    @Override
    public List<AudioTrack> getTracks() {
        return tracks;
    }

    @Override
    public AudioTrack getSelectedTrack() {
        return getTracks().isEmpty() ? null : getTracks().get(0);
    }

    @Override
    public boolean isSearchResult() {
        return false;
    }
}
