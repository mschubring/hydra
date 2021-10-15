package inc.troll.hydra.modules.discord.commands;

import java.util.List;

public interface ICommand {

    void handle(CommandContext ctx);
    String getName();
    List<String> getHelp();
    // default List<String> getAliases() {
    //     return List.of();
    // };
}
