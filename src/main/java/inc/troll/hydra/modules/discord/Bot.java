package inc.troll.hydra.modules.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bot {

    private final JDA jda;
}
