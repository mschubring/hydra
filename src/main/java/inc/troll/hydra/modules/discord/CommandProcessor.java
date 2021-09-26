package inc.troll.hydra.modules.discord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandProcessor {

    public void process(MessageReceivedEvent event) {

        Message message = event.getMessage();
        switch(parseCommand(message)) {
            case HELP:
                displayCommands(event);
                break;

            case NONE:
                break;

            case PLAY:
                break;

            case QUEUE:
                break;

            case SKIP:
                break;

            case STOP:
                break;

            default:
                break;
        }

        // MessageChannel channel = event.getChannel();
        // if(message.getContentRaw().equals("!ping")) {
        //     long time = System.currentTimeMillis();
        //     channel.sendMessage("Pong!").queue(res -> {
        //         long diff = System.currentTimeMillis() - time;
        //         res.editMessageFormat("Pong: %d ms", diff).queue();
        //     });
        // }
    }

    private Commands parseCommand(Message message) {
        String text = message.getContentStripped();
        return Commands.parse(text);
    }

    private void displayCommands(MessageReceivedEvent event) {
        StringBuilder help = new StringBuilder()
            .append("some helpful foobar text");

        event.getChannel()
            .sendMessage(help.toString())
            .queue();
    }
}
