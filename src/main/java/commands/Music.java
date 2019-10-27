package commands;

import audioCore.AudioInfo;
import audioCore.PlayerSendHandler;
import audioCore.TrackManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.sound.midi.Track;
import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Music implements Command {

    private static final int PLAYLIST_LIMIT = 1000;
    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS = new HashMap<>();

    public Music() {
        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    private AudioPlayer createPlayer(Guild g) {
        AudioPlayer p = MANAGER.createPlayer();
        TrackManager m = new TrackManager(p);
        p.addListener(m);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(p));

        PLAYERS.put(g, new AbstractMap.SimpleEntry<>(p, m));

        return p;

    }

    private boolean hasPlayer(Guild g) {
        return PLAYERS.containsKey(g);
    }

    private AudioPlayer getPlayer(Guild g) {
        if (hasPlayer(g))
            return PLAYERS.get(g).getKey();
        else
            return createPlayer(g);
    }

    private TrackManager getManager(Guild g) {
        return PLAYERS.get(g).getValue();
    }

    private boolean isIdle(Guild g) {
        return !hasPlayer(g) || getPlayer(g).getPlayingTrack() == null;
    }

    private void loadTrack(String identifier, Member author, Message msg) {
        Guild guild = author.getGuild();
        getPlayer(guild);
        MANAGER.setFrameBufferDuration(5000);
        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                getManager(guild).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (int i = 0; i < (playlist.getTracks().size() > PLAYLIST_LIMIT ? PLAYLIST_LIMIT : playlist.getTracks().size()); i++) {
                    getManager(guild).queue(playlist.getTracks().get(i), author);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    private void skip(Guild g) {
        getPlayer(g).stopTrack();
    }

    private String getTimestamp(long milis) {
        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);

        if (hours > 168) {
            return "-";
        }

        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String buildQueueMessage(AudioInfo info) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;

        return "`[ " + getTimestamp(length) + " ]` " + title + "\n";
    }

    private void sendErrorMsg(MessageReceivedEvent event, String context) {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.red)
                        .setDescription(context)
                        .build()
        ).queue();
    }


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        guild = event.getGuild();

        if (args.length < 1) {
            sendErrorMsg(event, help());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "play":
            case "p":

                if (args.length < 2) {
                    sendErrorMsg(event, "Please enter a valid source!");
                    return;
                }

                String input = Arrays.stream(args).skip(1).map(s -> " " + s).collect(Collectors.joining()).substring(1);

                if (!(input.startsWith("http://") || input.startsWith("https://")))
                    input = "ytsearch: " + input;

                loadTrack(input, event.getMember(), event.getMessage());
                break;

            case "skip":
            case "s":

                if (isIdle(guild)) return;
                for (int i = (args.length > 1 ? Integer.parseInt(args[1]) : 1); i == 1; i--) {
                    skip(guild);
                }
                break;

            case "stop":

                if (isIdle(guild)) return;

                getManager(guild).purgeQueue();
                skip(guild);
                guild.getAudioManager().closeAudioConnection();

                break;

            case "shuffle":

                if (isIdle(guild)) return;
                getManager(guild).shuffleQueue();

                break;

            case "volume":
            case "vol":

                if (isIdle(guild)) return;
                if (args.length < 2) {
                    sendErrorMsg(event, "Please enter a valid number!");
                    return;
                }
                try {
                    int vol = Integer.parseInt(Arrays.stream(args).skip(1).map(s -> " " + s).collect(Collectors.joining()).substring(1));
                    if (vol <= 100) {
                        getManager(guild).setVolume(vol);
                    }
                    else {
                        sendErrorMsg(event, "The allowed max. volume is 100!");
                    }
                }
                catch(Exception e) {
                    sendErrorMsg(event, "Please enter an integer value!");
                }

                break;

            case "pause":

                if (isIdle(guild)) return;
                getPlayer(guild).setPaused(true);
                break;

            case "resume":

                if (isIdle(guild)) return;
                getPlayer(guild).setPaused(false);
                break;

            case "time":

                if (isIdle(guild)) return;

                try {
                    String rawTime = (Arrays.stream(args).skip(1).map(s -> " " + s).collect(Collectors.joining()).substring(1));
                    String[] time = rawTime.split(":");
                    int temp;
                    int hour;
                    int minutes;
                    int seconds;

                    if (args.length < 2) {
                        sendErrorMsg(event, "Please enter a valid format!" +
                                "\n\nFormats:\nhh:mm:ss or mm:ss\n\nYou can also use " +
                                "just seconds to skip forward.");
                        return;
                    }

                    switch(time.length) {
                        case 1:
                            seconds = Integer.parseInt(time[0]);
                            temp =  seconds * 1000;
                            getPlayer(guild).getPlayingTrack().setPosition(getPlayer(guild).getPlayingTrack().getPosition() + temp);
                                break;

                        case 2:
                            minutes = Integer.parseInt(time[0]);
                            seconds = Integer.parseInt(time[1]);
                            temp = (seconds + (60 * minutes)) * 1000;
                            getPlayer(guild).getPlayingTrack().setPosition(temp);
                            break;

                        case 3:
                            hour = Integer.parseInt(time[0]);
                            minutes = Integer.parseInt(time[1]);
                            seconds = Integer.parseInt(time[2]);

                            temp = (seconds + (60 * minutes) + (3600 * hour)) * 1000;
                            getPlayer(guild).getPlayingTrack().setPosition(temp);
                            break;

                        default:
                            sendErrorMsg(event, "Please enter a valid format!" +
                                    "\n\nFormats:\nhh:mm:ss or mm:ss\n\nYou can also use " +
                                    "just seconds to skip forward.");
                            break;
                    }

                }
                catch(Exception e) {
                    sendErrorMsg(event, "Please enter a valid format!" +
                            "\n\nFormats:\nhh:mm:ss or mm:ss\n\nYou can also use " +
                            "just seconds to skip forward.");
                }
                break;

            case "save":

                    String rawSave = (Arrays.stream(args).skip(0).map(s -> " " + s).collect(Collectors.joining()).substring(1));
                    String[] save = rawSave.split(" ");


                    if(save.length == 1) {
                        sendErrorMsg(event, "You need a playlist name to save in!");
                        return;
                    }

                    else if(save.length == 2) {
                        sendErrorMsg(event, "You have to provide a link to the song!");
                        return;
                    }

                   else if (!(save[2].startsWith("http://") || save[2].startsWith("https://"))) {
                         sendErrorMsg(event, "Only links are allowed!");
                         return;
                     }

                    else if (save.length > 3) {
                        sendErrorMsg(event, "Only 2 arguments needed, " + save.length + " given!");
                        return;
                    }

                    File path = new File("SERVER_SETTINGS/" + event.getGuild().getId() + "/playlists");

                    if (!path.exists())
                        path.mkdirs();

                    File saveFile = new File("SERVER_SETTINGS/" + event.getGuild().getId() + "/playlists/" + args[1]);

                try {
                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(saveFile, true));
                    fileWriter.append(args[2] + "\n");
                    fileWriter.close();

                    event.getTextChannel().sendMessage("Playlist " + save[1] + " saved successfully").queue();
                }

                catch(Exception e) {
                    sendErrorMsg(event, "Sorry, an error has occurred! Please try to save again!");
                }

                break;

            case "saved":
                try {
                    File[] saves = new File("SERVER_SETTINGS/" + event.getGuild().getId() + "/playlists").listFiles();
                    StringBuilder list = new StringBuilder();

                    if (saves.length > 0) {
                        Arrays.stream(saves).forEach(file -> list.append("- **" + file.getName() + "**\n"));
                        event.getTextChannel().sendMessage(list.toString()).queue();
                    } else {
                        event.getTextChannel().sendMessage("No playlists saved!").queue();
                    }
                } catch (Exception e) {
                    sendErrorMsg(event, "Sorry, an error has occurred!");
                }

                break;

            case "load":

                if (args.length < 2) {
                 sendErrorMsg(event, "You need to define a playlist to load.");
                 return;
                }

                try {
                    File savedFile = new File("SERVER_SETTINGS/" + event.getGuild().getId() + "/playlists/" + args[1]);
                    BufferedReader reader = new BufferedReader(new FileReader(savedFile));

                    String out;
                    while((out = reader.readLine()) != null)
                        loadTrack(out, event.getMember(), event.getMessage());


                    if(getPlayer(guild).isPaused())
                        getPlayer(guild).setPaused(false);

                    new Timer().schedule(
                            new java.util.TimerTask() {

                                @Override
                                public void run() {
                                    int tracks =  getManager(guild).getQueue().size();
                                    event.getTextChannel().sendMessage("Queued " + tracks + " tracks").queue();
                                }
                            }, 5000
                    );

                } catch(Exception e) {
                    sendErrorMsg(event, "Sorry, an error has occurred!");
                }

                break;

            case "delete":
                try {
                    if (args.length < 2) {
                        sendErrorMsg(event, "You need to define a playlist to delete.");
                        return;
                    }

                    File deleteFile = new File("SERVER_SETTINGS/" + event.getGuild().getId() + "/playlists/" + args[1]);
                    deleteFile.delete();
                    event.getTextChannel().sendMessage("Successfully deleted " + args[1] + "!").queue();

                } catch (Exception e) {
                    sendErrorMsg(event, "Sorry, couldn't delete file!");
                }

                break;

            case "now":
            case "info":

                if (isIdle(guild)) return;

                AudioTrack track = getPlayer(guild).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();

                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription("**CURRENT TRACK INFO:**")
                                .addField("Title", info.title, false)
                                .addField("Duration", "`[ " + getTimestamp(track.getPosition()) + " / " + getTimestamp(track.getDuration()) + " ]`", false)
                                .addField("Author", info.author, false)
                                .build()
                ).queue();

                break;

            case "queue":

                if (isIdle(guild)) return;
                int sideNumb = args.length > 1 ? Integer.parseInt(args[1]) : 1;
                //Try catch
                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;

                getManager(guild).getQueue().forEach(audioInfo -> tracks.add(buildQueueMessage(audioInfo)));

                if (tracks.size() > 20)
                    trackSublist = tracks.subList((sideNumb - 1) * 20, (sideNumb - 1) * 20 + 20);
                else
                    trackSublist = tracks;

                String out = trackSublist.stream().collect(Collectors.joining("\n"));
                int sideNumbAll = tracks.size() >= 20 ? tracks.size() / 20 : 1;

                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription(
                                        "**CURRENT QUEUE:**\n" +
                                                "*[" + getManager(guild).getQueue().stream().count() + " Tracks | Side " + sideNumb + " / " + sideNumbAll + "]*\n\n" +
                                                out
                                )
                                .build()
                ).queue();


                break;
        }

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}