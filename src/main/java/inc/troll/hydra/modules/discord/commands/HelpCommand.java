package inc.troll.hydra.modules.discord.commands;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class HelpCommand implements ICommand {

    private static String COMMAND_PREFIX="* ";
    private static String COMMAND_SUFFIX=":";
    private static String COMMAND_DESCRIPTION_PREFIX="* ";
    @Nonnull
    private final Supplier<Stream<Map.Entry<String, ICommand>>> registeredCommandsProvider;

    @Override
    public void handle(CommandContext ctx) {
        MessageAction messageAction = ctx.getChannel().sendMessage("use:");
        // MessageAction is not immutable, no need to chain append calls.
        registeredCommandsProvider.get().flatMap(this::getHelpFor).map("\n"::concat).forEach(messageAction::append);
        messageAction.queue();
    }

    private Stream<String> getHelpFor(Map.Entry<String, ICommand> command) {
        return Stream.concat(
                Stream.of(COMMAND_PREFIX + command.getKey() + COMMAND_SUFFIX),
                command.getValue().getHelp().stream().map(COMMAND_DESCRIPTION_PREFIX::concat)
        );
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getHelp() {
        return List.of("displays this text");
    }


}
