package inc.troll.hydra.modules.discord.commands;

import inc.troll.hydra.modules.audio.HydraManager;
import inc.troll.hydra.modules.audio.TrackScheduler;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PlayCommand implements ICommand {

    private final List<String> whitelist = Arrays.asList(
            "www.youtube.com",
            "youtube.com",
            "youtu.be"
    );

    private final UrlValidator urlUtils = UrlValidator.getInstance();

    @Override
    public void handle(CommandContext ctx) {
        joinVoiceChannel(ctx);

        String args = String.join(" ", ctx.getArgs());
        if (StringUtils.isNotBlank(args)) {
            playRequestedUrl(ctx, getUrl(args));
        } else {
            playTopOfQueue(ctx);
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public List<String> getHelp() {
        return List.of(
            "plays a song",
            "usage:",
            "`.play` - plays top of queue",
            "`.play <YouTube link>` - plays Youtube link",
            "`.play <YouTube search>` - plays first YouTube search result"
        );
    }

    /**
     * join members voice channel
     *
     * @param ctx the command's execution context
     */
    private void joinVoiceChannel(CommandContext ctx) {
        Consumer<VoiceChannel> joinChannel = ctx.getGuild()
            .getAudioManager()::openAudioConnection;

        Optional.of(ctx)
            .flatMap(ctx.getVoiceChannelFor(CommandContext::getMember))
            .ifPresent(joinChannel);
    }


    /**
     * validate if url or YouTube search.
     * check if url is whitelisted.
     *
     * @param args url or search terms
     * @return an url to a remote media file or to a search
     */
    private String getUrl(String args) {
        if (!urlUtils.isValid(args)) {
            return "ytsearch: " + args;
        }
        String domain = URI.create(args).getHost().toLowerCase();
        return whitelist.stream()
            .filter(url -> StringUtils.equals(domain, url))
            .findFirst()
            .map(url -> args)
            .orElse(domain);
    }

    /**
     * add url to queue
     *
     * @param ctx the command's execution context
     * @param url url to remote media file
     */
    private void playRequestedUrl(CommandContext ctx, String url) {
        HydraManager.getInstance()
            .loadAndPlayUrl(ctx.getChannel(), url);
    }

    // FIXME - does not work, because queue will be emptied by scheduler event. fix scheduler events

    /**
     * play top of the queue
     *
     * @param ctx the command's execution context
     */
    private void playTopOfQueue(CommandContext ctx) {
        TrackScheduler scheduler = HydraManager.getInstance()
            .getMusicManager(ctx.getGuild())
            .getScheduler();

        if (scheduler.isNotPlaying()) {
            scheduler.nextTrack();
        }
    }
}
