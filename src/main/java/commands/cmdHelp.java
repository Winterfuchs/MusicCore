package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class cmdHelp implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {

        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setDescription("**MusicCore Version 1.1.0**")
                        .addField("Explaining all the commands:", "", false)
                        .addField("-m p [YouTube Link] or [Genre/Playlist]", "Plays a song, genre or playlist", false)
                        .addField("-m stop", "Stops the current track/playlist", false)
                        .addField("-m queue", "Shows the current playlist with all songs", false)
                        .addField("-m now", "Shows the current song with title and length", false)
                        .addField("-m skip", "Skips to the next song in the queue", false)
                        .addField("-m shuffle", "Shuffle all the songs in the queue", false)
                        .addField("-m vol", "Sets the music volume", false)
                        .addField("-m pause", "Pauses the song", false)
                        .addField("-m resume", "Resume the song", false)
                        .addField("-m save [name] [link]", "Saves a song into a playlist", false)
                        .addField("-m load [name]", "Loads a playlist into the queue", false)
                        .addField("-m saved", "Shows the saved playlists", false)
                        .addField("-m delete [name]", "Deletes a playlist", false)
                        .addField("-help", "Shows this text with all commands", false)
                        .addField("-ping", "Pong!", false)
                        .setColor(new Color(0,150,150))
                        .build()
        ).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[PING AUSGEFÜHRT]");
    }

    @Override
    public String help() {
        return null;
    }
}