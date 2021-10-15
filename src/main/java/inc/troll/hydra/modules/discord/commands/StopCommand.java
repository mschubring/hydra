package inc.troll.hydra.modules.discord.commands;

import inc.troll.hydra.modules.audio.HydraManager;

import java.util.List;

public class StopCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.areBotAndUserInSameVoiceChannel()) {
            HydraManager.getInstance()
                    .getMusicManager(ctx.getGuild())
                    .getScheduler()
                    .stopPlaying();

            ctx.getGuild()
                    .getAudioManager()
                    .closeAudioConnection();
        }
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public List<String> getHelp() {
        return List.of(
                "stop playing songs",
                "usage: `.stop`"
        );
    }

}
