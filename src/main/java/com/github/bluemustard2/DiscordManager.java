package com.github.bluemustard2;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class DiscordManager {
    public DiscordApi logIn() {
        return new DiscordApiBuilder()
                .setToken(System.getenv("QUOTE_BOT_TOKEN"))
                .login()
                .join();
    }
}
