package com.github.bluemustard2;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import java.util.Arrays;
import java.util.Deque;
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
    private ServerVoiceChannel musicChannel;

    private void run() {
        discordApi = new DiscordManager().logIn();
        playerManager = new PlayerManager(this);
        commandChannel = discordApi.getServerTextChannelById(System.getenv("COMMAND_CHANNEL_ID")).orElse(null);
        musicChannel = discordApi.getServerVoiceChannelById(System.getenv("MUSIC_CHANNEL_ID")).orElse(null);

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
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to the voice channel.");
        }

        discordApi.addMessageCreateListener(event -> {
            List<String> messageParts = Arrays.asList(event.getMessage().getContent().split(" "));

            String command = messageParts.get(0);
            if (command.equalsIgnoreCase("!play")) {
                event.getChannel().sendMessage("Adding song to play queue!");
                playerManager.searchForAndQueueSong(messageParts.subList(1, messageParts.size()));
            } else if (command.equalsIgnoreCase("!playurl")) {
                event.getChannel().sendMessage("Added song to play queue!");
                playerManager.queueUrl(messageParts.get(1));
            } else if (command.equalsIgnoreCase("!stop")) {
                event.getChannel().sendMessage("Stopping all playback including queued songs.");
                playerManager.stopAndClearQueue();
            } else if (command.equalsIgnoreCase("!pause")) {
                event.getChannel().sendMessage("Pausing (if there is anything to pause!)");
                playerManager.pausePlaying();
            } else if (command.equalsIgnoreCase("!resume")) {
                event.getChannel().sendMessage("Unpausing (if there is anything to play!)");
                playerManager.resumePlaying();
            } else if (command.equalsIgnoreCase("!skip")) {
                event.getChannel().sendMessage("Skipping song!");
                playerManager.skipSong();
            } else if (command.equalsIgnoreCase("!np")) {
                AudioTrack track = playerManager.getCurrentSong();
                if (track == null) {
                    event.getChannel().sendMessage("Nothing is currently playing.");
                } else {
                    AudioTrackInfo info = track.getInfo();
                    event.getChannel().sendMessage(String.format("%s - %s", info.author, info.title));
                }
            } else if (command.equalsIgnoreCase("!queue")) {
                Deque<AudioTrack> tracks = playerManager.getTrackQueue();
                if (tracks.isEmpty()) {
                    event.getChannel().sendMessage("Nothing is queued.");
                } else {
                    event.getChannel().sendMessage("Track Queue:");
                    playerManager.getTrackQueue().forEach(track -> {
                        AudioTrackInfo info = track.getInfo();
                        event.getChannel().sendMessage(String.format("%s - %s", info.author, info.title));
                    });
                }
            }
        });
    }

    public DiscordApi getDiscordApi() {
        return discordApi;
    }

    public ServerTextChannel getCommandChannel() {
        return commandChannel;
    }

    public ServerVoiceChannel getMusicChannel() {
        return musicChannel;
    }
}