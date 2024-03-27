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
import ru.kelcuprum.alina.music.TrackScheduler;

import static ru.kelcuprum.alina.Alina.Colors.DEFAULT;

public class Stop extends AbstractCommand {
    public Stop() {
        super("stop", Alina.localization.getLocalization("command.stop.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription()).setGuildOnly(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
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
        event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.stop.done")).setColor(DEFAULT).build()).setEphemeral(false).queue();
    }
}
