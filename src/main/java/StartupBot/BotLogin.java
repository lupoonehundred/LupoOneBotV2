package StartupBot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerEvent;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

public class BotLogin {
    private static DiscordApi api;
    private static Server currentServer;
    private static final Logger logger = LogManager.getLogger(BotLogin.class.getName());

    public static void main(String[] args) {
        api = new DiscordApiBuilder().setToken(args[0]).login().join();
        FallbackLoggerConfiguration.setDebug(true);
        api.addServerJoinListener(event -> { currentServer = event.getServer(); });
        logger.info("Bot started up! under the name " + api.getYourself().getDiscriminatedName());
        new BotMainListener().mainListeners();
    }
    public static Logger getLogger() { return logger; }
    public static DiscordApi getApi() { return api; }
    public static Server getCurrentServer() { return currentServer; }
}
