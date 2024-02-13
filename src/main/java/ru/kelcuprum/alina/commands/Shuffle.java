package ru.kelcuprum.alina.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;
import ru.kelcuprum.alina.music.TrackScheduler;

import static ru.kelcuprum.alina.Main.Colors.DEFAULT;

public class Shuffle {
    public Shuffle(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        TrackScheduler scheduler = mng.scheduler;
        if (scheduler.queue.isEmpty())
        {
            event.replyEmbeds(new EmbedBuilder().setDescription("Очередь пуста!").setColor(DEFAULT).build()).setEphemeral(true).queue();;
            return;
        }

        scheduler.shuffle();
        event.replyEmbeds(new EmbedBuilder().setDescription("Очередь перемешена!").setColor(DEFAULT).build()).setEphemeral(false).queue();
    }
}
