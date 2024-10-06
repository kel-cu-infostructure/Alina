package ru.kelcuprum.alina.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.commands.AbstractCommand;
import ru.kelcuprum.alina.commands.about.Bot;
import ru.kelcuprum.alina.commands.mods.*;
import ru.kelcuprum.alina.commands.music.*;
import ru.kelcuprum.alina.commands.utils.Artwork;
import ru.kelcuprum.alina.commands.utils.Avatar;

//import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SlashCommands extends ListenerAdapter {
    public List<AbstractCommand> commands = new ArrayList<>();
    public SlashCommands(JDA bot) {
        Collection<CommandData> commandData = new ArrayList<>();
//        InputStream changelog = Alina.class.getResourceAsStream("/changelog.md");
        commands.add(new Bot());
        if(Alina.config.getBoolean("MODULES.UTILS", true)){
            commands.add(new Avatar());
            commands.add(new Artwork());
        }
        if(Alina.config.getBoolean("MODULES.MUSIC", true)){
            commands.add(new Join());
            commands.add(new Leave());
            commands.add(new NowPlaying());
            commands.add(new Pause());
            commands.add(new Play());
            commands.add(new Queue());
            commands.add(new Repeat());
            commands.add(new Replay());
            commands.add(new Reset());
            commands.add(new Shuffle());
            commands.add(new Skip());
            commands.add(new Stop());
            commands.add(new Volume());
        }
        if(Alina.config.getBoolean("MODULES.MODRINTH", true)) {
            commands.add(new ChangeLog());
            commands.add(new Project());
        }

        for(AbstractCommand command : commands){
            Alina.log(String.format("Register command /%s, description: %s", command.getName(), command.getDescription()));
            commandData.add(command.commandData);
        }
        bot.updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
        for(AbstractCommand command : commands){
            if(command.getName().equalsIgnoreCase(event.getName())) {
                command.execute(event);
                return;
            }
        }
    }

}
