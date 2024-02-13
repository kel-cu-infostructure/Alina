package ru.kelcuprum.alina.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;

import static ru.kelcuprum.alina.Main.Colors.DEFAULT;

public class Play {
    public Play(SlashCommandInteractionEvent event){
        String url = event.getOption("url") != null ? event.getOption("url").getAsString() : event.getOption("file") != null ? event.getOption("file").getAsAttachment().getUrl() : "";
        if(url.isBlank()){
            event.replyEmbeds(new EmbedBuilder().setDescription("Задан пустой запрос!").setColor(DEFAULT).build()).setEphemeral(true).queue();
        } else {
            Guild guild = event.getGuild();
            GuildMusicManager mng = PlayerControl.getMusicManager(guild);
            PlayerControl.loadAndPlay(mng, event, url, true);
        }
    }
}
