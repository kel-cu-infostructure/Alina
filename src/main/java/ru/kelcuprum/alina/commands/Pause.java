package ru.kelcuprum.alina.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;

import static ru.kelcuprum.alina.Main.Colors.DEFAULT;

public class Pause {
    public Pause(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        AudioPlayer player = mng.player;
        if (player.getPlayingTrack() == null)
        {
            event.replyEmbeds(new EmbedBuilder().setDescription("Я не могу поставить на паузу, ничего не играет!").setColor(DEFAULT).build()).setEphemeral(true).queue();
            return;
        }

        player.setPaused(!player.isPaused());
        event.replyEmbeds(new EmbedBuilder().setDescription(player.isPaused() ? "Плеер поставлен на паузу" : "Плеер снят с паузы").setColor(DEFAULT).build()).queue();
    }
}
