package ru.kelcuprum.alina.commands.utils;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.WebAPI;
import ru.kelcuprum.alina.commands.AbstractCommand;
import ru.kelcuprum.alina.commands.about.Bot;

import static ru.kelcuprum.alina.Colors.*;

public class Artwork extends AbstractCommand {
    public Artwork() {
        super("artwork", Alina.localization.getLocalization("command.artwork.description"));
        this.setCommandData(Commands.slash(this.getName(), this.getDescription())
                .addOption(OptionType.STRING, "author", Alina.localization.getLocalization("command.artwork.author.description"), true)
                .addOption(OptionType.STRING, "title", Alina.localization.getLocalization("command.artwork.title.description"), false)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        String apiUrl = "https://wplayer.ru/v2/info?author="+WebAPI.uriEncode(event.getOption("author", OptionMapping::getAsString));
        if(event.getOption("title", OptionMapping::getAsString) != null) apiUrl+=("&album="+WebAPI.uriEncode(event.getOption("title", OptionMapping::getAsString)));
        try {
            JsonObject data = WebAPI.getJsonObject(apiUrl);
            if(data.has("error")){
                JsonObject error = data.getAsJsonObject("error");
                executeError(event, new Exception(error.has("message") ? error.get("message").getAsString() : error.get("codename").getAsString()));
            } else {
                JsonObject author = data.getAsJsonObject("author");
                builder.setColor(ALINA);
                builder.setAuthor(author.get("name").getAsString(), null, author.has("artwork") ? author.get("artwork").getAsString() : null);
                if(data.has("track")){
                    JsonObject track = data.getAsJsonObject("track");
                    builder.setColor(track.get("title").getAsString().equalsIgnoreCase("circus hop") ? CLOWNFISH : ALINA);
                    builder.setTitle(track.get("title").getAsString());
                    builder.setImage(track.has("artwork") ? track.get("artwork").getAsString() : null);
                } else {
                    builder.setImage(author.has("artwork") ? author.get("artwork").getAsString() : null);
                }
                event.replyEmbeds(builder.build()).queue();
            }
        } catch (Exception ex){
            executeError(event, ex);
        }

    }
}
