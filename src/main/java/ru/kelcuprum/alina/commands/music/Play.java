package ru.kelcuprum.alina.commands.music;

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

public class Play extends AbstractCommand {
    public Play() {
        super("play", Alina.localization.getLocalization("command.play.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription())
                .addOption(OptionType.STRING, "url", Alina.localization.getLocalization("command.play.description.url"), false)
                .addOption(OptionType.ATTACHMENT, "file", Alina.localization.getLocalization("command.play.description.file"), false)
                .setGuildOnly(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String url = event.getOption("url") != null ? event.getOption("url").getAsString() : event.getOption("file") != null ? event.getOption("file").getAsAttachment().getUrl() : "";
        if(url.isBlank()){
            event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.play.blank_request")).setColor(DEFAULT).build()).setEphemeral(true).queue();
        } else {
            Guild guild = event.getGuild();
            GuildMusicManager mng = PlayerControl.getMusicManager(guild);
            PlayerControl.loadAndPlay(mng, event, url, true);
        }
    }
}
