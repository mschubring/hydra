package inc.troll.hydra.modules.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class HydraManager {

    private static HydraManager INSTANCE;

    private final Map<Long, HydraMusicManager> musicManagers;
    private final AudioPlayerManager playerManager;

    private HydraManager() {
        musicManagers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static HydraManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new HydraManager();
        }
        return INSTANCE;
    }

    public HydraMusicManager getMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            HydraMusicManager hydra = new HydraMusicManager(playerManager);
            guild.getAudioManager().setSendingHandler(hydra.getSendHandler());
            return hydra;
        });
    }

    public void loadAndPlayUrl(TextChannel channel, String trackUrl) {
        HydraMusicManager hydra = getMusicManager(channel.getGuild());
        playerManager.loadItemOrdered(hydra, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void loadFailed(FriendlyException cause) {
                channel.sendMessage("Sorry can not load ")
                    .append(trackUrl)
                    .append(" because ")
                    .append(cause.getMessage())
                    .queue();
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Sorry can not load ")
                    .append(trackUrl)
                    .append(" ... url broken?")
                    .queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playList) {
                trackLoaded(playList.getSelectedTrack());
                channel.sendMessage("Sorry play lists are not supported ...\n")
                    .append("... but I will play the first track for you ;-)")
                    .queue();
            }

            @Override
            public void trackLoaded(AudioTrack track) {
                hydra.getScheduler().append(track);

                channel.sendMessage("Append to queue: ")
                    .append(track.getInfo().title)
                    .append(" by ")
                    .append(track.getInfo().author)
                    .queue();
            }
        });
    }
}
