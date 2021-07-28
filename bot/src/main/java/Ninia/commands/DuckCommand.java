package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class DuckCommand extends Command {
    public DuckCommand() {
        this.name = "duck";
        this.help = "Gets a random duck image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://random-d.uk/api/v1/random?type=png").asJson().getBody().getObject().getString("url")).queue();
    }
}
