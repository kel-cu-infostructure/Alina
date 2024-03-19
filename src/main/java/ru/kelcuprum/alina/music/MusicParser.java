package ru.kelcuprum.alina.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicParser {
    //
    public static boolean trackIsNull(AudioTrack track){
        return track == null;
    }
    //
    public static boolean isAuthorNull(AudioTrack info){
        return trackIsNull(info) || info.getInfo().author.equals("Unknown artist");
    }
    //
    public static String getAuthor(AudioTrack info){
        return isAuthorNull(info) ? "" : info.getInfo().author;
    }
    //
    public static boolean isTitleNull(AudioTrack info){
        return trackIsNull(info) || info.getInfo().title.equals("Unknown title");
    }
    //
    public static String getTitle(AudioTrack info){
        String[] fileArgs = info.getInfo().uri.split("/");
        if(fileArgs.length == 1) fileArgs = info.getInfo().uri.split("\\\\");
        String[] fileS = fileArgs[fileArgs.length-1].split("\\?");
        String file = fileS[0];
        return isTitleNull(info) ? file : info.getInfo().title;
    }
    //
    public static long getPosition(AudioTrack track){
        return trackIsNull(track) ? 0 : track.getPosition();
    }
    //
    public static long getDuration(AudioTrack track){
        return trackIsNull(track) ? 0 : track.getDuration();
    }
    //

}
