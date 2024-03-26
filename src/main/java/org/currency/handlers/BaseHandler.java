package org.currency.handlers;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BaseHandler {
    void handle(Update update, TelegramLongPollingBot bot);
}
