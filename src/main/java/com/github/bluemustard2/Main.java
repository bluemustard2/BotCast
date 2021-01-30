package com.github.bluemustard2;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FallbackLoggerConfiguration.setDebug(true);
        FallbackLoggerConfiguration.setTrace(true);

        new Main().run();
    }

    private DiscordApi discordApi;
    private PlayerManager playerManager;
    private ServerTextChannel commandChannel;

    private void run() {
        discordApi = new DiscordManager().logIn();

        commandChannel = discordApi.getServerTextChannelById(System.getenv("COMMAND_CHANNEL_ID")).orElse(null);
        ServerVoiceChannel musicChannel = discordApi.getServerVoiceChannelById(System.getenv("MUSIC_CHANNEL_ID")).orElse(null);
        if (commandChannel == null || musicChannel == null) {
            throw new RuntimeException("Failed to find command channel or music channel. Check the IDs.");
        }

        // Connect to the voice channel once we log in
        // Exit if connection fails (for we are useless if we don't have a voice)
        try {
            musicChannel.connect()
                    .thenAccept(connection -> connection.setAudioSource(playerManager.getAudioSource()))
                    .join();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to connect to the voice channel.");
        }

        playerManager = new PlayerManager(this);

        discordApi.addMessageCreateListener(event -> {
            List<String> messageParts = Arrays.asList(event.getMessage().getContent().split(" "));

            String command = messageParts.get(0);
            if (command.equalsIgnoreCase("!play")) {
                playerManager.searchForAndQueueSong(messageParts.subList(1, messageParts.size()));
            } else if (command.equalsIgnoreCase("!stop")) {
                playerManager.clearQueue();
                playerManager.skipSong();
            } else if (command.equalsIgnoreCase("!pause")) {
                playerManager.pausePlaying();
            } else if (command.equalsIgnoreCase("!resume")) {
                playerManager.resumePlaying();
            }
        });
    }

    public DiscordApi getDiscordApi() {
        return discordApi;
    }

    public ServerTextChannel getCommandChannel() {
        return commandChannel;
    }
}