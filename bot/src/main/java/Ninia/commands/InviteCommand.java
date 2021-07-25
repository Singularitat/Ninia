package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;


public class InviteCommand extends Command {

    public InviteCommand() {
        this.name = "invite";
        this.help = "Sends the invite link for the bot";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("https://discord.com/oauth2/authorize?client_id=787522276500832297&scope=bot&permissions=8589934591");
    }
}
