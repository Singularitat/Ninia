package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class BunnyCommand extends Command {
    public BunnyCommand() {
        this.name = "bunny";
        this.help = "Gets a random bunny image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://api.bunnies.io/v2/loop/random/?media=webm").asJson().getBody().getObject().getJSONObject("media").getString("webm")).queue();
    }
}
