package ru.kelcuprum.alina.commands.mods;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
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
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            JsonObject version = WebAPI.getJsonObject(String.format("%s/version/%s" , Alina.config.getString("modrinth-api", "https://staging-api.modrinth.com/v2"), event.getOption("id").getAsString()));
            JsonObject author = WebAPI.getJsonObject(String.format("%s/user/%s" , Alina.config.getString("modrinth-api", "https://staging-api.modrinth.com/v2"), version.get("author_id").getAsString()));
            JsonObject project = WebAPI.getJsonObject(String.format("%s/project/%s" , Alina.config.getString("modrinth-api", "https://staging-api.modrinth.com/v2"), version.get("project_id").getAsString()));
            String type = version.get("version_type").getAsString();
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(author.get("username").getAsString(), String.format("%s/user/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), author.get("username").getAsString()), author.get("avatar_url").getAsString())
                    .setTitle(String.format("%s > %s", project.get("title").getAsString(), version.get("name").getAsString()), String.format("%s/project/%s/version/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString(), version.get("id").getAsString()))
                    .setColor(type.equals("alpha") ? Colors.GROUPIE : type.equals("beta") ? Colors.CLOWNFISH : Colors.SEADRIVE)
                    .setDescription(version.get("changelog").getAsString());
            boolean isPermissions = event.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE);
            event.replyEmbeds(embed.build()).setContent((event.getOption("role") != null && isPermissions) ? event.getOption("role").getAsRole().getAsMention() : "")
                    .addActionRow(
                            Button.link(String.format("%s/project/%s/version/%s", Alina.config.getString("modrinth-site", "https://staging.modrinth.com"), project.get("id").getAsString(), version.get("id").getAsString()), Alina.localization.getLocalization("command.changelog.more")).withEmoji(Emoji.fromUnicode("🌐")),
                            Button.link(version.getAsJsonArray("files").get(0).getAsJsonObject().get("url").getAsString(), Alina.localization.getLocalization("command.changelog.download"))
                    )
                    .queue();
        } catch (Exception ex){
            this.executeError(event, ex);
        }
    }
}
