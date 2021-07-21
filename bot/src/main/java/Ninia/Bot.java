package Ninia;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Properties;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import javax.security.auth.login.LoginException;

import java.time.OffsetDateTime;

import java.awt.Color;


public class Bot extends ListenerAdapter {
    public static void main(String[] args) {
        try {
            InputStream input = new FileInputStream("src/main/java/Ninia/bot.properties");
            Properties config = new Properties();
            config.load(input);

            JDA jda = JDABuilder.createDefault(config.getProperty("token"))
                .addEventListeners(new Bot())
                .build();
            jda.awaitReady();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        long currentTime = System.currentTimeMillis();

        JDA jda = event.getJDA();
        long responseNumber = event.getResponseNumber();

        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        String msg = message.getContentDisplay();
        boolean bot = author.isBot();

        if (event.isFromType(ChannelType.TEXT)) {
            Guild guild = event.getGuild();
            TextChannel textChannel = event.getTextChannel();
            Member member = event.getMember();

            String name;
            if (message.isWebhookMessage()) {
                name = author.getName();
            } else {
                name = member.getEffectiveName();
            }

            System.out.printf("%s/%s <%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);

        } else if (event.isFromType(ChannelType.PRIVATE)) {
            PrivateChannel privateChannel = event.getPrivateChannel();
            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
        }

        switch (msg) {
            case "!ping":
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(0x7289da));

                long ping = (currentTime - message.getTimeCreated().toInstant().toEpochMilli());

                if (ping <= 50) {
                    embed.addField("Command Latency", "Clock out of sync", false);
                } else {
                    embed.addField("Command Latency", String.format("`%dms`", ping), false);
                }

                embed.addField("Discord API Latency", String.format("`%dms`", jda.getGatewayPing()), false);

                channel.sendMessage(embed.build()).queue();
                break;
            case "!roll":
                Random rand = ThreadLocalRandom.current();
                int roll = rand.nextInt(6) + 1;
                channel.sendMessage("" + roll).queue();
                break;
            case "!source":
                channel.sendMessage("https://github.com/Singularitat/Ninia").queue();
                break;
            case "!uptime":
                long upTime = ManagementFactory.getRuntimeMXBean().getUptime();
                channel.sendMessage(String.format("%ds", upTime / 1000)).queue();
                break;
        }
    }
}
