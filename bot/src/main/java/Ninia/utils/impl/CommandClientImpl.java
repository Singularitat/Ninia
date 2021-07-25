package Ninia.utils.impl;

import Ninia.utils.*;
import Ninia.utils.Command.Category;
import Ninia.utils.impl.FixedSizeCache;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class CommandClientImpl implements CommandClient, EventListener {
    private static final Logger LOG = LoggerFactory.getLogger(CommandClient.class);
    private static final String DEFAULT_PREFIX = "@mention";

    private final OffsetDateTime start;
    private final Activity activity;
    private final OnlineStatus status;
    private final String ownerId;
    private final String[] coOwnerIds;
    private final String prefix;
    private final String altprefix;
    private final HashMap < String, Integer > commandIndex;
    private final ArrayList < Command > commands;
    private final String success;
    private final String warning;
    private final String error;
    private final HashMap < String, OffsetDateTime > cooldowns;
    private final HashMap < String, Integer > uses;
    private final FixedSizeCache < Long, Set < Message >> linkMap;
    private final boolean useHelp;
    private final boolean shutdownAutomatically;
    private final String helpWord;
    private final ScheduledExecutorService executor;

    private String textPrefix;
    private CommandListener listener = null;
    private int totalGuilds;

    public CommandClientImpl(String ownerId, String[] coOwnerIds, String prefix, String altprefix, Activity activity, OnlineStatus status,
        String success, String warning, String error, ArrayList < Command > commands,
        boolean useHelp, boolean shutdownAutomatically, String helpWord, ScheduledExecutorService executor,
        int linkedCacheSize) {
        Checks.check(ownerId != null, "Owner ID was set null or not set! Please provide an User ID to register as the owner!");

        this.start = OffsetDateTime.now();

        this.ownerId = ownerId;
        this.coOwnerIds = coOwnerIds;
        this.prefix = prefix == null || prefix.isEmpty() ? DEFAULT_PREFIX : prefix;
        this.altprefix = altprefix == null || altprefix.isEmpty() ? null : altprefix;
        this.textPrefix = prefix;
        this.activity = activity;
        this.status = status;
        this.success = success == null ? "" : success;
        this.warning = warning == null ? "" : warning;
        this.error = error == null ? "" : error;
        this.commandIndex = new HashMap < > ();
        this.commands = new ArrayList < > ();
        this.cooldowns = new HashMap < > ();
        this.uses = new HashMap < > ();
        this.linkMap = linkedCacheSize > 0 ? new FixedSizeCache < > (linkedCacheSize) : null;
        this.useHelp = useHelp;
        this.shutdownAutomatically = shutdownAutomatically;
        this.helpWord = helpWord == null ? "help" : helpWord;
        this.executor = executor == null ? Executors.newSingleThreadScheduledExecutor() : executor;

        // Load commands
        for (Command command: commands) {
            addCommand(command);
        }
    }

    @Override
    public void setListener(CommandListener listener) {
        this.listener = listener;
    }

    @Override
    public CommandListener getListener() {
        return listener;
    }

    @Override
    public List < Command > getCommands() {
        return commands;
    }

    @Override
    public OffsetDateTime getStartTime() {
        return start;
    }

    @Override
    public OffsetDateTime getCooldown(String name) {
        return cooldowns.get(name);
    }

    @Override
    public int getRemainingCooldown(String name) {
        if (cooldowns.containsKey(name)) {
            int time = (int) Math.ceil(OffsetDateTime.now().until(cooldowns.get(name), ChronoUnit.MILLIS) / 1000D);
            if (time <= 0) {
                cooldowns.remove(name);
                return 0;
            }
            return time;
        }
        return 0;
    }

    @Override
    public void applyCooldown(String name, int seconds) {
        cooldowns.put(name, OffsetDateTime.now().plusSeconds(seconds));
    }

    @Override
    public void cleanCooldowns() {
        OffsetDateTime now = OffsetDateTime.now();
        cooldowns.keySet().stream().filter((str) -> (cooldowns.get(str).isBefore(now)))
            .collect(Collectors.toList()).forEach(cooldowns::remove);
    }

    @Override
    public int getCommandUses(Command command) {
        return getCommandUses(command.getName());
    }

    @Override
    public int getCommandUses(String name) {
        return uses.getOrDefault(name, 0);
    }

    @Override
    public void addCommand(Command command) {
        addCommand(command, commands.size());
    }

    @Override
    public void addCommand(Command command, int index) {
        if (index > commands.size() || index < 0)
            throw new ArrayIndexOutOfBoundsException("Index specified is invalid: [" + index + "/" + commands.size() + "]");
        synchronized(commandIndex) {
            String name = command.getName().toLowerCase();
            //check for collision
            if (commandIndex.containsKey(name))
                throw new IllegalArgumentException("Command added has a name or alias that has already been indexed: \"" + name + "\"!");
            for (String alias: command.getAliases()) {
                if (commandIndex.containsKey(alias.toLowerCase()))
                    throw new IllegalArgumentException("Command added has a name or alias that has already been indexed: \"" + alias + "\"!");
            }
            //shift if not append
            if (index < commands.size()) {
                commandIndex.entrySet().stream().filter(entry -> entry.getValue() >= index).collect(Collectors.toList())
                    .forEach(entry -> commandIndex.put(entry.getKey(), entry.getValue() + 1));
            }
            //add
            commandIndex.put(name, index);
            for (String alias: command.getAliases())
                commandIndex.put(alias.toLowerCase(), index);
        }
        commands.add(index, command);
    }

    @Override
    public void removeCommand(String name) {
        synchronized(commandIndex) {
            if (!commandIndex.containsKey(name.toLowerCase()))
                throw new IllegalArgumentException("Name provided is not indexed: \"" + name + "\"!");
            int targetIndex = commandIndex.remove(name.toLowerCase());
            Command removedCommand = commands.remove(targetIndex);
            for (String alias: removedCommand.getAliases()) {
                commandIndex.remove(alias.toLowerCase());
            }
            commandIndex.entrySet().stream().filter(entry -> entry.getValue() > targetIndex).collect(Collectors.toList())
                .forEach(entry -> commandIndex.put(entry.getKey(), entry.getValue() - 1));
        }
    }

    @Override
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public long getOwnerIdLong() {
        return Long.parseLong(ownerId);
    }

    @Override
    public String[] getCoOwnerIds() {
        return coOwnerIds;
    }

    @Override
    public long[] getCoOwnerIdsLong() {
        // Thought about using java.util.Arrays#setAll(T[], IntFunction<T>)
        // here, but as it turns out it's actually the same thing as this but
        // it throws an error if null. Go figure.
        if (coOwnerIds == null)
            return null;
        long[] ids = new long[coOwnerIds.length];
        for (int i = 0; i < ids.length; i++)
            ids[i] = Long.parseLong(coOwnerIds[i]);
        return ids;
    }

    @Override
    public String getSuccess() {
        return success;
    }

    @Override
    public String getWarning() {
        return warning;
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public ScheduledExecutorService getScheduleExecutor() {
        return executor;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getAltPrefix() {
        return altprefix;
    }

    @Override
    public String getTextualPrefix() {
        return textPrefix;
    }

    @Override
    public int getTotalGuilds() {
        return totalGuilds;
    }

    @Override
    public String getHelpWord() {
        return helpWord;
    }

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof MessageReceivedEvent)
            onMessageReceived((MessageReceivedEvent) event);
        else if (event instanceof ReadyEvent)
            onReady((ReadyEvent) event);
    }

    private void onReady(ReadyEvent event) {
        textPrefix = prefix.equals(DEFAULT_PREFIX) ? "@" + event.getJDA().getSelfUser().getName() + " " : prefix;
        event.getJDA().getPresence().setPresence(status == null ? OnlineStatus.ONLINE : status,
            activity == null ? null : "default".equals(activity.getName()) ? Activity.playing("Type " + textPrefix + helpWord) : activity);
    }

    private void onMessageReceived(MessageReceivedEvent event) {
        // Return if it's a bot
        if (event.getAuthor().isBot())
            return;

        String[] parts = null;
        String rawContent = event.getMessage().getContentRaw();

        // Check for prefix or alternate prefix (@mention cases)
        if (prefix.equals(DEFAULT_PREFIX) || (altprefix != null && altprefix.equals(DEFAULT_PREFIX))) {
            if (rawContent.startsWith("<@" + event.getJDA().getSelfUser().getId() + ">") ||
                rawContent.startsWith("<@!" + event.getJDA().getSelfUser().getId() + ">")) {
                parts = splitOnPrefixLength(rawContent, rawContent.indexOf(">") + 1);
            }
        }
        // Check for prefix
        if (parts == null && rawContent.toLowerCase().startsWith(prefix.toLowerCase()))
            parts = splitOnPrefixLength(rawContent, prefix.length());
        // Check for alternate prefix
        if (parts == null && altprefix != null && rawContent.toLowerCase().startsWith(altprefix.toLowerCase()))
            parts = splitOnPrefixLength(rawContent, altprefix.length());

        if (parts != null) //starts with valid prefix
        {
            if (event.isFromType(ChannelType.PRIVATE) || event.getTextChannel().canTalk()) {
                String name = parts[0];
                String args = parts[1] == null ? "" : parts[1];
                final Command command; // this will be null if it's not a command
                synchronized(commandIndex) {
                    int i = commandIndex.getOrDefault(name.toLowerCase(), -1);
                    command = i != -1 ? commands.get(i) : null;
                }

                if (command != null) {
                    CommandEvent cevent = new CommandEvent(event, args, this);

                    if (listener != null)
                        listener.onCommand(cevent, command);
                    uses.put(command.getName(), uses.getOrDefault(command.getName(), 0) + 1);
                    command.run(cevent);
                    return; // Command is done
                }
            }
        }

        if (listener != null)
            listener.onNonCommandMessage(event);
    }

    private static String[] splitOnPrefixLength(String rawContent, int length) {
        return Arrays.copyOf(rawContent.substring(length).trim().split("\\s+", 2), 2);
    }
}
