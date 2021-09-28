package inc.troll.hydra.modules.discord;

import inc.troll.hydra.config.HydraConfig;
import inc.troll.hydra.modules.discord.commands.HelpCommand;
import inc.troll.hydra.modules.discord.commands.PingCommand;
import inc.troll.hydra.modules.discord.commands.PlayCommand;
import lombok.Getter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@Component
public class HydraBot extends ListenerAdapter {

    private final HydraConfig config;

    public HydraBot(HydraConfig config)
        throws LoginException, InterruptedException
    {
        this.config = config;
        JDABuilder.createDefault(config.getBotToken())
            .setStatus(OnlineStatus.ONLINE)
            .enableCache(CacheFlag.VOICE_STATE)
            .setActivity(Activity.of(ActivityType.LISTENING, "... euch"))
            .addEventListeners(this)
            .build()
            .awaitReady();
    }

    @Getter
    private CommandManager commandManager;

    @PostConstruct
    private void initCommands() {
        commandManager = new CommandManager(config);
        commandManager.add(new PlayCommand());
        commandManager.add(new PingCommand());
        commandManager.add(new HelpCommand());
        // new commands go here
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        long channelId = event.getChannel().getIdLong();
        long hydraChannelId = config.getChannelId();
        if( event.getAuthor().isBot() ||
            event.isWebhookMessage() ||
            channelId != hydraChannelId
        ) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        if(message.startsWith(config.getPrefix())) {
            commandManager.handle(event);
        }
    }
}
