package inc.troll.hydra.modules.discord.commands;

import inc.troll.hydra.modules.audio.HydraManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import javax.annotation.Nullable;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class PlayCommand implements ICommand {

    private final List<String> witelist = Arrays.asList(
        "www.youtube.com",
        "youtube.com",
        "youtu.be"
    );

    private final UrlValidator urlUtils = UrlValidator.getInstance();

    @Override
    public void handle(CommandContext ctx) {

        String args = String.join(" ", ctx.getArgs());
        if(StringUtils.isNotBlank(args)) {
            joinVoiceChannelAndPlay(ctx, getUrl(args));
        } else {
            // TODO - play top of queue or last track
            throw new NotImplementedException("TODO - play top of queue or last track");
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
            .append("* `.play <YouTube link>`")
            .append("* `.play <YouTube search>`")
            .toString();
    }

    private String getUrl(String args) {

        if(!urlUtils.isValid(args)) {
            return "ytsearch: " + args;
        }

        String domain = URI.create(args).getHost().toLowerCase();
        return witelist.stream()
            .filter(url -> StringUtils.equals(domain, url))
            .findFirst()
            .map(url -> args)
            .orElse(domain);
    }

    /**
     * join members voice channel and play url
     * @param ctx
     * @param url
     */
    private void joinVoiceChannelAndPlay(CommandContext ctx, String url) {

        VoiceChannel channel = getMembersVoiceCannel(ctx);
        if(channel == null) {
            return;
        }

        ctx.getGuild()
            .getAudioManager()
            .openAudioConnection(channel);

        HydraManager.getInstance()
            .loadAndPlayUrl(ctx.getChannel(), url);
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
}
