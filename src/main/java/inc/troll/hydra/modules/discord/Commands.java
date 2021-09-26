package inc.troll.hydra.modules.discord;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum Commands {

    PLAY(".play"),
    STOP(".stop"),
    SKIP(".skip"),
    QUEUE(".queue"),
    HELP(".help"),
    NONE("");

    private final String text;

    public static Commands parse(String text) {
        String command = StringUtils.trimToEmpty(text)
            .toLowerCase()
            .split(" ")[0];

        return Stream.of(Commands.values())
            .filter(cmd -> cmd.getText().equals(command))
            .peek(cmd -> log.debug("command: {}", cmd))
            .findFirst()
            .orElseGet(() -> {
                log.debug("could not parse '{}' to command", text);
                return NONE;
            });
    }

    public static String list() {
        return Stream.of(Commands.values())
            .filter(cmd -> cmd != NONE)
            .map(Commands::getText)
            .collect(Collectors.joining("\n- ","- ",""));
    }
}
