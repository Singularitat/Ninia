package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class CatCommand extends Command {
    public CatCommand() {
        this.name = "cat";
        this.help = "Gets a random cat image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://thatcopy.pw/catapi/rest/").asJson().getBody().getObject().getString("webpurl")).queue();
    }
}
