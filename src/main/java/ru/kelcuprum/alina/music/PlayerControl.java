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
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.TvHtml5EmbeddedWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.Colors;
import ru.kelcuprum.alina.WebAPI;
import ru.kelcuprum.alina.config.Config;
import ru.kelcuprum.alina.music.sources.waterplayer.WaterPlayerSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static ru.kelcuprum.alina.Alina.Colors.*;

public class PlayerControl extends ListenerAdapter
{
    public static final int DEFAULT_VOLUME = 30;

    public static AudioPlayerManager audioPlayerManager;
    public static Map<String, GuildMusicManager> musicManagers;
    public final LocalAudioSourceManager localAudioSourceManager = new LocalAudioSourceManager();

    public PlayerControl()
    {
        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

        audioPlayerManager = new DefaultAudioPlayerManager();
        Config config = Alina.config;
        WaterPlayerSource wps = new WaterPlayerSource();
        if (!config.getString("MUSIC.YANDEX_MUSIC_TOKEN", "").isBlank())
            audioPlayerManager.registerSourceManager(new YandexMusicSourceManager(config.getString("MUSIC.YANDEX_MUSIC_TOKEN", "")));
        if (!config.getString("MUSIC.FLOWERY_TTS_VOICE", "Alena").isBlank())
            audioPlayerManager.registerSourceManager(new FloweryTTSSourceManager(config.getString("MUSIC.FLOWERY_TTS_VOICE", "Alena")));
        if (!config.getString("MUSIC.DEEZER_DECRYPTION_KEY", "").isBlank())
            audioPlayerManager.registerSourceManager(new DeezerAudioSourceManager(config.getString("MUSIC.DEEZER_DECRYPTION_KEY", "")));
        if (!config.getString("MUSIC.APPLE_MUSIC_MEDIA_API_TOKEN", "").isBlank() && !config.getString("MUSIC.APPLE_MUSIC_COUNTRY_CODE", "us").isBlank())
            audioPlayerManager.registerSourceManager(new AppleMusicSourceManager(null, config.getString("MUSIC.APPLE_MUSIC_MEDIA_API_TOKEN", ""), config.getString("MUSIC.APPLE_MUSIC_COUNTRY_CODE", "us"), audioPlayerManager));
        if (!config.getString("MUSIC.SPOTIFY_CLIENT_ID", "").isBlank() && !config.getString("MUSIC.SPOTIFY_CLIENT_SECRET", "").isBlank() && !config.getString("MUSIC.SPOTIFY_COUNTRY_CODE", "US").isBlank())
            audioPlayerManager.registerSourceManager(new SpotifySourceManager(null, config.getString("MUSIC.SPOTIFY_CLIENT_ID", ""), config.getString("MUSIC.SPOTIFY_CLIENT_SECRET", ""), config.getString("MUSIC.SPOTIFY_COUNTRY_CODE", "US"), audioPlayerManager));

        if (config.getBoolean("MUSIC.ENABLE_YOUTUBE", true)) {
            final YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true, new MusicWithThumbnail(), new WebWithThumbnail(), new TvHtml5EmbeddedWithThumbnail());
            youtube.setPlaylistPageCount(100);
            audioPlayerManager.registerSourceManager(youtube);
        }
        if (config.getBoolean("MUSIC.ENABLE_SOUNDCLOUD", true))
            audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        if (config.getBoolean("MUSIC.ENABLE_BANDCAMP", true))
            audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        if (config.getBoolean("MUSIC.ENABLE_VIMEO", true))
            audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        if (config.getBoolean("MUSIC.ENABLE_TWITCH", false))
            audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        if (config.getBoolean("MUSIC.ENABLE_BEAM", true))
            audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
        audioPlayerManager.registerSourceManager(wps);
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
        audioPlayerManager.registerSourceManager(localAudioSourceManager);

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


        audioPlayerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                EmbedBuilder embed = new EmbedBuilder();
                if(!MusicParser.isAuthorNull(track)) {
                    String url = WebAPI.getAuthorAvatar(track);
                    if(url.isBlank()) embed.setAuthor(MusicParser.getAuthor(track));
                    else embed.setAuthor(MusicParser.getAuthor(track), track.getInfo().uri, url);
                }
                String url = WebAPI.getArtwork(track);
                if(!url.isBlank()) embed.setThumbnail(url);
                else if(track.getInfo().artworkUrl != null) embed.setThumbnail(track.getInfo().artworkUrl);
                else embed.setThumbnail("https://wf.kelcu.ru/mods/waterplayer/icons/tetra.gif");

                embed.setColor(track.getInfo().isStream ? LIVE : TRACK)
                        .setTitle(MusicParser.getTitle(track), track.getInfo().uri)
                        .setDescription(Alina.localization.getLocalization("player.load."+ (mng.player.getPlayingTrack() == null ? "played" : "queue_added")));
                mng.scheduler.queue(track);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
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
                    event.getHook().sendMessageEmbeds(new EmbedBuilder().setColor(Colors.TETRA).setDescription(String.format(Alina.localization.getLocalization("player.load.playlist"), playlist.getTracks().size(), playlist.getName())).build()).queue();
                    tracks.forEach(mng.scheduler::queue);
                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    if(!MusicParser.isAuthorNull(firstTrack)) embed.setAuthor(MusicParser.getAuthor(firstTrack));
                    if(firstTrack.getInfo().artworkUrl != null) embed.setThumbnail(firstTrack.getInfo().artworkUrl);
                    embed.setColor(firstTrack.getInfo().isStream ? LIVE : TRACK)
                            .setTitle(MusicParser.getTitle(firstTrack), firstTrack.getInfo().uri)
                            .setDescription(Alina.localization.getLocalization("player.load."+ (mng.player.getPlayingTrack() == null ? "played" : "queue_added")))
                            .setFooter(String.format(Alina.localization.getLocalization("player.load.first_playlist"), playlist.getName()));
                    mng.scheduler.queue(firstTrack);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }
            }

            @Override
            public void noMatches()
            {
                event.getHook().sendMessageEmbeds(new EmbedBuilder().setColor(Colors.CLOWNFISH).setDescription(String.format(Alina.localization.getLocalization("player.no_matches"), trackUrl)).setColor(DEFAULT).build()).setEphemeral(true).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                event.getHook().sendMessageEmbeds(new EmbedBuilder().setColor(Colors.GROUPIE).setDescription(String.format(Alina.localization.getLocalization("player.load_failed"), exception.getMessage())).setColor(DEFAULT).build()).setEphemeral(true).queue();
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
                    mng = new GuildMusicManager(audioPlayerManager);
                    mng.player.setVolume(Alina.guildVolume.getNumber(guild.getId(), DEFAULT_VOLUME).intValue());
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
