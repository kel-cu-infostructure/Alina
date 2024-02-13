package ru.kelcuprum.alina.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.MusicParser;
import ru.kelcuprum.alina.music.PlayerControl;

import static ru.kelcuprum.alina.Main.Colors.*;

public class NowPlaying {
    public NowPlaying(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        assert guild != null;
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        AudioPlayer player = mng.player;
        AudioTrack currentTrack = player.getPlayingTrack();
        if (currentTrack != null)
        {
            String position = PlayerControl.getTimestamp(MusicParser.getPosition(currentTrack));
            String duration = currentTrack.getInfo().isStream ? "-" : PlayerControl.getTimestamp(MusicParser.getDuration(currentTrack));
            EmbedBuilder embed = new EmbedBuilder();
            if(!MusicParser.isAuthorNull(currentTrack)) embed.setAuthor(MusicParser.getAuthor(currentTrack));
            if(currentTrack.getInfo().artworkUrl != null) embed.setImage(currentTrack.getInfo().artworkUrl);
            embed.setTitle(MusicParser.getTitle(currentTrack), currentTrack.getInfo().uri)
                    .setColor(mng.player.isPaused() ? PAUSE : currentTrack.getInfo().isStream ? LIVE : TRACK)
                    .setDescription(String.format("%s%s%s / %s", mng.scheduler.isRepeating() ? ":repeat_one: " : "",
                            (mng.player.getVolume() <= 0) ? "🔇 " : (mng.player.getVolume() <= 1) ? "🔈 " : (mng.player.getVolume() <= 70) ? "🔉 " :  "🔊 ",
                            position, duration));
            event.replyEmbeds(embed.build()).queue();
        }
        else
            event.replyEmbeds(new EmbedBuilder().setDescription("Сейчас ничего не играет!").setColor(DEFAULT).build()).setEphemeral(false).queue();
    }
}
