package ru.kelcuprum.alina.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.kelcuprum.alina.Configuration;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;

import static ru.kelcuprum.alina.Main.Colors.DEFAULT;

public class Volume {
    public Volume(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        AudioPlayer player = mng.player;
        if (event.getOption("value") == null)  event.replyEmbeds(new EmbedBuilder().setDescription("Текущая громкость: **" + player.getVolume() + "**").setColor(DEFAULT).build()).queue();
        else
        {
            try
            {
                int newVolume = Math.max(1, Math.min(100, event.getOption("value").getAsInt()));
                int oldVolume = player.getVolume();
                Configuration.CURRENT_VOLUME = newVolume;
                Configuration.save();
                player.setVolume(newVolume);
                event.replyEmbeds(new EmbedBuilder().setDescription("Громкость изменена с `" + oldVolume + "` на `" + newVolume + "`").setColor(DEFAULT).build()).queue();
            }
            catch (NumberFormatException e)
            {
                event.replyEmbeds(new EmbedBuilder().setDescription("`" + event.getOption("value").getAsInt() + "` не является допустимым целым числом. (10 - 100)").setColor(DEFAULT).build()).setEphemeral(true).queue();
            }
        }
    }
}
