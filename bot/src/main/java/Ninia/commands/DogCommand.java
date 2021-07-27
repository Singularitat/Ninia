package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class DogCommand extends Command {
    public DogCommand() {
        this.name = "dog";
        this.help = "Gets a random dog image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://random.dog/woof.json").asJson().getBody().getObject().getString("url")).queue();
    }
}
