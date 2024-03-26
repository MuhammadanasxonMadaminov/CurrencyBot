package org.currency.bot;

import org.currency.Properties.BotProps;
import org.currency.handlers.BaseHandler;
import org.currency.handlers.CallbackHandler;
import org.currency.handlers.MessageHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingBot {
    private final BaseHandler messageHandler;
    private final BaseHandler callbackHandler;

    public Bot() {
        this.messageHandler = new MessageHandler();
        this.callbackHandler = new CallbackHandler();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            messageHandler.handle(update, this);
        } else {
            callbackHandler.handle(update, this);
        }
    }

    @Override
    public String getBotUsername() {
        return BotProps.get("bot.username");
    }

    @Override
    public String getBotToken() {
        return BotProps.get("bot.token");
    }
}
