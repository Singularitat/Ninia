package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;


public class SourceCommand extends Command {

    public SourceCommand() {
        this.name = "source";
        this.help = "Sends a link to the bots source";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("https://github.com/Singularitat/Ninia");
    }
}
