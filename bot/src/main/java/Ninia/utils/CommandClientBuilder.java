package Ninia.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;

import Ninia.utils.impl.CommandClientImpl;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;


public class CommandClientBuilder {
    private Activity activity = Activity.playing("default");
    private OnlineStatus status = OnlineStatus.ONLINE;
    private String ownerId;
    private String[] coOwnerIds;
    private String prefix;
    private String altprefix;
    private String success;
    private String warning;
    private String error;
    private final LinkedList < Command > commands = new LinkedList < > ();
    private CommandListener listener;
    private boolean useHelp = true;
    private boolean shutdownAutomatically = true;
    private String helpWord;
    private ScheduledExecutorService executor;
    private int linkedCacheSize = 0;


    public CommandClient build() {
        CommandClient client = new CommandClientImpl(ownerId, coOwnerIds, prefix, altprefix, activity, status,
            success, warning, error, new ArrayList < > (commands), useHelp,
            shutdownAutomatically, helpWord, executor, linkedCacheSize);
        if (listener != null)
            client.setListener(listener);
        return client;
    }

    public CommandClientBuilder setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public CommandClientBuilder setCoOwnerIds(String...coOwnerIds) {
        this.coOwnerIds = coOwnerIds;
        return this;
    }

    public CommandClientBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public CommandClientBuilder setAlternativePrefix(String prefix) {
        this.altprefix = prefix;
        return this;
    }

    public CommandClientBuilder useHelpBuilder(boolean useHelp) {
        this.useHelp = useHelp;
        return this;
    }

    public CommandClientBuilder setHelpWord(String helpWord) {
        this.helpWord = helpWord;
        return this;
    }

    public CommandClientBuilder setEmojis(String success, String warning, String error) {
        this.success = success;
        this.warning = warning;
        this.error = error;
        return this;
    }

    public CommandClientBuilder setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public CommandClientBuilder useDefaultGame() {
        this.activity = Activity.playing("default");
        return this;
    }

    public CommandClientBuilder setStatus(OnlineStatus status) {
        this.status = status;
        return this;
    }

    public CommandClientBuilder addCommand(Command command) {
        commands.add(command);
        return this;
    }

    public CommandClientBuilder addCommands(Command...commands) {
        for (Command command: commands)
            this.addCommand(command);
        return this;
    }

    public CommandClientBuilder setListener(CommandListener listener) {
        this.listener = listener;
        return this;
    }

    public CommandClientBuilder setScheduleExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public CommandClientBuilder setShutdownAutomatically(boolean shutdownAutomatically) {
        this.shutdownAutomatically = shutdownAutomatically;
        return this;
    }

    public CommandClientBuilder setLinkedCacheSize(int linkedCacheSize) {
        this.linkedCacheSize = linkedCacheSize;
        return this;
    }
}
