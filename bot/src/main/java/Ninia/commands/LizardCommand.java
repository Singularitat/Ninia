package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class LizardCommand extends Command {
    public LizardCommand() {
        this.name = "lizard";
        this.help = "Gets a random lizard image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://nekos.life/api/v2/img/lizard").asJson().getBody().getObject().getString("url")).queue();
    }
}
