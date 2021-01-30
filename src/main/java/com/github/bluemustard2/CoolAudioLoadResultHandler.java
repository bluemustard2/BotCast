package com.github.bluemustard2;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public class CoolAudioLoadResultHandler implements AudioLoadResultHandler {
    private final Main main;
    private final PlayerManager playerManager;

    public CoolAudioLoadResultHandler(Main main, PlayerManager manager) {
        this.main = main;
        this.playerManager = manager;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if (playerManager.isCurrentlyPlaying()) {
            // currently playing, add track to queue
            playerManager.addTrackToQueue(track);
        } else {
            // nothing playing, play track now
            playerManager.playTrack(track);
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        List<AudioTrack> tracks = playlist.getTracks();

        if (playerManager.isCurrentlyPlaying()) {
            // currently playing a song, add all of the tracks to the queue to player later
            playerManager.addTracksToQueue(tracks);
        } else {
            // nothing is currently playing, play first track
            playerManager.playTrack(tracks.get(0));
            // add rest of the tracks to the queue
            playerManager.addTracksToQueue(tracks.subList(1, tracks.size()));
        }
    }

    @Override
    public void noMatches() {
        main.getCommandChannel().sendMessage("No songs were found for that query.");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        main.getCommandChannel().sendMessage(exception.getMessage());
    }
}
