package ru.kelcuprum.alina.commands;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ru.kelcuprum.alina.Alina;
import ru.kelcuprum.alina.Colors;

public abstract class AbstractCommand {
    private String name;
    private String description;
    public CommandData commandData;
    public AbstractCommand(String name, String description){
        this(name, description, null);
    }
    public AbstractCommand(String name, String description, CommandData commandData){
        setName(name);
        setDescription(description);
        if(commandData != null) setCommandData(commandData);
    }
    // Execute
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("This abstract command 🤓🤓").queue();
    }
    public void executeError(SlashCommandInteractionEvent event, Exception exception){
        Alina.log(exception);
        event.replyEmbeds(new EmbedBuilder().setTitle("Exception").setDescription(String.format("> %s", exception.getLocalizedMessage())).setColor(Alina.Colors.EXCEPTION).build()).setEphemeral(true).queue();
    }

    // Name
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    // Name
    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    // Command data
    public void setCommandData(CommandData commandData) { this.commandData = commandData; }

    public CommandData getCommandData() { return commandData; }

}
