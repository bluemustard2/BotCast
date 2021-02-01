package com.github.bluemustard2;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;

public class YouTubeSearch {
    public static String linkGenerator(List<SearchResult> list) {
        SearchResult first = list.stream().findFirst().orElse(null);
        if (first == null) return null;

        return String.format("https://youtube.com/watch?v=%s", first.getId().getVideoId());
    }

    public static String search(List<String> searchTerms) {
        try {
            YouTube yt = new YouTube.Builder(newTrustedTransport(), new JacksonFactory(), null)
                    .setApplicationName("BotCast")
                    .build();

            return linkGenerator(
                    yt.search()
                            .list(Collections.singletonList("snippet"))
                            .setQ(String.join(" ", searchTerms))
                            .setKey(System.getenv("YOUTUBE_API_KEY"))
                            .execute()
                            .getItems()
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
