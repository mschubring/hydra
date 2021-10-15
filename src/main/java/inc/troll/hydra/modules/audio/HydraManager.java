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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
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
        if (INSTANCE == null) {
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

    public void loadAndPlayFromClassPath(Guild guild, String classPathLocation) {
        String fileExtension = StringUtils.substringAfterLast(classPathLocation, ".");
        try {
            File tempFile = File.createTempFile("tmp_hydra", fileExtension);
            tempFile.deleteOnExit();
            try (InputStream classpathInput = this.getClass().getClassLoader().getResourceAsStream(classPathLocation); OutputStream tempFileOutput = new FileOutputStream(tempFile)) {
                if (classpathInput != null) {
                    // If a resource is in a jar, the url provided is almost exclusively understood by the jvm.
                    // The lavaplayer does not support an AudioTrack solely backed by an inputstream.
                    // Therefore, we copy the resource into a temporary file.
                    IOUtils.copy(classpathInput, tempFileOutput);
                    loadAndPlayFilePath(guild, tempFile.getPath());
                } else {
                    log.error("Did not find {} at the classpath", classPathLocation);
                }
            } catch (IOException e) {
                log.error("Failed to write the temp file {} for resource {}", tempFile, classPathLocation, e);
            }
        } catch (IOException e) {
            log.error("Failed to create a temp file for resource {}", classPathLocation, e);
        }
    }

    public void loadAndPlayFilePath(Guild guild, String filePath) {
        HydraMusicManager hydra = getMusicManager(guild);
        playerManager.loadItemOrdered(hydra, filePath, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                hydra.getScheduler().append(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // TODO Auto-generated method stub
            }

            @Override
            public void noMatches() {
                // TODO Auto-generated method stub
                log.error("no match for {}", filePath);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                // TODO Auto-generated method stub
                log.error("failed to load {}", filePath);
            }
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
                if (!playList.isSearchResult()) {
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
