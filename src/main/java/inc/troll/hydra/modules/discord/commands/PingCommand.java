package inc.troll.hydra.modules.discord.commands;

import net.dv8tion.jda.api.JDA;

import java.util.List;

public class PingCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();
        jda.getRestPing().queue(ping -> {
            String msg = "REST ping: `%sms`\nWS ping: `%sms`";
            long wsPing = jda.getGatewayPing();
            ctx.getChannel()
                .sendMessageFormat(msg, ping, wsPing)
                .queue();
        });
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public List<String> getHelp() {
        return List.of(
            "display current latency in ms for REST and WebSocket",
            "usage: `.ping`"
        );
    }

}
