package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class RollCommand extends Command {

    public RollCommand() {
        this.name = "roll";
        this.help = "Rolls a dice";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        Random rand = ThreadLocalRandom.current();
        int roll = rand.nextInt(6) + 1;
        event.reply(Integer.toString(roll));
    }
}
