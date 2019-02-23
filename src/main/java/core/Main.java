package core;

import commands.Music;
import commands.cmdHelp;
import commands.cmdPing;
import listener.commandListener;
import listener.readyListener;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import properties.AppProperties;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDABuilder builder;
    private AppProperties properties;

    public static void main(String[] args) {

        AppProperties properties = new AppProperties();

        properties.init();

        builder = new JDABuilder(AccountType.BOT);

        builder.setToken(properties.getProperties("token"));
        builder.setAutoReconnect(true);

        addListener();
        addCommands();

        try {
            JDA jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

    }


    public static void addCommands() {
        commandHandler.commands.put("music", new Music());
        commandHandler.commands.put("m", new Music());
        commandHandler.commands.put("ping", new cmdPing());
        commandHandler.commands.put("help", new cmdHelp());
    }


    public static void addListener() {
        builder.addEventListeners(new commandListener());
        builder.addEventListeners(new readyListener());
    }
}