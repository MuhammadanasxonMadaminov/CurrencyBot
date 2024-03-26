package org.currency;

import org.currency.bot.Bot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new Bot());
            getCurrencies();
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private static void getCurrencies() {
        try {
            HttpClient client= HttpClient.newBuilder().build();
            HttpRequest request= HttpRequest
                    .newBuilder()
                    .uri(new URI("https://cbu.uz/uz/arkhiv-kursov-valyut/json/"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            Files.writeString(Path.of("currencies.json"),response.body());
            System.out.println("body = " + body);
            System.out.println(response.statusCode());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}