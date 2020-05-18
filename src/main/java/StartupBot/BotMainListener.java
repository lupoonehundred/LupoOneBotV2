package StartupBot;

import StartupBot.BotListeners.BotVerification;
import StartupBot.BotVoice.BotAudio;

import java.util.ArrayList;

public class BotMainListener extends BotLogin {
    public void mainListeners() {
        getApi().addMessageCreateListener(mainEvent -> {
            getLogger().info("Received a Message, checking for content " + mainEvent.getMessage().getContent());
            switch (mainEvent.getMessage().getContent()) {
                case "!request":
                    ArrayList<String> musicQueue = new ArrayList<>();
                    musicQueue.add(mainEvent.getMessage().getContent().substring(9));
                case "!song":
                case "!skip":
                case "!stop":
                case "!join":
                    getLogger().info("Sending to the BotAudio with a Server and the Event.");
                    new BotAudio(getCurrentServer(), mainEvent, musicQueue).botMusic();
                    break;
            }
        });
        getApi().addServerMemberJoinListener(new BotVerification());
    }
}
/*
if(mainEvent.getMessage().getContent().equalsIgnoreCase("!setup")) {
                getLogger().info("Sending to the BotSetup with an Api, a User and an Event.");
                //BotSetup(getApi(), mainEvent.getMessage().getUserAuthor().get(), mainEvent);
            }
            else if("!request | !song | !skip | !stop".matches(mainEvent.getMessage().getContent())) {
                getLogger().info("Sending to the BotAudio with a Server and the Event.");
                //BotAudio(getCurrentServer(), mainEvent);
            }
 */