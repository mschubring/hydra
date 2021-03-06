package inc.troll.hydra.modules.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@RequiredArgsConstructor
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> tracks = new LinkedBlockingQueue<>();

    public void nextTrack() {
        player.startTrack(tracks.poll(), false);
    }

    public void append(AudioTrack track) {
        if(!player.startTrack(track, true)) {
            tracks.offer(track);
        }
    }

    /**
     * stop player and empty queue
     */
    public void stopPlaying() {
        player.stopTrack();
        tracks.clear();
    }

    /**
     * {@code true} if player currently playing
     * @return
     */
    public boolean isPlaying() {
        return player.getPlayingTrack() != null;
    }

    /**
     * {@code true} if player is currently NOT playing
     * @return
     */
    public boolean isNotPlaying() {
        return !isPlaying();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
    }

    @Override
    public void onTrackEnd(
        AudioPlayer player,
        AudioTrack track,
        AudioTrackEndReason endReason
    ) {
        if(endReason.mayStartNext) {
            nextTrack();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(
        AudioPlayer player,
        AudioTrack track,
        FriendlyException exception
    ) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(
        AudioPlayer player,
        AudioTrack track,
        long thresholdMs
    ) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }
}
