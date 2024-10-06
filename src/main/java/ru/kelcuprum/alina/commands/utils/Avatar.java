package ru.kelcuprum.alina.commands.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.commands.AbstractCommand;

import static ru.kelcuprum.alina.Colors.SEADRIVE;

public class Avatar extends AbstractCommand {
    public Avatar() {
        super("avatar", Alina.localization.getLocalization("command.avatar.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription()).addOption(OptionType.USER, "user", Alina.localization.getLocalization("command.avatar.user.description"), false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        User user = event.getOption("user", OptionMapping::getAsUser) != null ? event.getOption("user", OptionMapping::getAsUser) : event.getUser();
        assert user != null;
        builder.setTitle(String.format(Alina.localization.getLocalization("command.avatar"), user.getName()))
                .setColor(SEADRIVE)
                .setImage(user.getAvatarUrl()+"?size=1024");
        event.replyEmbeds(builder.build()).queue();
    }
}
