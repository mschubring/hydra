package inc.troll.hydra.modules.discord.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CommandContext implements ICommandContext {

    private final GuildMessageReceivedEvent event;
    private final List<String> args;
}
