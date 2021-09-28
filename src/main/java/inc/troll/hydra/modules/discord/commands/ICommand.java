package inc.troll.hydra.modules.discord.commands;

public interface ICommand {

    void handle(CommandContext ctx);
    String getName();
    String getHelp();
    // default List<String> getAliases() {
    //     return List.of();
    // };
}
