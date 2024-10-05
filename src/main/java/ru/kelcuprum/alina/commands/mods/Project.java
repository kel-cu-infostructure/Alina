package ru.kelcuprum.alina.commands.mods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.Colors;
import ru.kelcuprum.alina.WebAPI;
import ru.kelcuprum.alina.commands.AbstractCommand;

public class Project extends AbstractCommand {
    public Project(){
        super("project", Alina.localization.getLocalization("command.project.description"));
        this.setCommandData(
                Commands.slash(this.getName(), this.getDescription())
                        .addOption(OptionType.STRING, "id", Alina.localization.getLocalization("command.project.description.id"), true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            String projectID = event.getOption("id").getAsString();
            JsonObject project = WebAPI.getJsonObject(String.format("%s/project/%s" , Alina.config.getString("modrinth-api", "https://staging-api.modrinth.com/v2"), projectID));
            JsonArray authors = WebAPI.getJsonArray(String.format("%s/project/%s/members" , Alina.config.getString("modrinth-api", "https://staging-api.modrinth.com/v2"), projectID));
            EmbedBuilder embed = new EmbedBuilder().setTitle(project.get("title").getAsString(), String.format("%s/project/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString()))
                    .setThumbnail(project.get("icon_url").getAsString()).setColor(project.get("color").isJsonNull() ? Colors.SEADRIVE : project.get("color").getAsInt())
                    .setDescription(project.get("description").getAsString());
            StringBuilder author = new StringBuilder();
            boolean isFirstAut = true;
            for(JsonElement user : authors){
                if(isFirstAut){
                    isFirstAut = false;
                    author.append(user.getAsJsonObject().getAsJsonObject("user").get("username").getAsString());
                } else author.append(", ").append(user.getAsJsonObject().getAsJsonObject("user").get("username").getAsString());
            }
            embed.addField(Alina.localization.getLocalization("command.project.authors"), author.toString(), true);
            embed.addField(Alina.localization.getLocalization("command.project.mc_versions"), Alina.getMCVersions(project.getAsJsonArray("game_versions")).toString(), true);
            embed.addField(Alina.localization.getLocalization("command.project.loaders"), Alina.getLoaders(project.getAsJsonArray("loaders")).toString(), true);

            embed.addField(Alina.localization.getLocalization("command.project.downloads"), String.valueOf(project.get("downloads").getAsInt()), true);
            embed.addField(Alina.localization.getLocalization("command.project.followers"), String.valueOf(project.get("followers").getAsInt()), true);
            String licence = project.get("license").getAsJsonObject().get("name").getAsString();
            if(licence.isBlank()){
                if(project.get("license").getAsJsonObject().get("id").getAsString().startsWith("LicenseRef-")){
                    licence = project.get("license").getAsJsonObject().get("id").getAsString().replace("LicenseRef-", "").replace("-", " ");
                } else licence = "No Fishes?";
            }
            embed.setFooter(Alina.localization.getLocalization("command.project.license")+": "+licence);
            String url = "";
            for(JsonElement element : project.get("gallery").getAsJsonArray()){
                JsonObject object = element.getAsJsonObject();
                if(object.get("featured").getAsBoolean()) url = object.get("url").getAsString();
            }
            if(url.isBlank() && !project.get("gallery").getAsJsonArray().isEmpty()) url = project.get("gallery").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
            if(!url.isBlank()) embed.setImage(url);
            event.replyEmbeds(embed.build()).addActionRow(
                    Button.link(String.format("%s/project/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString()), "Modrinth").withEmoji(Emoji.fromUnicode("🌐")),
                    Button.link(String.format("%s/project/%s/versions", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString()), Alina.localization.getLocalization("command.project.versions")).withEmoji(Emoji.fromUnicode("📃"))
            ).queue();
        } catch (Exception ex){
            this.executeError(event, ex);
        }
    }
}
