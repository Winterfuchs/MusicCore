package audioCore;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer PLAYER;
    private final Queue<AudioInfo> queue;

    public TrackManager(AudioPlayer player) {
        this.PLAYER = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, Member author) {

        AudioInfo info = new AudioInfo(track, author);
        queue.add(info);

        if (PLAYER.getPlayingTrack() == null) {
            PLAYER.playTrack(track);
        }
    }

    public Set<AudioInfo> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    public AudioInfo getInfo(AudioTrack track) {
        return queue.stream().filter(info -> info.getTrack().equals(track))
                .findFirst().orElse(null);
    }

    public void purgeQueue() {
        queue.clear();
    }

    public void shuffleQueue() {
        List<AudioInfo> cQueue = new ArrayList<>(getQueue());
        AudioInfo current = cQueue.get(0);
        cQueue.remove(0);
        Collections.shuffle(cQueue);
        cQueue.add(0, current);
        purgeQueue();
        queue.addAll(cQueue);
    }

    public void setVolume(int vol) {
        PLAYER.setVolume(vol);
    }

    @Override
    public void onTrackStart(com.sedmelluq.discord.lavaplayer.player.AudioPlayer player, com.sedmelluq.discord.lavaplayer.track.AudioTrack track) {
        AudioInfo info = queue.element();
        VoiceChannel vChan = info.getAuthor().getVoiceState().getChannel();

        if (vChan == null) {
            player.stopTrack();
            //ErrorMessage

        } else
            info.getAuthor().getGuild().getAudioManager().openAudioConnection(vChan);
    }


    @Override
    public void onTrackEnd(com.sedmelluq.discord.lavaplayer.player.AudioPlayer player, com.sedmelluq.discord.lavaplayer.track.AudioTrack track, com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason endReason) {
        Guild g = queue.poll().getAuthor().getGuild();

        if (queue.isEmpty())
            new Thread(() -> g.getAudioManager().closeAudioConnection()).start();
        else
            player.playTrack(queue.element().getTrack());
    }
}
