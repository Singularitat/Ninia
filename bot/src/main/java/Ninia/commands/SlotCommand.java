package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;


public class SlotCommand extends Command {

    public SlotCommand() {
        this.name = "slot";
        this.help = "Rolls the slot machine";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] emojis = {
            ":apple:",
            ":tangerine:",
            ":pear:",
            ":lemon:",
            ":watermelon:",
            ":grapes:",
            ":strawberry:",
            ":cherries:",
            ":kiwi:",
            ":pineapple:",
            ":coconut:",
            ":peach:",
            ":mango:"
        };
        Random rand = ThreadLocalRandom.current();

        String a = emojis[rand.nextInt(13)];
        String b = emojis[rand.nextInt(13)];
        String c = emojis[rand.nextInt(13)];
        String d = emojis[rand.nextInt(13)];

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0x7289da));

        embed.setTitle(String.format("[ %s %s %s %s ]", a, b, c, d), null);
        event.reply(embed.build());
    }
}
