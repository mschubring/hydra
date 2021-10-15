package inc.troll.hydra.modules.discord.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class CommandContext implements ICommandContext {

    private final GuildMessageReceivedEvent event;
    private final List<String> args;

    /**
     * checks if requesting member and bot are in same voice channel
     *
     * @return <code>true</code> if the bot and the user, who sent the command are in the same voice channel.
     */
    public boolean areBotAndUserInSameVoiceChannel() {
        Optional<VoiceChannel> userVoiceChannel = Optional.of(this).flatMap(getVoiceChannelFor(CommandContext::getMember));
        Optional<VoiceChannel> botVoiceChannel = Optional.of(this).flatMap(getVoiceChannelFor(CommandContext::getSelfMember));

        boolean userNotInVoice = userVoiceChannel.isEmpty();
        boolean botNotInVoice = botVoiceChannel.isEmpty();
        if (userNotInVoice || botNotInVoice) {
            return false;
        }

        long memberChannelId = userVoiceChannel.map(ISnowflake::getIdLong).get();
        long selfChannelId = botVoiceChannel.map(ISnowflake::getIdLong).get();
        return memberChannelId == selfChannelId;
    }

    /**
     * Get the voice channel of the guild member defined by the <code>memberSelector</code>.
     *
     * @param memberSelector selects a member from the commandContext.
     * @return a function to extract the Optional&lg;VoiceChannel&gt; of the selected member. It's empty, if the member is not in a voice channel.
     */
    private Function<CommandContext, Optional<VoiceChannel>> getVoiceChannelFor(@Nonnull Function<CommandContext, Member> memberSelector) {
        return ctx -> Optional.of(ctx)
                .map(memberSelector) // select bot or user.
                .map(Member::getVoiceState)
                .filter(GuildVoiceState::inVoiceChannel) // is not really necessary, because GuildVoiceState::getChannel returns null, if not in a channel
                .map(GuildVoiceState::getChannel);
    }
}
