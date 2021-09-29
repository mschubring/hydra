package inc.troll.hydra.modules.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HydraManager {

    private static HydraManager INSTANCE;

    @Getter
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
                if(!playList.isSearchResult()) {
                    channel.sendMessage("No play lists, sorry :sweat_smile:\n")
                        .append("But I'll play the first one for you :wink:")
                        .queue();
                }
                playList.getTracks().stream()
                        .findFirst()
                        .ifPresent(this::trackLoaded);
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
