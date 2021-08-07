package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class RacoonCommand extends Command {
    public RacoonCommand() {
        this.name = "racoon";
        this.help = "Gets a random racoon image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://some-random-api.ml/img/racoon").asJson().getBody().getObject().getString("link")).queue();
    }
}
