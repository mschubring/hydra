package inc.troll.hydra.modules.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;

public class HydraMusicManager {

    private final AudioPlayer player;

    @Getter
    private final TrackScheduler scheduler;

    @Getter
    private final AudioPlayerSendHandler sendHandler;

    public HydraMusicManager(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(player);
        this.sendHandler = new AudioPlayerSendHandler(player);
        player.addListener(scheduler);
    }
}
