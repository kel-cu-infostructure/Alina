package ru.kelcuprum.alina.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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

public class Replay extends AbstractCommand {
    public Replay() {
        super("replay", Alina.localization.getLocalization("command.replay.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription()).setGuildOnly(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        TrackScheduler scheduler = mng.scheduler;
        AudioPlayer player = mng.player;

        AudioTrack track = player.getPlayingTrack();
        if (track == null)
            track = scheduler.lastTrack;

        if (track != null)
        {
            event.replyEmbeds(new EmbedBuilder().setDescription(String.format( Alina.localization.getLocalization("command.replay.done"), track.getInfo().title)).setColor(DEFAULT).build()).setEphemeral(false).queue();;
            player.playTrack(track.makeClone());
        }
        else
        {
            event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.replay.nothing_playing")).setColor(DEFAULT).build()).setEphemeral(true).queue();;
        }
    }
}
