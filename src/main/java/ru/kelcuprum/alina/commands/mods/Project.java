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
            StringBuilder mc_versions = new StringBuilder();
            boolean isFirstVer = true;
            JsonArray mc_versions_array = project.getAsJsonArray("game_versions");
            if(mc_versions_array.size() <= 3) {
                for (JsonElement mc_version : mc_versions_array) {
                    if (isFirstVer) {
                        isFirstVer = false;
                        mc_versions.append(mc_version.getAsString());
                    } else mc_versions.append(", ").append(mc_version.getAsString());
                }
            } else {
                mc_versions.append(mc_versions_array.get(0).getAsString()).append(" - ").append(mc_versions_array.get(mc_versions_array.size()-1).getAsString());
            }
            StringBuilder loaders = new StringBuilder();
            boolean isFirstLoa = true;
            for(JsonElement loader : project.getAsJsonArray("loaders")){
                String loader_name = switch (loader.getAsString()){
                    case "fabric" -> "Fabric";
                    case "quilt" -> "Quilt";
                    case "forge" -> "Forge";
                    case "neoforge" -> "NeoForge";
                    case "modloader" -> "Risugami's ModLoader";
                    case "rift" -> "Rift";
                    case "liteloader" -> "LiteLoader";
                    case "bukkit" -> "Bukkit";
                    case "bungeecord" -> "BungeeCord";
                    case "folia" -> "Folia";
                    case "paper" -> "Paper";
                    case "purpur" -> "Purpur";
                    case "spigot" -> "Spigot";
                    case "sponge" -> "Sponge";
                    case "velocity" -> "Velocity";
                    case "waterfall" -> "Waterfall";
                    default -> loader.getAsString();
                };
                if(isFirstLoa){
                    isFirstLoa = false;
                    loaders.append(loader_name);
                } else loaders.append(", ").append(loader_name);
            }
            embed.addField(Alina.localization.getLocalization("command.project.authors"), author.toString(), true);
            embed.addField(Alina.localization.getLocalization("command.project.mc_versions"), mc_versions.toString(), true);
            embed.addField(Alina.localization.getLocalization("command.project.loaders"), loaders.toString(), true);

            embed.addField(Alina.localization.getLocalization("command.project.downloads"), String.valueOf(project.get("downloads").getAsInt()), true);
            embed.addField(Alina.localization.getLocalization("command.project.followers"), String.valueOf(project.get("followers").getAsInt()), true);
            embed.setFooter(Alina.localization.getLocalization("command.project.license")+": "+project.get("license").getAsJsonObject().get("name").getAsString());
            event.replyEmbeds(embed.build()).addActionRow(
                    Button.link(String.format("%s/project/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString()), "Modrinth").withEmoji(Emoji.fromUnicode("🌐")),
                    Button.link(String.format("%s/project/%s/versions", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString()), Alina.localization.getLocalization("command.project.versions")).withEmoji(Emoji.fromUnicode("📃"))
            ).queue();
        } catch (Exception ex){
            this.executeError(event, ex);
        }
    }
}
