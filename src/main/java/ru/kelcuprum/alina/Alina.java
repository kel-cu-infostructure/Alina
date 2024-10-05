package ru.kelcuprum.alina;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ActivityFlag;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import ru.kelcuprum.alina.config.Config;
import ru.kelcuprum.alina.config.GsonHelper;
import ru.kelcuprum.alina.listeners.SlashCommands;
import ru.kelcuprum.alina.listeners.VoiceListeners;
import ru.kelcuprum.alina.music.PlayerControl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public class Alina extends ListenerAdapter
{
    @Getter
    public static JDA bot;
    public static Config release = new Config(new JsonObject());

    public static Logger LOG = LoggerFactory.getLogger("Alina");
    public static void log(String message) { log(message, Level.INFO);}
    public static void log(String message, Level level) {
        switch (level){
            case INFO -> LOG.info(message);
            case WARN -> LOG.warn(message);
            case ERROR -> LOG.error(message);
            case DEBUG -> LOG.debug(message);
            case TRACE -> LOG.trace(message);
        }
    }
    public static void log(Exception message) {log(message, Level.ERROR);}
    public static void log(Exception message, Level level) {
        switch (level){
            case INFO -> LOG.info(message.getLocalizedMessage(), message.fillInStackTrace());
            case WARN -> LOG.warn(message.getLocalizedMessage(), message.fillInStackTrace());
            case ERROR -> LOG.error(message.getLocalizedMessage(), message.fillInStackTrace());
            case DEBUG -> LOG.debug(message.getLocalizedMessage(), message.fillInStackTrace());
            case TRACE -> LOG.trace(message.getLocalizedMessage(), message.fillInStackTrace());
        }
    }
    public static Localization localization = new Localization("./localization.json");
    public static Config config = new Config("./alina.json");
    public static Config guildVolume = new Config("./guildVolumes.json");
    public static void main(String[] args)
            throws IllegalArgumentException, InterruptedException {
        try {
            InputStream releaseFile = Alina.class.getResourceAsStream("/release.json");
            release = new Config(GsonHelper.parse(new String(releaseFile.readAllBytes(), StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(config.getString("TOKEN", "").isBlank())
            throw new RuntimeException("Discord token not specified, no launch possible!");
        new PlayerControl();
        bot = JDABuilder.createDefault(config.getString("TOKEN", "")) // Use token provided as JVM argument
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.customStatus(localization.getLocalization("bot.info")))
                .addEventListeners(new VoiceListeners())
                .build(); // Build JDA - connect to discord
        bot.awaitReady();
        log(String.format("Hello, world! My version: %s", release.getString("version", "You're a failure!")));
        bot.addEventListener(new SlashCommands(bot));
    }


    public interface Colors {
        int DEFAULT = 0xFF6A1D31;
        int LIVE  = 0xFFfc1a47;
        int EXCEPTION = LIVE;
        int TRACK = 0xFF79c738;
        int PAUSE = 0xFFf1ae31;
    }

    public static StringBuilder getLoaders(JsonArray loaders){
        StringBuilder loadersNames = new StringBuilder();
        boolean isFirstLoa = true;
        for(JsonElement loader : loaders){
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
                loadersNames.append(loader_name);
            } else loadersNames.append(", ").append(loader_name);
        }
        return loadersNames;
    }
    public static StringBuilder getMCVersions(JsonArray versions){
        StringBuilder mc_versions = new StringBuilder();
        boolean isFirstVer = true;
        if(versions.size() <= 3) {
            for (JsonElement mc_version : versions) {
                if (isFirstVer) {
                    isFirstVer = false;
                    mc_versions.append(mc_version.getAsString());
                } else mc_versions.append(", ").append(mc_version.getAsString());
            }
        } else {
            mc_versions.append(versions.get(0).getAsString()).append(" - ").append(versions.get(versions.size()-1).getAsString());
        }
        return mc_versions;
    }
}