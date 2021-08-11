package Ninia;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import Ninia.commands.*;
import Ninia.utils.CommandClientBuilder;
import Ninia.utils.EventWaiter;

import java.util.Properties;

import javax.security.auth.login.LoginException;


public class Bot extends ListenerAdapter {
    public static void main(String[] args) throws IOException, LoginException, InterruptedException {
        InputStream input = new FileInputStream("src/main/java/Ninia/bot.properties");
        Properties config = new Properties();
        config.load(input);

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder client = new CommandClientBuilder();

        client.useDefaultGame();
        client.setOwnerId(config.getProperty("owner_id"));
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        client.setPrefix("!");
        client.addCommands(
            new CatCommand(),
            new ChooseCommand(),
            new PingCommand(),
            new InviteCommand(),
            new RollCommand(),
            new SlotCommand(),
            new SourceCommand(),
            new UptimeCommand(),
            new ServerCommand(),
            new DogCommand(),
            new DuckCommand(),
            new LizardCommand(),
            new BunnyCommand(),
            new WhaleCommand(),
            new AxolotlCommand(),
            new MonkeyCommand(),
            new RacoonCommand(),
            new BirdCommand(),
            new KoalaCommand()
        );

        JDA jda = JDABuilder.createDefault(config.getProperty("token"))
            .setActivity(Activity.playing("Playing with snakebot"))
            .addEventListeners(waiter, client.build())
            .build();
        jda.awaitReady();
    }
}
