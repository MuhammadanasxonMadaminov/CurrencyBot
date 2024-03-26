package org.currency.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.currency.Properties.TranslationProps;
import org.currency.bean.Currency;
import org.currency.bean.Steps;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MessageHandler implements BaseHandler {
    private TelegramLongPollingBot bot;

    @Override
    public void handle(Update update, TelegramLongPollingBot bot) {
        this.bot = bot;

        String chatId = update.getMessage().getChatId().toString();
        Message message = update.getMessage();
        System.out.println(" = " +update);

        sendMenu(chatId,message);
    }

    private void sendMenu(String chatId, Message message) {
        String step = Steps.get(chatId);

        if(step.equals("main")) {
            sendMainMenu(chatId);
        }else if(step.startsWith("second")) {
            String[] curr =step.split("_");
            exchangeMenu(chatId,curr[1],curr[2], message);
        }
    }

    private void exchangeMenu(String chatId, String from, String to, Message message) {
        Double money = Double.parseDouble(message.getText());
        Double fromCurr = getRate(from);
        Double sum = money * fromCurr;

        Double toCurr = getRate(to);
        double result = sum / toCurr;


        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Sizning " + money + " " + from + " pulingiz " + result + " " + to + " bo'ladi!");

        Steps.set(chatId,"main");

        try {
            bot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private Double getRate(String currency) {
        try {
            if(currency.equals("UZS")) {
                return 1.0;
            }

            Gson gson = new Gson();
            TypeToken<?> type = TypeToken.getParameterized(List.class, Currency.class);

            List<Currency> currencies = gson.fromJson(Files.readString(Path.of("currencies.json")), type.getType());

            for (Currency curr : currencies) {
                if(curr.getShortName().equals(currency)) {
                    return curr.getRate();
                }
            }
            return 0.0;
        } catch (IOException e) {
            return 0.0;
        }
    }

    private void sendMainMenu(String chatId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(TranslationProps.get("en","greeting"));

        sm.setReplyMarkup(getMainInlineKeyboard());
        try {
            bot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboard getMainInlineKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton b1 = new InlineKeyboardButton();
        b1.setText("Get Started");
        b1.setCallbackData("getStarted");

        keyboardMarkup.setKeyboard(List.of(List.of(b1)));
        return keyboardMarkup;
    }


}
