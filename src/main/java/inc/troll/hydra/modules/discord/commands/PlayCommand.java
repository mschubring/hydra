package inc.troll.hydra.modules.discord.commands;

import inc.troll.hydra.modules.audio.HydraManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PlayCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {

        TextChannel textChannel = ctx.getChannel();
        Member member = ctx.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();
        if(!memberVoiceState.inVoiceChannel()) {
            textChannel.sendMessage("you need to be in a voice channel")
                .queue();
            return;
        }

        VoiceChannel voiceChannel = memberVoiceState.getChannel();
        ctx.getGuild().getAudioManager().openAudioConnection(voiceChannel);

        HydraManager.getInstance()
            .loadAndPlayUrl(textChannel, "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return new StringBuilder()
            .append("plays a song\n")
            .append("usage: `.play <YouTube link>`")
            .toString();
    }
}
