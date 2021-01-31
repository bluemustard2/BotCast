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
        String iD = list.toString().replace("\"", "").replace("}", "");
        String[] listTerms = iD.split(",");
        String line = null;

        for (String blank : listTerms) {
            if (blank.contains("videoId")) {
                line = blank.substring(blank.indexOf(":") + 1);
                break;
            }
        }

        line = "https://youtube.com/watch?v=" + line;

        System.out.println("Found link: " + line);

        return line;
    }

    public static String search(String[] messageContent) {
        List<String> searchTerms = Arrays.asList(messageContent).subList(1, messageContent.length);

        try {
            YouTube yt = new YouTube.Builder(newTrustedTransport(), new JacksonFactory(), null)
                    .setApplicationName("BotCast")
                    .build();

            List<SearchResult> list = yt.search().list(Collections.singletonList("snippet")).setQ(Arrays.toString(searchTerms.toArray()).replace("[", "").replace("]", "")).setKey(System.getenv("YOUTUBE_API_KEY")).execute().getItems();

            return linkGenerator(list);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
