package inc.troll.hydra.modules.discord;

import inc.troll.hydra.config.HydraConfig;
import inc.troll.hydra.modules.discord.commands.CommandContext;
import inc.troll.hydra.modules.discord.commands.ICommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class CommandManager {

    private final HydraConfig config;
    private final Map<String, ICommand> commands = new HashMap<>();

    /**
     * add command to manager.
     * command name need to be unique.
     * @param command
     */
    public void add(ICommand command) {
        String name = command.getName();
        if(commands.containsKey(name)) {
            String msg = "command " + name + " already present";
            throw new IllegalArgumentException(msg);
        }
        commands.putIfAbsent(name, command);
    }

    /**
     * handles {@link GuildMessageReceivedEvent} and checks for known command.
     * only {@link #add(ICommand) added} commands are taken into account
     * @param event
     */
    public void handle(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String[] split = StringUtils.trimToEmpty(message).split("\\s+");
        String command = split[0].replace(config.getPrefix(), "")
            .toLowerCase();

        Optional.ofNullable(commands.get(command))
            .ifPresent(cmd -> {
                List<String> args = Arrays.asList(split).subList(1, split.length);
                CommandContext ctx = new CommandContext(event, args);
                cmd.handle(ctx);
            });
    }
}
