package ru.kelcuprum.alina.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;
import ru.kelcuprum.alina.music.TrackScheduler;

import static ru.kelcuprum.alina.Main.Colors.DEFAULT;

public class Replay {
    public Replay(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        TrackScheduler scheduler = mng.scheduler;
        AudioPlayer player = mng.player;

        AudioTrack track = player.getPlayingTrack();
        if (track == null)
            track = scheduler.lastTrack;

        if (track != null)
        {
            event.replyEmbeds(new EmbedBuilder().setDescription("Перезапущен трек: " + track.getInfo().title).setColor(DEFAULT).build()).setEphemeral(false).queue();;
            player.playTrack(track.makeClone());
        }
        else
        {
            event.replyEmbeds(new EmbedBuilder().setDescription("Ни один трек не был запущен ранее, поэтому плеер не может переиграть трек!").setColor(DEFAULT).build()).setEphemeral(true).queue();;
        }
    }
}
