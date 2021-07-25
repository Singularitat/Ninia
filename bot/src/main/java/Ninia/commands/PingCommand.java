package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.awt.Color;


public class PingCommand extends Command {

    public PingCommand() {
        this.name = "ping";
        this.help = "Gets the bots ping";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        long currentTime = System.currentTimeMillis();

        JDA jda = event.getJDA();
        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(new Color(0x7289da));

        long ping = (currentTime - event.getMessage().getTimeCreated().toInstant().toEpochMilli());

        if (ping <= 50) {
            embed.addField("Command Latency", "Clock out of sync", false);
        } else {
            embed.addField("Command Latency", String.format("`%dms`", ping), false);
        }

        embed.addField("Discord API Latency", String.format("`%dms`", jda.getGatewayPing()), false);

        event.getChannel().sendMessage(embed.build()).queue();
    }

}
