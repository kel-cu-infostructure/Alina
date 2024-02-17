package ru.kelcuprum.alina.commands;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ru.kelcuprum.alina.music.PlayerControl;

public class Search {
    public Search(SlashCommandInteractionEvent event){
        EmbedBuilder embed = new EmbedBuilder();
        String request = "";
        String service = "";
        for(OptionMapping option : event.getOptions()){
            switch (option.getName()){
                case "yandex" -> {
                    request = option.getAsString();
                    service = "yandexmusic";
                }
                case "spotify" -> {
                    request = option.getAsString();
                    service = "spotify";
                }
                case "apple" -> {
                    request = option.getAsString();
                    service = "applemusic";
                }
                case "deezer" -> {
                    request = option.getAsString();
                    service = "deezer";
                }
                default -> {
                    request = option.getAsString();
                    service = "";
                }
            }
        }
        if(service.isBlank()){
            embed.setDescription("Вы не указали сервис!");
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            return;
        }
        for(AudioSourceManager manager : PlayerControl.playerManager.getSourceManagers()){
            if(manager.getSourceName().equalsIgnoreCase(service)){
                break;
            }
        }

        event.reply(String.format("Service: %s\nRequest: %s", service, request)).queue();
    }
}
