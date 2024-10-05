package ru.kelcuprum.alina.listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.music.GuildMusicManager;
import ru.kelcuprum.alina.music.PlayerControl;
import ru.kelcuprum.alina.music.TrackScheduler;

public class VoiceListeners extends ListenerAdapter {
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
                Alina.log(String.format("The player for %s has been reset!", guild.getName()));
            } else {
                Alina.log(String.format("I was connected to %s on server %s!", event.getMember().getVoiceState().getChannel().getName(), guild.getName()));
            }
        }
    }
}
