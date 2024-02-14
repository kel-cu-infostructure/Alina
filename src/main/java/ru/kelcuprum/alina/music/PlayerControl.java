package ru.kelcuprum.alina.music;

import com.github.topi314.lavasrc.applemusic.AppleMusicSourceManager;
import com.github.topi314.lavasrc.deezer.DeezerAudioSourceManager;
import com.github.topi314.lavasrc.flowerytts.FloweryTTSSourceManager;
import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.github.topi314.lavasrc.yandexmusic.YandexMusicSourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.kelcuprum.alina.config.UserConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static ru.kelcuprum.alina.Main.Colors.*;

public class PlayerControl extends ListenerAdapter
{
    public static final int DEFAULT_VOLUME = UserConfig.CURRENT_VOLUME; //(0 - 150, where 100 is default max volume)

    private static AudioPlayerManager playerManager;
    public static Map<String, GuildMusicManager> musicManagers;

    public PlayerControl()
    {
        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

        playerManager = new DefaultAudioPlayerManager();
        final YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager();
        youtube.setPlaylistPageCount(100);
        if(!UserConfig.YANDEX_MUSIC_TOKEN.isBlank()) playerManager.registerSourceManager(new YandexMusicSourceManager(UserConfig.YANDEX_MUSIC_TOKEN));
        if(!UserConfig.FLOWERY_TTS_VOICE.isBlank()) playerManager.registerSourceManager(new FloweryTTSSourceManager(UserConfig.FLOWERY_TTS_VOICE));
        if(!UserConfig.DEEZER_DECRYPTION_KEY.isBlank()) playerManager.registerSourceManager(new DeezerAudioSourceManager(UserConfig.DEEZER_DECRYPTION_KEY));
        if(!UserConfig.APPLE_MUSIC_MEDIA_API_TOKEN.isBlank() && !UserConfig.APPLE_MUSIC_COUNTRY_CODE.isBlank()) playerManager.registerSourceManager(new AppleMusicSourceManager(null, UserConfig.APPLE_MUSIC_MEDIA_API_TOKEN, UserConfig.APPLE_MUSIC_COUNTRY_CODE, playerManager));
        if(!UserConfig.SPOTIFY_CLIENT_ID.isBlank() && !UserConfig.SPOTIFY_CLIENT_SECRET.isBlank() && !UserConfig.SPOTIFY_COUNTRY_CODE.isBlank()) playerManager.registerSourceManager(new SpotifySourceManager(null, UserConfig.SPOTIFY_CLIENT_ID, UserConfig.SPOTIFY_CLIENT_SECRET, UserConfig.SPOTIFY_COUNTRY_CODE, playerManager));


        playerManager.registerSourceManager(youtube);
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());

        musicManagers = new HashMap<>();
    }

    public static void loadAndPlay(GuildMusicManager mng, final SlashCommandInteractionEvent event, String url, final boolean addPlaylist)
    {
        final String trackUrl;

        //Strip <>'s that prevent discord from embedding link resources
        if (url.startsWith("<") && url.endsWith(">"))
            trackUrl = url.substring(1, url.length() - 1);
        else
            trackUrl = url;


        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                EmbedBuilder embed = new EmbedBuilder();
                if(!MusicParser.isAuthorNull(track)) embed.setAuthor(MusicParser.getAuthor(track));
                if(track.getInfo().artworkUrl != null) embed.setThumbnail(track.getInfo().artworkUrl);
                embed.setColor(track.getInfo().isStream ? LIVE : TRACK)
                        .setTitle(MusicParser.getTitle(track), track.getInfo().uri)
                        .setDescription(mng.player.getPlayingTrack() == null? "Трек был поставлен на воспроизведения" : "Трек добавлен в очередь");
                mng.scheduler.queue(track);
                event.replyEmbeds(embed.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();


                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (addPlaylist)
                {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Добавлено **" + playlist.getTracks().size() +"** треков в очередь из плейлиста: " + playlist.getName()).setColor(DEFAULT).build()).setEphemeral(false).queue();
                    tracks.forEach(mng.scheduler::queue);
                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    if(!MusicParser.isAuthorNull(firstTrack)) embed.setAuthor(MusicParser.getAuthor(firstTrack));
                    if(firstTrack.getInfo().artworkUrl != null) embed.setThumbnail(firstTrack.getInfo().artworkUrl);
                    embed.setColor(firstTrack.getInfo().isStream ? LIVE : TRACK)
                            .setTitle(MusicParser.getTitle(firstTrack), firstTrack.getInfo().uri)
                            .setDescription(mng.player.getPlayingTrack() == null? "Трек был поставлен на воспроизведения" : "Трек добавлен в очередь")
                            .setFooter("Первый трек из плейлиста " + playlist.getName());
                    mng.scheduler.queue(firstTrack);
                    event.replyEmbeds(embed.build()).queue();
                }
            }

            @Override
            public void noMatches()
            {
                event.replyEmbeds(new EmbedBuilder().setDescription("Ничего не найдено по " + trackUrl).setColor(DEFAULT).build()).setEphemeral(true).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                event.replyEmbeds(new EmbedBuilder().setDescription("Не удалось воспроизвести: " + exception.getMessage()).setColor(DEFAULT).build()).setEphemeral(true).queue();
            }
        });
    }

    public static GuildMusicManager getMusicManager(Guild guild)
    {
        String guildId = guild.getId();
        GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null)
        {
            synchronized (musicManagers)
            {
                mng = musicManagers.get(guildId);
                if (mng == null)
                {
                    mng = new GuildMusicManager(playerManager);
                    mng.player.setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, mng);
                }
            }
        }
        return mng;
    }

    public static String getTimestamp(long milliseconds)
    {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

}
