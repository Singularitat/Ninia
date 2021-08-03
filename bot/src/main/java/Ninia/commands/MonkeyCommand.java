package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class MonkeyCommand extends Command {
    public MonkeyCommand() {
        this.name = "monkey";
        this.help = "Gets a random monkey image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://api.monkedev.com/attachments/monkey").asJson().getBody().getObject().getString("url")).queue();
    }
}
