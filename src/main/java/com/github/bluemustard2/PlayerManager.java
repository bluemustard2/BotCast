package com.github.bluemustard2;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.javacord.api.audio.AudioSource;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class PlayerManager {
    private final Main main;
    private final Deque<AudioTrack> trackQueue;
    private final AudioPlayerManager backend;
    private final AudioPlayer player;
    private final AudioSource audioSource;
    private final CoolAudioLoadResultHandler loadResultHandler;

    public PlayerManager(Main main) {
        this.main = main;

        // Create a queue object
        trackQueue = new ArrayDeque<>();

        // Create LavaPlayer backend
        backend = new DefaultAudioPlayerManager();
        backend.registerSourceManager(new YoutubeAudioSourceManager());

        // Create the Player and add a Listener
        // If queue isn't empty, it will play the next track in the queue
        player = backend.createPlayer();
        player.addListener(event -> {
            // is the audio event we got a track ending?
            if (event instanceof TrackEndEvent) {
                TrackEndEvent endEvent = (TrackEndEvent) event;

                // Check if track was ended by user or the inevitable passage of time
                if (endEvent.endReason == AudioTrackEndReason.FINISHED) {

                    // Get next track in queue; if there isn't one, exit
                    AudioTrack nextTrack = trackQueue.poll();
                    if (nextTrack != null) {
                        player.playTrack(nextTrack);
                    }
 
                    // Play next track
                    playTrack(nextTrack);
                }
            }
        });

        // Create an audio source for Discord
        // Automatically linked to whatever the player is doing
        audioSource = new LavaPlayerAudioSource(main.getDiscordApi(), player);

        // Create a handler to deal with YouTube results
        loadResultHandler = new CoolAudioLoadResultHandler(main, this);
    }

    // This is literally the same thing as you had in that giant if for playing a song
    public void searchForAndQueueSong(List<String> searchTerms) {
        backend.loadItem(YouTubeSearch.search(searchTerms.toArray(String[]::new)), loadResultHandler);
    }

    public boolean isCurrentlyPlaying() {
        // Check if player is currently playing something
        return getCurrentSong() != null;
    }

    public void playTrack(AudioTrack track) {
        // Play given track
        // If something else is playing, add new track to the queue
        if (isCurrentlyPlaying()){
            trackQueue.add(track);
        } else {
            player.playTrack(track);
        }
    }

    /**
     * Adds the given track to the queue
     *
     * @param track the track to add
     */
    public void addTrackToQueue(AudioTrack track) {
        trackQueue.add(track);
    }

    /**
     * Adds all the tracks to the queue
     *
     * @param playlist the list of tracks to add
     */
    public void addTracksToQueue(List<AudioTrack> playlist) {
        trackQueue.addAll(playlist);
    }

    public void resumePlaying() {
        // Resume a paused track
        player.setPaused(false);
    }

    public void pausePlaying() {
        // Pause current track
        player.setPaused(true);
    }

    public void clearQueue() {
        // Clear the queue
        trackQueue.clear();
    }

    public void skipSong() {
        // Skip current song, stop if no other songs remain
        player.stopTrack();
        AudioTrack nextSong = trackQueue.poll();
        if (nextSong != null){
            player.playTrack(nextSong);
        }
    }

    /**
     * Gets the Main class so we can use it elsewhere
     *
     * @return the main class
     */
    public Main getMain() {
        return main;
    }

    /**
     * Gets the Track queue so we can inspect it
     *
     * @return the track queue
     */
    public Deque<AudioTrack> getTrackQueue() {
        return trackQueue;
    }

    /**
     * Gets the track currently playing
     *
     * @return the current track
     */
    public AudioTrack getCurrentSong() {
        return player.getPlayingTrack();
    }

    /**
     * Gets the AudioSource that we can give to Discord
     *
     * @return the audio source
     */
    public AudioSource getAudioSource() {
        return audioSource;
    }
}
