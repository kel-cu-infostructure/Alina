package ru.kelcuprum.alina.commands.mods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.Colors;
import ru.kelcuprum.alina.WebAPI;
import ru.kelcuprum.alina.commands.AbstractCommand;

public class ChangeLog extends AbstractCommand {
    public ChangeLog(){
        super("change-log", Alina.localization.getLocalization("command.changelog.description"));
        this.setCommandData(
                Commands.slash(this.getName(), this.getDescription())
                        .addOption(OptionType.STRING, "id", Alina.localization.getLocalization("command.changelog.description.id"), true)
                        .addOption(OptionType.ROLE, "role", Alina.localization.getLocalization("command.changelog.description.role"), false)
                        .addOption(OptionType.BOOLEAN, "show_loader", Alina.localization.getLocalization("command.changelog.description.show_loader"), false)
                        .addOption(OptionType.BOOLEAN, "show_versions", Alina.localization.getLocalization("command.changelog.description.show_versions"), false)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            JsonObject version = WebAPI.getJsonObject(String.format("%s/version/%s" , Alina.config.getString("modrinth-api", "https://staging-api.modrinth.com/v2"), event.getOption("id").getAsString()));
            JsonObject author = WebAPI.getJsonObject(String.format("%s/user/%s" , Alina.config.getString("modrinth-api", "https://staging-api.modrinth.com/v2"), version.get("author_id").getAsString()));
            JsonObject project = WebAPI.getJsonObject(String.format("%s/project/%s" , Alina.config.getString("modrinth-api", "https://staging-api.modrinth.com/v2"), version.get("project_id").getAsString()));
            String type = version.get("version_type").getAsString();
            String versionName = version.get("name").getAsString();
            StringBuilder versionNameForTitle = new StringBuilder();
            for(String arg : versionName.split(" ")){
                if(!isLoaderName(arg, version)) versionNameForTitle.append(arg).append(" ");
                else Alina.log(arg);
            }
            if(versionNameForTitle.isEmpty()) versionNameForTitle = new StringBuilder(versionName);
            String ver = versionNameForTitle.toString().replaceAll("\\[[^\\[]+(?=])]", "")
                    .replaceAll("\\([^\\[]+(?=])\\)", "");
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(String.format("%s > %s", project.get("title").getAsString(), ver), String.format("%s/project/%s/version/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString(), version.get("id").getAsString()), project.get("icon_url").getAsString())
                    .setColor(type.equals("alpha") ? Colors.GROUPIE : type.equals("beta") ? Colors.CLOWNFISH : Colors.SEADRIVE)
                    .setDescription(version.get("changelog").getAsString())
                    .setFooter(author.get("username").getAsString(), author.get("avatar_url").getAsString());

            StringBuilder mc_versions = Alina.getMCVersions(project.getAsJsonArray("game_versions"));
            if(event.getOption("show_versions") != null && event.getOption("show_loader").getAsBoolean())
                embed.addField(Alina.localization.getLocalization("command.project.mc_versions"), mc_versions.toString(), true);

            if(event.getOption("show_loader") != null && event.getOption("show_loader").getAsBoolean())
                embed.addField(Alina.localization.getLocalization("command.project.loaders"), Alina.getLoaders(version.getAsJsonArray("loaders")).toString(), true);
            boolean isForPublic = event.isFromGuild() && event.getChannel() instanceof NewsChannel;
            boolean isPermissions = event.isFromGuild() && event.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE);
            if(isForPublic){
                event.reply(":+1:").setEphemeral(true).queue();
                event.getChannel().sendMessageEmbeds(embed.build()).setContent((event.getOption("role") != null && isPermissions) ? event.getOption("role").getAsRole().getAsMention() : "")
                        .addActionRow(
                                Button.link(String.format("%s/project/%s/version/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString(), version.get("id").getAsString()), Alina.localization.getLocalization("command.changelog.more")).withEmoji(Emoji.fromUnicode("🌐")),
                                Button.link(version.getAsJsonArray("files").get(0).getAsJsonObject().get("url").getAsString(), Alina.localization.getLocalization("command.changelog.download"))
                        ).queue();
            } else {
                event.replyEmbeds(embed.build()).setContent((event.getOption("role") != null && isPermissions) ? event.getOption("role").getAsRole().getAsMention() : "")
                        .addActionRow(
                                Button.link(String.format("%s/project/%s/version/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString(), version.get("id").getAsString()), Alina.localization.getLocalization("command.changelog.more")).withEmoji(Emoji.fromUnicode("🌐")),
                                Button.link(version.getAsJsonArray("files").get(0).getAsJsonObject().get("url").getAsString(), Alina.localization.getLocalization("command.changelog.download"))
                        )
                        .queue();
            }
        } catch (Exception ex){
            this.executeError(event, ex);
        }
    }
    public boolean isLoaderName(String arg, JsonObject version){
        boolean bl = false;
        for(JsonElement element : version.getAsJsonArray("loaders")){
            if(arg.toLowerCase().contains(element.getAsString())){
                bl = true;
                break;
            }
        }
        return bl;
    }
}
