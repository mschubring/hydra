package inc.troll.hydra.modules.discord.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import inc.troll.hydra.modules.audio.HydraManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class ListCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        MessageAction messageAction = ctx.getChannel()
                .sendMessage("scheduled tracks:");
        HydraManager.getInstance().getMusicManager(ctx.getGuild())
                .getScheduler()
                .getTracks()
                .stream()
                .map(this::authorAndTitleOrIdentifier)
                .map("\n"::concat)
                .forEach(messageAction::append);
        messageAction.queue();
    }

    private String authorAndTitleOrIdentifier(AudioTrack track) {
        return Optional.of(track).map(AudioTrack::getInfo)
                .filter(info -> StringUtils.isNotBlank(info.author))
                .filter(info -> StringUtils.isNotBlank(info.title))
                .map(info -> info.author + " - " + info.title)
                .orElse(track.getIdentifier());
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getHelp() {
        return "print current play list";
    }
}
