package ru.kelcuprum.alina.music.playlist;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.slf4j.event.Level;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.config.GsonHelper;
import ru.kelcuprum.alina.music.sources.waterplayer.WaterPlayerPlaylist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Playlist {
    public String title;
    public String author;
    public JsonArray urlsJSON;
    public List<String> urls = new ArrayList<>();
    public Path path;
    public String icon;
    public String fileName = "Unknown";


    public Playlist(JsonObject data){
        title = data.has("title") ? data.get("title").getAsString() : "Example title";
        author = data.has("author") ? data.get("author").getAsString() : "";
        urlsJSON = data.has("urls") ? data.get("urls").getAsJsonArray() : GsonHelper.parseArray("[\"https://www.youtube.com/watch?v=2bjBl-nX1oc\"]");
        icon = data.has("icon") ? data.get("icon").getAsString() : null;
        for(int i = 0; i < urlsJSON.size(); i++){
            urls.add(urlsJSON.get(i).getAsString());
        }
    }
    public static boolean isValid(JsonObject data){
        return data.has("title") && data.has("author") && data.has("urls");
    }

    public void save(){
        if(this.path == null) return;
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, toJSON().toString());
        } catch (IOException e) {
            Alina.log(e.getLocalizedMessage(), Level.ERROR);
        }
    }

    public Playlist addUrl(String url){
        urls.add(url);
        save();
        return this;
    }
    public Playlist setUrl(String url, int position){
        urls.set(position, url);
        save();
        return this;
    }



    public JsonObject toJSON(){
        JsonObject data = new JsonObject();
        data.addProperty("title", title);
        data.addProperty("author", author);
        if(icon != null) data.addProperty("icon", icon);
        data.add("urls", getUrlsJSON());
        return data;
    }
    public WaterPlayerPlaylist getLavaplayerPlaylist(AudioPlayerManager audioPlayerManager){
        return new WaterPlayerPlaylist(audioPlayerManager, this);
    }
    @Override
    public String toString() {
        return toJSON().toString();
    }

    public JsonArray getUrlsJSON(){
        JsonArray array = new JsonArray();
        for(String url : urls){
            if(!url.isBlank()) array.add(url);
        }
        return array;
    }
}
