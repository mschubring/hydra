package inc.troll.hydra.modules.discord.commands;

import inc.troll.hydra.modules.audio.HydraManager;
import inc.troll.hydra.modules.audio.TrackScheduler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import javax.annotation.Nullable;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

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
        if(StringUtils.isNotBlank(args)) {
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
    public String getHelp() {
        return new StringBuilder()
            .append("plays a song\n")
            .append("usage:")
            .append("`.play` - plays top of queue")
            .append("`.play <YouTube link>` - plays Youtube link")
            .append("`.play <YouTube search>` - plays first YouTube search result")
            .toString();
    }

    /**
     * join members voice channel
     * @param ctx
     */
    private void joinVoiceChannel(CommandContext ctx) {
        VoiceChannel channel = getMembersVoiceCannel(ctx);
        if(channel == null) {
            return;
        }
        ctx.getGuild()
            .getAudioManager()
            .openAudioConnection(channel);
    }

    /**
     * returns the voice channel of the member
     * @param ctx
     * @return
     */
    @Nullable
    private VoiceChannel getMembersVoiceCannel(CommandContext ctx) {
        GuildVoiceState voiceState = ctx.getMember().getVoiceState();
        if(voiceState.inVoiceChannel()) {
            return voiceState.getChannel();
        }
        return null;
    }

    /**
     * validate if url or YouTube search.
     * check if url is whitelisted.
     * @param args
     * @return
     */
    private String getUrl(String args) {
        if(!urlUtils.isValid(args)) {
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
     * @param ctx
     * @param url
     */
    private void playRequestedUrl(CommandContext ctx, String url) {
        HydraManager.getInstance()
            .loadAndPlayUrl(ctx.getChannel(), url);
    }

    // FIXME - does not worh, because queue will be emptied by scheduler event. fix scheduler events
    /**
     * play top of the queue
     * @param ctx
     */
    private void playTopOfQueue(CommandContext ctx) {
        TrackScheduler scheduler = HydraManager.getInstance()
            .getMusicManager(ctx.getGuild())
            .getScheduler();

        if(scheduler.isNotPlaying()) {
            scheduler.nextTrack();
        }
    }
}
