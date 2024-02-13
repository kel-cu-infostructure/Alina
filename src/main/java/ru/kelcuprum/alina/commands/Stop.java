package ru.kelcuprum.alina.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;
import ru.kelcuprum.alina.music.TrackScheduler;

import static ru.kelcuprum.alina.Main.Colors.DEFAULT;

public class Stop {
    public Stop(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        TrackScheduler scheduler = mng.scheduler;
        AudioPlayer player = mng.player;
        synchronized (PlayerControl.musicManagers)
        {
            scheduler.queue.clear();
            player.destroy();
            guild.getAudioManager().setSendingHandler(null);
            PlayerControl.musicManagers.remove(guild.getId());
        }

        mng = PlayerControl.getMusicManager(guild);
        guild.getAudioManager().setSendingHandler(mng.sendHandler);
        event.replyEmbeds(new EmbedBuilder().setDescription("Этот плеер был успешно сброшен.").setColor(DEFAULT).build()).setEphemeral(false).queue();
    }
}
