package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;


public class ChooseCommand extends Command {

    public ChooseCommand() {
        this.name = "choose";
        this.help = "Chooses between items";
        this.arguments = "<item> <item> ...";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage("You didn't give me any choices!").queue();
        } else {
            String[] items = event.getArgs().split("\\s+");
            event.getChannel().sendMessage(items[(int)(Math.random() * items.length)]).queue();
        }
    }

}
