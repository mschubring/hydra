package inc.troll.hydra.modules.discord.commands;

import java.util.List;

public class HelpCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        ctx.getChannel()
            .sendMessage("use:\n")
            .append("* `.play <YouTube link>` - plays song of given YouTube link\n")
            .append("* `.ping` - current latency in ms for REST and WebSocket\n")
            .append("* `.help` - displays this text")
            .queue();
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
