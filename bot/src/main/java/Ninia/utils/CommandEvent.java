package Ninia.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import Ninia.utils.impl.CommandClientImpl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.internal.utils.Checks;


public class CommandEvent {
    public static int MAX_MESSAGES = 2;

    private final MessageReceivedEvent event;
    private String args;
    private final CommandClient client;


    public CommandEvent(MessageReceivedEvent event, String args, CommandClient client) {
        this.event = event;
        this.args = args == null ? "" : args;
        this.client = client;
    }

    public String getArgs() {
        return args;
    }

    void setArgs(String args) {
        this.args = args;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public CommandClient getClient() {
        return client;
    }

    public void reply(String message) {
        event.getChannel().sendMessage(message).queue();
    }

    public void reply(MessageEmbed embed) {
        event.getChannel().sendMessage(embed).queue(m -> {});
    }

    public void reply(MessageEmbed embed, Consumer < Message > success) {
        event.getChannel().sendMessage(embed).queue(m -> {
            success.accept(m);
        });
    }

    public void reply(MessageEmbed embed, Consumer < Message > success, Consumer < Throwable > failure) {
        event.getChannel().sendMessage(embed).queue(m -> {
            success.accept(m);
        }, failure);
    }

    public void reply(Message message) {
        event.getChannel().sendMessage(message).queue(m -> {
        });
    }

    public void reply(Message message, Consumer < Message > success) {
        event.getChannel().sendMessage(message).queue(m -> {
            success.accept(m);
        });
    }

    public void reply(Message message, Consumer < Message > success, Consumer < Throwable > failure) {
        event.getChannel().sendMessage(message).queue(m -> {
            success.accept(m);
        }, failure);
    }

    public void reply(File file, String filename) {
        event.getChannel().sendFile(file, filename).queue();
    }

    public void reply(String message, File file, String filename) {
        event.getChannel().sendFile(file, filename).content(message).queue();
    }


    public void async (Runnable runnable) {
        Checks.notNull(runnable, "Runnable");
        client.getScheduleExecutor().submit(runnable);
    }


    public SelfUser getSelfUser() {
        return event.getJDA().getSelfUser();
    }

    public Member getSelfMember() {
        return event.getGuild() == null ? null : event.getGuild().getSelfMember();
    }

    /**
     * Tests whether or not the {@link net.dv8tion.jda.api.entities.User User} who triggered this
     * event is an owner of the bot.
     *
     * @return {@code true} if the User is the Owner, else {@code false}
     */
    public boolean isOwner() {
        if (event.getAuthor().getId().equals(this.getClient().getOwnerId()))
            return true;
        if (this.getClient().getCoOwnerIds() == null)
            return false;
        for (String id: this.getClient().getCoOwnerIds())
            if (id.equals(event.getAuthor().getId()))
                return true;
        return false;
    }


    public User getAuthor() {
        return event.getAuthor();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public ChannelType getChannelType() {
        return event.getChannelType();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public Member getMember() {
        return event.getMember();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public PrivateChannel getPrivateChannel() {
        return event.getPrivateChannel();
    }

    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }
}
