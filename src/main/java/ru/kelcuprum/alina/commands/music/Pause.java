package ru.kelcuprum.alina.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.commands.AbstractCommand;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;

import static ru.kelcuprum.alina.Alina.Colors.DEFAULT;

public class Pause extends AbstractCommand {
    public Pause() {
        super("pause", Alina.localization.getLocalization("command.pause.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription()).setGuildOnly(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        AudioPlayer player = mng.player;
        if (player.getPlayingTrack() == null)
        {
            event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.pause.nothing_playing")).setColor(DEFAULT).build()).setEphemeral(true).queue();
            return;
        }

        player.setPaused(!player.isPaused());
        event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.pause.state_"+player.isPaused())).setColor(DEFAULT).build()).queue();
    }
}
