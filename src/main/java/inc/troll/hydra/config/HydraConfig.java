package inc.troll.hydra.config;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

import java.util.Arrays;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties("hydra")
public class HydraConfig {


    private String botToken;

    @Bean
    public JDA jda() throws LoginException, InterruptedException {
        return JDABuilder.createDefault(botToken)
            .setStatus(OnlineStatus.ONLINE)
            // .disableCache(CacheFlag.ACTIVITY)
            // .enableIntents(getIntensList())
            // .setMemberCachePolicy(memberCachePolicy())
            .setActivity(Activity.of(ActivityType.LISTENING, "... euch"))
            .addEventListeners(readyEvent(), messageReceived())
            .build()
            .awaitReady();
    }

    private MemberCachePolicy memberCachePolicy() {
        return MemberCachePolicy.VOICE
            .or(MemberCachePolicy.OWNER);
    }

    private List<GatewayIntent> getIntensList() {
        return Arrays.asList(
            GatewayIntent.GUILD_MESSAGES
        );
    }

    private EventListener readyEvent() {
        return new EventListener() {

            @Override
            public void onEvent(GenericEvent event) {
                if (event instanceof ReadyEvent) {
                    System.out.println("API is ready!");
                }
            }
        };
    }

    private EventListener messageReceived() {
        return new EventListener() {

            @Override
            public void onEvent(GenericEvent event) {
                if (event instanceof MessageReceivedEvent) {
                    MessageReceivedEvent messageEvent = (MessageReceivedEvent) event;
                    Message message = messageEvent.getMessage();
                    MessageChannel channel = messageEvent.getChannel();

                    if(message.getContentRaw().equals("!ping")) {
                        long time = System.currentTimeMillis();
                        channel.sendMessage("Pong!").queue(res -> {
                            long diff = System.currentTimeMillis() - time;
                            res.editMessageFormat("Pong: %d ms", diff).queue();
                        });
                    }
                }
            }
        };
    }
}
