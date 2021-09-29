package inc.troll.hydra.modules.discord.commands;

import inc.troll.hydra.modules.audio.HydraManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;

public class StopCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        if(isBotWithMemberinChannel(ctx)) {
            HydraManager.getInstance()
                .getMusicManager(ctx.getGuild())
                .getScheduler()
                .stopPlaying();

            ctx.getGuild()
                .getAudioManager()
                .closeAudioConnection();
        }
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return new StringBuilder()
            .append("stop playing songs\n")
            .append("usage: `.stop`")
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
