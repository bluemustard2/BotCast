package com.github.bluemustard2;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;

import java.util.Optional;

public class PlayerManager {

    protected static String line;
    protected static AudioSource source;

    public AudioPlayer Manager() {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioPlayer player = playerManager.createPlayer();

        Optional<ServerVoiceChannel> channel = api.getServerVoiceChannelById(System.getenv("SERVER_ID"));

        if (channel.isPresent()) {
            ServerVoiceChannel unwrapped = channel.get();

            unwrapped.getId();
            unwrapped.connect().thenAccept(audioConnection -> {
                source = new LavaplayerAudioSource(api, player);
                audioConnection.setAudioSource(source);
            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });

            api.addMessageCreateListener(event -> {
                String[] messageContent = event.getMessageContent().split(" ");

                line = YouTubeSearch.Search(messageContent);

                if (messageContent[0].equalsIgnoreCase("!play")) {
                    playerManager.loadItem(line, new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack track) {
                            player.playTrack(track);
                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist playlist) {
                            for (AudioTrack track : playlist.getTracks()) {
                                player.playTrack(track);
                            }
                        }

                        @Override
                        public void noMatches() {
                            System.out.println("Song could not be found!");
                        }

                        @Override
                        public void loadFailed(FriendlyException e) {
                            e.printStackTrace();
                        }
                    });
                } else if (messageContent[0].equalsIgnoreCase("!stop")) {
                    player.stopTrack();
                } else if (messageContent[0].equalsIgnoreCase("!pause")) {
                    player.setPaused(true);
                } else if (messageContent[0].equalsIgnoreCase("!resume")) {
                    player.setPaused(false);
                }
            });
        }
        return player;
    }
}
