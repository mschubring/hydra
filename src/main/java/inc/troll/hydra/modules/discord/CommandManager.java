package inc.troll.hydra.modules.discord;

import inc.troll.hydra.config.HydraConfig;
import inc.troll.hydra.modules.discord.commands.CommandContext;
import inc.troll.hydra.modules.discord.commands.ICommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class CommandManager {

    private final HydraConfig config;
    private final Map<String, ICommand> commands = new TreeMap<>();

    /**
     * add command to manager.
     * command name need to be unique.
     * @param command which should be registered.
     */
    public void add(ICommand command) {
        String name = config.getPrefix()+command.getName();
        if(commands.containsKey(name)) {
            String msg = "command " + name + " already present";
            throw new IllegalArgumentException(msg);
        }
        commands.putIfAbsent(name, command); // absence check was done above?
    }

    /**
     * handles {@link GuildMessageReceivedEvent} and checks for known command.
     * only {@link #add(ICommand) added} commands are taken into account
     * @param event the event containing a message in the bot's channel.
     */
    public void handle(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String[] split = StringUtils.trimToEmpty(message).split("\\s+");
        String command = split[0].toLowerCase();

        Optional.ofNullable(commands.get(command))
            .ifPresent(cmd -> {
                List<String> args = Arrays.asList(split).subList(1, split.length);
                CommandContext ctx = new CommandContext(event, args);
                cmd.handle(ctx);
            });
    }

    public Stream<Map.Entry<String,ICommand>> getRegisteredCommands(){
        return this.commands.entrySet().stream();
    }
}
