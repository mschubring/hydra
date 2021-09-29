package inc.troll.hydra.modules.discord.commands;

import inc.troll.hydra.modules.audio.HydraManager;
import inc.troll.hydra.modules.audio.TrackScheduler;
import net.dv8tion.jda.api.entities.GuildVoiceState;

public class SkipCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        if(isBotWithMemberinChannel(ctx)) {
            TrackScheduler scheduler = HydraManager.getInstance()
                .getMusicManager(ctx.getGuild())
                .getScheduler();

            if(scheduler.isPlaying()) {
                scheduler.nextTrack();
            }
        }
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getHelp() {
        return new StringBuilder()
            .append("skip current song\n")
            .append("usage: `.skip`")
            .toString();
    }

    /**
     * checks if requesting member and bot are in same voice channel
     * @param ctx
     * @return
     */
    private boolean isBotWithMemberinChannel(CommandContext ctx) {
        GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();
        GuildVoiceState selfVoiceState = ctx.getSelfMember().getVoiceState();

        if(!memberVoiceState.inVoiceChannel() ||
            !selfVoiceState.inVoiceChannel()
        ) {
            return false;
        }

        long memberChannelId = memberVoiceState.getChannel().getIdLong();
        long selfChannelId = selfVoiceState.getChannel().getIdLong();
        return memberChannelId == selfChannelId;
    }
}
