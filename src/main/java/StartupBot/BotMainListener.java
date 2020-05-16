package StartupBot;

import StartupBot.BotListeners.BotSetup;
import StartupBot.BotListeners.BotVerification;

public class BotMainListener extends BotLogin {
    public void mainListeners() {
        getApi().addMessageCreateListener(mainEvent -> {
            getLogger().info("Received a Message, checking for content " + mainEvent.getMessage().getContent());
            switch (mainEvent.getMessage().getContent()) {
                case "!setup":
                    getLogger().info("Sending to the BotSetup with an Api, a User and an Event.");
                    new BotSetup(mainEvent.getMessage().getUserAuthor().get(), mainEvent);
                    break;
                case "!request":
                case "!song":
                case "!skip":
                case "!stop":
                    getLogger().info("Sending to the BotAudio with a Server and the Event.");
                    //BotAudio(getCurrentServer(), mainEvent);
                    break;
            }
        });
        getApi().addServerMemberJoinListener(new BotVerification());

        getLogger().info("Sending to the BotAudio because someone joined a voice channel.");
        //getApi().addServerVoiceChannelMemberJoinListener(new BotAudio());
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