package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class BirdCommand extends Command {
    public BirdCommand() {
        this.name = "bird";
        this.help = "Gets a random bird image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://some-random-api.ml/img/birb").asJson().getBody().getObject().getString("link")).queue();
    }
}
