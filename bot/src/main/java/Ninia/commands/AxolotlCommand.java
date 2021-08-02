package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import kong.unirest.Unirest;

import net.dv8tion.jda.api.Permission;


public class AxolotlCommand extends Command {
    public AxolotlCommand() {
        this.name = "axolotl";
        this.help = "Gets a random axolotl image";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage(Unirest.get("https://axoltlapi.herokuapp.com").asJson().getBody().getObject().getString("url")).queue();
    }
}
