package ru.kelcuprum.alina;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ru.kelcuprum.alina.commands.*;
import ru.kelcuprum.alina.commands.Queue;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;
import ru.kelcuprum.alina.music.TrackScheduler;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter
{
    @Getter
    public static JDA bot;

    public static void main(String[] args)
            throws IllegalArgumentException, LoginException, RateLimitedException, InterruptedException {
        Configuration.load();
        if(Configuration.TOKEN.isBlank()){
            throw new RuntimeException("Discord token not specified, no launch possible!");
        }
        bot = JDABuilder.createDefault(Configuration.TOKEN) // Use token provided as JVM argument
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.customStatus("ⓘ Music Bot"))
                .addEventListeners(new Main())
                .build(); // Build JDA - connect to discord
        bot.awaitReady();
        new PlayerControl();
        bot.updateCommands().addCommands(
                Commands.slash("join", "Подключение к каналу").setGuildOnly(true)
                        .addOption(OptionType.CHANNEL, "channel", "Канал", false),
                Commands.slash("play", "Воспроизвести трек").setGuildOnly(true)
                        .addOption(OptionType.STRING, "url", "Ссылка", false)
                        .addOption(OptionType.ATTACHMENT, "file", "Файл", false),
                Commands.slash("skip", "Пропустить трек").setGuildOnly(true),
                Commands.slash("quit", "Покинуть канал").setGuildOnly(true),
                Commands.slash("pause", "Пауза").setGuildOnly(true),
                Commands.slash("stop", "Сброс плеера").setGuildOnly(true),
                Commands.slash("volume", "Громкость").setGuildOnly(true)
                        .addOption(OptionType.INTEGER, "value", "Кол-во громкости", false),
                Commands.slash("replay", "Перезапуск трека").setGuildOnly(true),
                Commands.slash("reset", "Сброс очереди").setGuildOnly(true),
                Commands.slash("repeat", "Повтор трека").setGuildOnly(true),
                Commands.slash("shuffle", "Перемешать очередь").setGuildOnly(true),
                Commands.slash("nowplaying", "Что сейчас играет").setGuildOnly(true),
                Commands.slash("queue", "Очередь треков").setGuildOnly(true)
        ).queue();
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()){
            case "join" -> new Join(event);
            case "play" -> new Play(event);
            case "pause" -> new Pause(event);
            case "skip" -> new Skip(event);
            case "reset" -> new Reset(event);
            case "quit" -> new Leave(event);
            case "volume" -> new Volume(event);
            case "replay" -> new Replay(event);
            case "repeat" -> new Repeat(event);
            case "stop" -> new Stop(event);
            case "nowplaying" -> new NowPlaying(event);
            case "shuffle" -> new Shuffle(event);
            case "queue" -> new Queue(event);
        }
    }

    @Override
    public void onGenericGuildVoice (GenericGuildVoiceEvent event){
        if(event.getMember().getUser().isBot() && event.getMember().getUser().equals(event.getJDA().getSelfUser())){
            Guild guild = event.getGuild();
            if(event.getMember().getVoiceState().getChannel() == null){
                GuildMusicManager mng = PlayerControl.getMusicManager(guild);
                TrackScheduler scheduler = mng.scheduler;
                AudioPlayer player = mng.player;
                guild.getAudioManager().setSendingHandler(null);
                guild.getAudioManager().closeAudioConnection();
                scheduler.queue.clear();
                player.destroy();
                PlayerControl.musicManagers.remove(guild.getId());
                System.out.printf("Плеер для %s была сброшена!%n", guild.getName());
            } else {
                System.out.printf("Я была подключена к  %s на сервере %s!%n", guild.getName(), event.getMember().getVoiceState().getChannel().getName());
            }
        }
    }

    public interface Colors {
        int DEFAULT = 0xFF6A1D31;
        int LIVE = 0xFFfc1a47;
        int TRACK = 0xFF79c738;
        int PAUSE = 0xFFf1ae31;
    }
}