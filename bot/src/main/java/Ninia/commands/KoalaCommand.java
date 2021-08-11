package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class KoalaCommand extends Command {
    public KoalaCommand() {
        this.name = "koala";
        this.help = "Gets a random koala image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://some-random-api.ml/img/koala/").asJson().getBody().getObject().getString("link")).queue();
    }
}
