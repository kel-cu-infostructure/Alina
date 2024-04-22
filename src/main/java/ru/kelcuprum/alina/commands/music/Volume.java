package ru.kelcuprum.alina.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.commands.AbstractCommand;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;

import static ru.kelcuprum.alina.Alina.Colors.DEFAULT;

public class Volume extends AbstractCommand {
    public Volume() {
        super("volume", Alina.localization.getLocalization("command.volume.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription())
                .addOption(OptionType.INTEGER, "value", Alina.localization.getLocalization("command.volume.description.value"), false)
                .setGuildOnly(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        AudioPlayer player = mng.player;
        if (event.getOption("value") == null)  event.replyEmbeds(new EmbedBuilder().setDescription(String.format(Alina.localization.getLocalization("command.volume.state"), player.getVolume())).setColor(DEFAULT).build()).queue();
        else
        {
            try
            {
                int newVolume = Math.max(1, Math.min(100, event.getOption("value").getAsInt()));
                int oldVolume = player.getVolume();
                Alina.config.setNumber("MUSIC.CURRENT_VOLUME", newVolume);
                player.setVolume(newVolume);
                event.replyEmbeds(new EmbedBuilder().setDescription(String.format(Alina.localization.getLocalization("command.volume.change"), newVolume, oldVolume)).setColor(DEFAULT).build()).queue();
            }
            catch (NumberFormatException e)
            {
//                event.replyEmbeds(new EmbedBuilder().setDescription("`" + event.getOption("value").getAsInt() + "` не является допустимым целым числом. (10 - 100)").setColor(DEFAULT).build()).setEphemeral(true).queue();
                event.replyEmbeds(new EmbedBuilder().setDescription(String.format(Alina.localization.getLocalization("command.volume.non_correct"), event.getOption("value").getAsInt())).setColor(DEFAULT).build()).setEphemeral(true).queue();
            }
        }
    }
}
