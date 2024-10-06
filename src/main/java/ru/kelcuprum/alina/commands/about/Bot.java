package ru.kelcuprum.alina.commands.about;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.Colors;
import ru.kelcuprum.alina.commands.AbstractCommand;

public class Bot extends AbstractCommand {
    public Bot() {
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
        embedBuilder.addField(Alina.localization.getLocalization("command.me.uptime"), "<t:"+parseSeconds(Alina.startedTime)+":R>", true);
        embedBuilder.setImage(Alina.config.getString("ME.BANNER", "https://wf.kelcu.ru/other/bot/banner-beta.png"));
        embedBuilder.setFooter(Alina.localization.getLocalization("command.me.footer"), "https://wf.kelcu.ru/icons/clover.png");
        event.replyEmbeds(embedBuilder.build()).queue();
    }
    public long parseSeconds(long mills) {
        return (mills - (mills % 1000)) / 1000;
    }
}
