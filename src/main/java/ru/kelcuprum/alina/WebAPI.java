package ru.kelcuprum.alina;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.event.Level;
import ru.kelcuprum.alina.config.GsonHelper;
import ru.kelcuprum.alina.music.MusicHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;

public class WebAPI {
    public static java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .version(java.net.http.HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static String getString(java.net.http.HttpRequest request) throws IOException, InterruptedException {
        if (httpClient == null) {
            httpClient = java.net.http.HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
        }
        java.net.http.HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    public static String getString(java.net.http.HttpRequest.Builder url) throws IOException, InterruptedException {
        return getString(url.build());
    }
    public static String getString(String url) throws IOException, InterruptedException {
        return getString(java.net.http.HttpRequest.newBuilder().uri(URI.create(url)).build());
    }

    public static JsonObject getJsonObject(String url) throws IOException, InterruptedException {
        return GsonHelper.parse(getString(java.net.http.HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").build()));
    }

    public static JsonArray getJsonArray(String url) throws IOException, InterruptedException {
        return GsonHelper.parseArray(getString(java.net.http.HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").build()));
    }
    // HttpRequest
    public static JsonObject getJsonObject(java.net.http.HttpRequest url) throws IOException, InterruptedException {
        return GsonHelper.parse(getString(url));
    }

    public static JsonArray getJsonArray(java.net.http.HttpRequest url) throws IOException, InterruptedException {
        return GsonHelper.parseArray(getString(url));
    }
    // HttpRequest.Builder
    public static JsonObject getJsonObject(java.net.http.HttpRequest.Builder url) throws IOException, InterruptedException {
        return getJsonObject(url.header("Content-Type", "application/json").build());
    }

    public static JsonArray getJsonArray(HttpRequest.Builder url) throws IOException, InterruptedException {
        return getJsonArray(url.header("Content-Type", "application/json").build());
    }


    public static HashMap<String, JsonObject> urlsArtworks = new HashMap<>();

    public static String getAuthorAvatar(AudioTrack track){
        String author = MusicHelper.getAuthor(track);
        if(author.split(",").length > 1) author = author.split(",")[0];
        else if(author.split(";").length > 1) author = author.split(";")[0];
        else if(author.split("/").length > 1) author = author.split("/")[0];
        return getAuthorAvatar(author);
    }
    public static String getAuthorAvatar(String author){
        try{
            JsonObject authorInfo;
            String url = (String.format("https://wplayer.ru/v2/info?author=%1$s", uriEncode(author)));
            if(urlsArtworks.containsKey(url)) authorInfo = urlsArtworks.get(url);
            else {
                authorInfo = WebAPI.getJsonObject(url);
                urlsArtworks.put(url, authorInfo);
            }
            if(authorInfo.has("error")) throw new RuntimeException(authorInfo.getAsJsonObject("error").get("message").getAsString());
            else if(authorInfo.getAsJsonObject("author").has("artwork"))
                return authorInfo.getAsJsonObject("author").get("artwork").getAsString();
            else return "";
        } catch (Exception ex){
            Alina.log(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage(), Level.DEBUG);
            return "";
        }
    }
    public static String getArtwork(AudioTrack track){
        String author = MusicHelper.getAuthor(track);
        if(author.split(",").length > 1) author = author.split(",")[0];
        else if(author.split(";").length > 1) author = author.split(";")[0];
        else if(author.split("/").length > 1) author = author.split("/")[0];
        return getArtwork(author, MusicHelper.getTitle(track));
    }
    public static String getArtwork(String author, String album){
        try{
            JsonObject authorInfo;
            String url = (String.format("https://wplayer.ru/v2/info?author=%1$s&album=%2$s", uriEncode(author), uriEncode(album)));
            if(urlsArtworks.containsKey(url)) authorInfo = urlsArtworks.get(url);
            else {
                authorInfo = WebAPI.getJsonObject(url);
                urlsArtworks.put(url, authorInfo);
            }
            if(authorInfo.has("error")) throw new RuntimeException(authorInfo.getAsJsonObject("error").get("message").getAsString());
            else if(authorInfo.getAsJsonObject("track").has("artwork"))
                return authorInfo.getAsJsonObject("track").get("artwork").getAsString();
            else return "";
        } catch (Exception ex){
            Alina.log(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage(), Level.INFO);
            return "";
        }
    }
    public static String uriEncode(String uri){
        return URLEncoder.encode(uri, StandardCharsets.UTF_8);
    }
}
