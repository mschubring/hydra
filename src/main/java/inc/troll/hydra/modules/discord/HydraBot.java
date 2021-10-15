package inc.troll.hydra.modules.discord;

import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import inc.troll.hydra.config.HydraConfig;
import inc.troll.hydra.modules.audio.HydraManager;
import inc.troll.hydra.modules.discord.commands.HelpCommand;
import inc.troll.hydra.modules.discord.commands.PingCommand;
import inc.troll.hydra.modules.discord.commands.PlayCommand;
import inc.troll.hydra.modules.discord.commands.SkipCommand;
import inc.troll.hydra.modules.discord.commands.StopCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@Slf4j
@Component
public class HydraBot extends ListenerAdapter {

    private static final String HYDRA_TRACK = "tracks/heil_hydra.mp3";
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
        commandManager.add(new HelpCommand(commandManager::getRegisteredCommands)); // yep a little ugly :(
        commandManager.add(new StopCommand());
        commandManager.add(new SkipCommand());
        // new commands go here
    }

    /**
     * log basic guild information to identify potention abuse
     * @param event the occurred guild event
     */
    private void logGuildInfo(@Nonnull GenericGuildEvent event) {
        Guild guild = event.getGuild();
        long id = guild.getIdLong();
        String name = guild.getName();

        log.info("guild: {} ({})", name, id);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        logGuildInfo(event);

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

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        logGuildInfo(event);
        if(hasHydraJoinedVoiceChannel(event)) {
            playHydraTrack(event.getGuild());
        }
        // do other stuff, if needed
    }

    /**
     * {@code true} if the event was hydra bot joining the voice channel
     * @param event the GuildVoiceJoinEvent of the bot.
     * @return <code>true</code> of the bot is the user joining the channel.
     */
    private boolean hasHydraJoinedVoiceChannel(GuildVoiceJoinEvent event) {
        long selfId = event.getGuild().getSelfMember().getIdLong();
        long memberId = event.getMember().getIdLong();
        return selfId == memberId;
    }

    /**
     * play MP3 file of location 'resources:tracks/heil_hydra.mp3'
     * @param guild the server hydra should play the song at.
     */
    private void playHydraTrack(Guild guild) {
        HydraManager.getInstance().loadAndPlayFromClassPath(guild, HYDRA_TRACK);
    }
}
