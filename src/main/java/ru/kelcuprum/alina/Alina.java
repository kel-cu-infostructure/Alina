package ru.kelcuprum.alina;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import ru.kelcuprum.alina.config.Config;
import ru.kelcuprum.alina.listeners.SlashCommands;
import ru.kelcuprum.alina.listeners.VoiceListeners;
import ru.kelcuprum.alina.music.PlayerControl;

public class Alina extends ListenerAdapter
{
    @Getter
    public static JDA bot;

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
    public static void main(String[] args)
            throws IllegalArgumentException, InterruptedException {
        if(config.getString("TOKEN", "").isBlank()){
            throw new RuntimeException("Discord token not specified, no launch possible!");
        }
        new PlayerControl();
        bot = JDABuilder.createDefault(config.getString("TOKEN", "")) // Use token provided as JVM argument
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.customStatus(localization.getLocalization("bot.info")))
                .addEventListeners(new VoiceListeners())
                .build(); // Build JDA - connect to discord
        bot.awaitReady();
        log("Hello, world");
        bot.addEventListener(new SlashCommands(bot));
    }


    public interface Colors {
        int DEFAULT = 0xFF6A1D31;
        int LIVE  = 0xFFfc1a47;
        int EXCEPTION = LIVE;
        int TRACK = 0xFF79c738;
        int PAUSE = 0xFFf1ae31;
    }
}