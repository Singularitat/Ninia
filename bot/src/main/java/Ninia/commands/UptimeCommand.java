package Ninia.commands;

import Ninia.utils.Command;
import Ninia.utils.CommandEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;


public class UptimeCommand extends Command {

    public UptimeCommand() {
        this.name = "uptime";
        this.help = "Gets the uptime of the bot in seconds";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime();
        event.reply(String.format("%ds", upTime / 1000));
    }
}
