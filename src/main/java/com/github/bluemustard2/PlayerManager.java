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

        // create a queue object
        trackQueue = new ArrayDeque<>();

        // create the backend stuff for LavaPlayer
        backend = new DefaultAudioPlayerManager();
        backend.registerSourceManager(new YoutubeAudioSourceManager());

        // create the actual Player and add a Listener to play the next track in the queue, if the queue is not empty.
        player = backend.createPlayer();
        player.addListener(event -> {
            // is the audio event we got a track ending?
            if (event instanceof TrackEndEvent) {
                TrackEndEvent endEvent = (TrackEndEvent) event;

                // was it ended forcefully or did it naturally finish the song?
                if (endEvent.endReason == AudioTrackEndReason.FINISHED) {

                    // get the next track in the queue, if there is none dip out
                    AudioTrack nextTrack = trackQueue.poll();
                    if (nextTrack == null) {
                        return;
                    }

                    // play the next track
                    playTrack(nextTrack);
                }
            }
        });

        // create the audio source that we will send to Discord. This is automatically linked to whatever the player
        // is doing
        audioSource = new LavaPlayerAudioSource(main.getDiscordApi(), player);

        // create the handler that deals with results loaded from YouTube
        loadResultHandler = new CoolAudioLoadResultHandler(main, this);
    }

    // This is literally the same thing as you had in that giant if for playing a song
    public void searchForAndQueueSong(List<String> searchTerms) {
        backend.loadItem(YouTubeSearch.search(searchTerms.toArray(String[]::new)), loadResultHandler);
    }

    public boolean isCurrentlyPlaying() {
        // how might we check if it's currently playing something?
        // don't overthink it
        throw new RuntimeException("Not yet implemented");
    }

    public void playTrack(AudioTrack track) {
        // play the given track plz
        // if there is already something playing, add it to the queue instead!
        throw new RuntimeException("Not yet implemented");
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
        // start playing again!!!!! this assumes it was paused before.
        throw new RuntimeException("Not yet implemented");
    }

    public void pausePlaying() {
        // stop playing immediately!!!!!!!!!! or puppies die.
        throw new RuntimeException("Not yet implemented");
    }

    public void clearQueue() {
        // clear the queue so that nothing else will play after the current song is done playing
        throw new RuntimeException("Not yet implemented");
    }

    public void skipSong() {
        // skip the current song, if there are no more songs stop playing
        throw new RuntimeException("Not yet implemented");
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
     * Gets the AudioSource that we can give to Discord
     *
     * @return the audio source
     */
    public AudioSource getAudioSource() {
        return audioSource;
    }
}
