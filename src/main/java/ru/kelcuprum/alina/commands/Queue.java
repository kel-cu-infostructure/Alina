package ru.kelcuprum.alina.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.MusicParser;
import ru.kelcuprum.alina.music.PlayerControl;
import ru.kelcuprum.alina.music.TrackScheduler;

import static ru.kelcuprum.alina.Main.Colors.DEFAULT;

public class Queue {
    public Queue(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        GuildMusicManager mng = PlayerControl.getMusicManager(guild);
        TrackScheduler scheduler = mng.scheduler;
        java.util.Queue<AudioTrack> queue = scheduler.queue;
        synchronized (queue)
        {
            boolean isEphemeral = true;
            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(DEFAULT);
            if (queue.isEmpty())
            {
                embedBuilder.setDescription("Очередь пустая!");
            }
            else
            {
                isEphemeral = false;
                int trackCount = 0;
                long queueLength = 0;
                StringBuilder sb = new StringBuilder();
                embedBuilder.setTitle("Текущая очередь:");
                for (AudioTrack track : queue)
                {
                    if(!track.getInfo().isStream) queueLength += track.getDuration();
                    if (trackCount < 10)
                    {
                        sb.append("- [");
                        if(!MusicParser.isAuthorNull(track)) sb.append("**").append(MusicParser.getAuthor(track)).append(" »** ");
                        sb.append(track.getInfo().title).append("](").append(track.getInfo().uri).append(")\n");
                        trackCount++;
                    }
                }
                if(trackCount >= 10) sb.append("...");
                embedBuilder.setDescription(sb.toString())
                        .setFooter("Общее время очереди: "+PlayerControl.getTimestamp(queueLength)+" / Длниа: "+queue.size());
            }
            event.replyEmbeds(embedBuilder.build()).setEphemeral(isEphemeral).queue();
        }
    }
}
