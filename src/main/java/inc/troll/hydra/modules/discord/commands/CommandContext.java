package inc.troll.hydra.modules.discord.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.GuildVoiceState;
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
     * @return <code>true</code> if the bot and the user,
     * who sent the command are in the same voice channel.
     */
    public boolean areBotAndUserInSameVoiceChannel() {
        Optional<Long> memberChannelId = Optional.of(this)
            .flatMap(getVoiceChannelFor(CommandContext::getMember))
            .map(VoiceChannel::getIdLong);

        Optional<Long> selfChannelId = Optional.of(this)
            .flatMap(getVoiceChannelFor(CommandContext::getSelfMember))
            .map(VoiceChannel::getIdLong);

        return memberChannelId.equals(selfChannelId)
             && memberChannelId.isPresent();
    }

    /**
     * Get the voice channel of the guild member defined by the <code>memberSelector</code>.
     *
     * @param memberSelector selects a member from the commandContext.
     * @return a function to extract the Optional&lg;VoiceChannel&gt;
     * of the selected member. It's empty, if the member is not in a voice channel.
     */
    public Function<CommandContext, Optional<VoiceChannel>> getVoiceChannelFor(
        @Nonnull Function<CommandContext, Member> memberSelector
    ) {
        return ctx -> Optional.of(ctx)
            .map(memberSelector) // select bot or user.
            .map(Member::getVoiceState)
            .map(GuildVoiceState::getChannel);
    }
}
