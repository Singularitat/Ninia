
package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class WhaleCommand extends Command {
    public WhaleCommand() {
        this.name = "whale";
        this.help = "Gets a random whale image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://some-random-api.ml/img/whale").asJson().getBody().getObject().getString("link")).queue();
    }
}
