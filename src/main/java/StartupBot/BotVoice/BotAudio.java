package StartupBot.BotVoice;

import StartupBot.BotLogin;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.channel.server.voice.ServerVoiceChannelMemberJoinEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberJoinListener;
import org.javacord.lavaplayerwrapper.youtube.YouTubeAudioSource;
import org.javacord.lavaplayerwrapper.youtube.YouTubeAudioSourceBuilder;

import java.util.ArrayList;

public class BotAudio extends BotLogin {

    private final Server musicServer;
    private final MessageCreateEvent messageEvent;
    private final ArrayList<String> musicQueue;

    public BotAudio(Server server, MessageCreateEvent event, ArrayList<String> musicQueue) {
        musicServer = server;
        messageEvent = event;
        this.musicQueue = musicQueue;
    }

    public void botMusic() {
        getApi().addServerVoiceChannelMemberLeaveListener(leaveEvent -> {
            leaveEvent.getServer().getAudioConnection().ifPresent(connection -> {
                if (connection.getChannel() == leaveEvent.getChannel()) {
                    if (leaveEvent.getChannel().getConnectedUsers().size() <= 1) {
                        leaveEvent.getServer().getTextChannelsByName("music-text-channel").get(0)
                                .sendMessage("Bot has left the VC, Music from queue has been removed");
                        connection.close();
                    }
                }
            });
        });

        if(messageEvent.getMessage().getContent().equals("!join")) {
            ServerVoiceChannel voiceChannel =
                    messageEvent.getMessageAuthor()
                                .asUser().get()
                                .getConnectedVoiceChannel(musicServer).get();
            for(String url : musicQueue) {
                voiceChannel.connect()
                    .thenAcceptAsync(connection -> {
                        connection.queue(YouTubeAudioSource.of(getApi(), url).join());
                        getApi().addMessageCreateListener(event -> {
                            switch (event.getMessage().getContent()) {
                                case "!skip":
                                    connection.dequeueCurrentSource();
                                case "!stop":
                                    connection.close().exceptionally(throwable -> {
                                        getLogger().error("Closing connection Failed ", throwable);
                                        return null;
                                    });
                                case "!song":
                                    cSongEvent(url, event.getChannel());
                            }
                        });
                    })
                    .exceptionally(throwable -> {
                        messageEvent.getChannel().sendMessage("Failed to start song" + url);
                        getLogger().error("Failed to start song", throwable);
                        return null;
                    });
            }
        }
        else
            messageEvent.getChannel().sendMessage("Please Join a Voice Channel First");
    }

    public void cSongEvent(String url, TextChannel channel) {
        new YouTubeAudioSourceBuilder(getApi())
                .setUrl(url)
                .build()
                .thenAcceptAsync(youTube -> {
                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor(youTube.getTitle())
                        .setTitle(youTube.getTitle())
                        .setDescription("Uploaded By " + youTube.getCreatorName())
                        .addField("The URL", url)
                        .addField("Duration", youTube.getDuration().toString());
                channel.sendMessage(embed);
        });
    }
}
