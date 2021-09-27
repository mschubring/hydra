package inc.troll.hydra.modules.discord;

import inc.troll.hydra.config.HydraConfig;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@Service
@RequiredArgsConstructor
public class HydraBot extends ListenerAdapter {

    private final HydraConfig config;
    private final CommandProcessor commandProcessor;
    private JDA jda;

    @PostConstruct
    public JDA initDiscordClient()
        throws LoginException, InterruptedException
    {
        if (jda != null) {
            return jda;
        }

        return JDABuilder.createDefault(config.getBotToken())
            .setStatus(OnlineStatus.ONLINE)
            .setActivity(Activity.of(ActivityType.LISTENING, "... euch"))
            .addEventListeners(this)
            .build()
            .awaitReady();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel chanel = event.getChannel();
        long hydraChannelId = config.getChannelId();
        if(chanel.getIdLong() == hydraChannelId) {
            commandProcessor.process(event);
        }
    }
}
