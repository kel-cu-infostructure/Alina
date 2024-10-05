package ru.kelcuprum.alina.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicHelper {
    //
    public static boolean trackIsNull(AudioTrack track){
        return track == null;
    }
    //
    public static boolean isAuthorNull(AudioTrack info){
        return trackIsNull(info) || info.getInfo().author.isBlank() || info.getInfo().author.equals("Unknown artist") || info.getInfo().author.equals("Unknown");
    }
    //
    public static String getAuthor(AudioTrack info){
        String author = isAuthorNull(info) ? "" : info.getInfo().author;
        if(author.endsWith(" - Topic")) author = author.replace(" - Topic", "");
        return author;
    }
    //
    public static boolean isTitleNull(AudioTrack info){
        return trackIsNull(info) || info.getInfo().title.isBlank() || info.getInfo().title.equals("Unknown title");
    }
    //
    public static String getTitle(AudioTrack info){
        if(trackIsNull(info)) return "";
        String[] fileArgs = info.getInfo().uri.split("/");
        if(fileArgs.length == 1) fileArgs = info.getInfo().uri.split("\\\\");
        String file = fileArgs[fileArgs.length-1];
        return isTitleNull(info) ? file : info.getInfo().title;
    }

}
