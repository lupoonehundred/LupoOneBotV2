package StartupBot.BotListeners;

import StartupBot.BotLogin;
import org.awaitility.core.ConditionTimeoutException;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.channel.user.PrivateChannelCreateEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.channel.user.PrivateChannelCreateListener;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;

public class BotVerification extends BotLogin implements ServerMemberJoinListener {
    @Override
    public void onServerMemberJoin(ServerMemberJoinEvent event) {
        getLogger().info("Sending to the BotVerification because a person joined the server. "
                + event.getUser().getDiscriminatedName());
        AtomicInteger tries = new AtomicInteger();
        String verifyName = event.getUser().getName();
        event.getUser().openPrivateChannel()
                .thenAcceptAsync(channel -> {
                    new MessageBuilder()
                        .append("You have joined the server " + event.getServer().getName() + ".")
                        .append(" Please Verify your Identity by responding with you name.")
                        .append(" IE: LupoOneBot (Case Sensitive)")
                        .send(channel);
                    boolean verified = false;
verification:
                    while((tries.get() < 3)) {
                        try{
                            await().atMost(20, TimeUnit.SECONDS).ignoreExceptions().until(() -> channel.getMessages(0)
                                    .get().getNewestMessage().get().getContent().equals(verifyName));
                            getLogger().info("Finished waiter, and now returning a response");
                            verified = channel.getMessages(0).get().getNewestMessage().get()
                                    .getContent().equals(verifyName);
                            if(verified){ break verification; }
                        } catch (InterruptedException | ExecutionException | CompletionException | ConditionTimeoutException e) {
                            getLogger().error("Exception caught ", e);
                        }
                        new MessageBuilder()
                                .append("User not been verified.")
                                .append(" Reply with you name ")
                                .append("(Remember its Case Sensitive)")
                                .send(channel);
                        tries.getAndIncrement();
                    }
                    if(verified) {
                        Role verifiedRole = event.getServer().getRolesByNameIgnoreCase("verified").get(0);
                        event.getServer().addRoleToUser(event.getUser(), verifiedRole, "Was Verified");
                        getLogger().info("Veririfed User assigned role to User " + event.getUser());
                        try {
                            new MessageBuilder()
                                    .append(Files.readString(Paths.get("src/main/resources/!help.txt"), StandardCharsets.UTF_8))
                                    .send(channel);
                        } catch (IOException e) {
                            getLogger().error("Error Reading file ", e);
                        }
                    }
                    else {
                        Role unverifiedRole = event.getServer().getRolesByNameIgnoreCase("unverified").get(0);
                        event.getServer().addRoleToUser(event.getUser(), unverifiedRole, "Was not Verified");
                        getLogger().info("Unverified User assigned role to User " + event.getUser());
                    }
                })
                .exceptionally(throwable -> {
                    getLogger().error("Error opening the private channel with "
                            + event.getUser().getDiscriminatedName(), throwable);
                    return null;
                });
    }
}
