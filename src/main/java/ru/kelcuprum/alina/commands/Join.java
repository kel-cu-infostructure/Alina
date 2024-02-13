package ru.kelcuprum.alina.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;

import static ru.kelcuprum.alina.Main.Colors.DEFAULT;

public class Join {
    public Join(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        VoiceChannel chan;
        if (event.getOption("channel") == null) {
            if (event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
                event.replyEmbeds(new EmbedBuilder().setDescription("Вы не подключены к голосовому каналу").setColor(DEFAULT).build()).setEphemeral(true).queue();
                return;
            } else chan = event.getMember().getVoiceState().getChannel().asVoiceChannel();
        } else {
            if (event.getOption("channel").getAsChannel().getType().name().equals("VOICE")) {
                event.replyEmbeds(new EmbedBuilder().setDescription("Это не голосовой канал").setColor(DEFAULT).build()).setEphemeral(true).queue();
                return;
            } else chan = event.getOption("channel").getAsChannel().asVoiceChannel();
        }
        guild.getAudioManager().setSendingHandler(mng.sendHandler);

        try {
            guild.getAudioManager().openAudioConnection(chan);
            event.replyEmbeds(new EmbedBuilder().setDescription("Я подключилась к " + chan.getAsMention()).setColor(DEFAULT).build()).queue();
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                event.replyEmbeds(new EmbedBuilder().setDescription("У меня нет прав чтобы присоединится к каналу " + chan.getAsMention()).setColor(DEFAULT).build()).setEphemeral(true).queue();
            }
        }
    }
}
