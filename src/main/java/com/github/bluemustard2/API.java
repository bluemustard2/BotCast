package com.github.bluemustard2;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class API {
    public DiscordApi apiMaker () {
        DiscordApi api = new DiscordApiBuilder()
                .setToken(System.getenv("QUOTE_BOT_TOKEN"))
                .login()
                .join();

        return api;
    }
}
