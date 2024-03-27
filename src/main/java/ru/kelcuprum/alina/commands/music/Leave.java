package ru.kelcuprum.alina.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.commands.AbstractCommand;

public class Leave extends AbstractCommand {
    public Leave() {
        super("leave", Alina.localization.getLocalization("command.leave.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription()).setGuildOnly(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        guild.getAudioManager().setSendingHandler(null);
        guild.getAudioManager().closeAudioConnection();
        event.reply(Alina.localization.getLocalization("command.leave.bye")).queue();
    }
}
