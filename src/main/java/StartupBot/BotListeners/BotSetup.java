package StartupBot.BotListeners;

import StartupBot.BotLogin;
import org.awaitility.core.ConditionTimeoutException;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class BotSetup extends BotLogin {
    private User owner;
    private MessageCreateEvent event;

    public BotSetup(User userAuthor, MessageCreateEvent givenEvent) {
        owner = userAuthor;
        event = givenEvent;
        setupCommand();
    }

    public void setupCommand() {
        owner.openPrivateChannel().thenAcceptAsync(channel -> {
            new MessageBuilder()
                .append("You used the !setup command.")
                .append(" Do you want to me to Create a new server or")
                .append(" Add to an existing server? ")
                .append(" (Create, Existing, Exit) ")
                .send(channel);
            try {
                Message confirm = channel.getMessagesAfter(0, 10000).get().getNewestMessage().get();
                if(confirm.getContent().equalsIgnoreCase("create")) {
                    channel.sendMessage("Please provide a Name for the server.");
                    String nameOfServer = channel.getMessagesAfter(0, 10000).get()
                            .getNewestMessage().get().getContent();
                    createServer(nameOfServer);
                }
                else if(confirm.getContent().equalsIgnoreCase("existing")) {
                    new MessageBuilder()
                            .append("Do you want me too add to the server called from ")
                            .append("or a different server? (Existing, New, Exit")
                            .send(channel);
                    Message servercreate = channel.getMessagesAfter(0, 10000).get().getNewestMessage().get();
                    if(servercreate.getContent().equalsIgnoreCase("existing") &&
                            getApi().getServerById(event.getServer().get().getId()).get().isAdmin(owner)) {
                        channel.sendMessage("Adding to called server " + event.getServer().get().getName());
                        addToServer(event.getServer().get().getId());
                    }
//                    else if(servercreate.getContent().equalsIgnoreCase("new")) {
//                        long id = channel.sendMessage("Please provide the ServerID").get().getId();
//                        try {
//                            await().atLeast(10, TimeUnit.SECONDS).until(() ->
//                                    !channel.getMessagesAfter(0, id).get().isEmpty());
//                            long serverID = channel.getMessages(0).get().getNewestMessage().get().getId();
//                            long messageID = new MessageBuilder()
//                                    .append("Received a server by the name of ")
//                                    .append(getApi().getServerById(serverID).get().getName())
//                                    .append(". Is this correct?")
//                                    .send(channel)
//                                    .get().getId();
//                            switch (channel.getMessagesAfter(0, messageID).get().getNewestMessage().get().getContent()) {
//                                case "yes":
//                                case "Yes":
//                                    addToServer(serverID);
//                                case "no":
//                                case "No":
//                                    addToServer(event.getServer().get().getId());
//                            }
//                        } catch(InterruptedException | ExecutionException ie) {
//                            getLogger().error("Something went wrong at message receiving", ie);
//                        }
//                    }
                }
            } catch(InterruptedException | ExecutionException | CompletionException | ConditionTimeoutException e) {
                getLogger().error("Wait time for answer exceeded", e);
            }
        });
    }
}
