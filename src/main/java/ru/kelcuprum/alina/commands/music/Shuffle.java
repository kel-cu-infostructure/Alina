package ru.kelcuprum.alina.commands.music;

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

public class Shuffle extends AbstractCommand {
    public Shuffle() {
        super("shuffle", Alina.localization.getLocalization("command.shuffle.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription()).setGuildOnly(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        TrackScheduler scheduler = mng.scheduler;
        if (scheduler.queue.isEmpty())
        {
            event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.shuffle.queue_blank")).setColor(DEFAULT).build()).setEphemeral(true).queue();;
            return;
        }

        scheduler.shuffle();
        event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.shuffle.done")).setColor(DEFAULT).build()).setEphemeral(false).queue();
    }
}
