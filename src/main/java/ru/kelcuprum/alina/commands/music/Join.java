package ru.kelcuprum.alina.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.commands.AbstractCommand;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;

import static ru.kelcuprum.alina.Alina.Colors.DEFAULT;

public class Join extends AbstractCommand {
    public Join() {
        super("join", Alina.localization.getLocalization("command.join.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription())
                .addOption(OptionType.CHANNEL, "channel", Alina.localization.getLocalization("command.join.description.channel"), false)
                .setGuildOnly(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        VoiceChannel chan;
        if (event.getOption("channel") == null) {
            if (event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
                event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.join.user_not_connected")).setColor(DEFAULT).build()).setEphemeral(true).queue();
                return;
            } else chan = event.getMember().getVoiceState().getChannel().asVoiceChannel();
        } else {
            if (!event.getOption("channel").getAsChannel().getType().name().equals("VOICE")) {
                event.replyEmbeds(new EmbedBuilder().setDescription(Alina.localization.getLocalization("command.join.channel_not_voice")).setColor(DEFAULT).build()).setEphemeral(true).queue();
                return;
            } else chan = event.getOption("channel").getAsChannel().asVoiceChannel();
        }
        guild.getAudioManager().setSendingHandler(mng.sendHandler);

        try {
            guild.getAudioManager().openAudioConnection(chan);
            // Alina.localization.getLocalization("command.join.user_not_connected")
//            event.replyEmbeds(new EmbedBuilder().setDescription("Я подключилась к " + chan.getAsMention()).setColor(DEFAULT).build()).queue();
            event.replyEmbeds(new EmbedBuilder().setDescription(String.format(Alina.localization.getLocalization("command.join.connected"), chan.getAsMention())).setColor(DEFAULT).build()).queue();
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                event.replyEmbeds(new EmbedBuilder().setDescription(String.format(Alina.localization.getLocalization("command.join.no_permission"), chan.getAsMention())).setColor(DEFAULT).build()).setEphemeral(true).queue();
            }
        }
    }
}
