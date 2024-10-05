package ru.kelcuprum.alina.commands.about;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.Colors;
import ru.kelcuprum.alina.commands.AbstractCommand;

public class Me extends AbstractCommand {
    public Me() {
        super("bot", Alina.localization.getLocalization("command.me.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription()));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Colors.ALINA);
        embedBuilder.setTitle(Alina.localization.getLocalization("command.me.title"));
        embedBuilder.setDescription(Alina.localization.getLocalization("command.me"));
        embedBuilder.addField(Alina.localization.getLocalization("command.me.version"), Alina.release.getString("version", "not found :("), true);
        embedBuilder.setImage(Alina.config.getString("ME.BANNER", "https://wf.kelcu.ru/other/bot/banner-beta.png"));
//        embedBuilder.setColor()
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
