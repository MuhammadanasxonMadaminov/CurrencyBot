package org.currency.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.currency.Properties.TranslationProps;
import org.currency.bean.Currency;
import org.currency.bean.Steps;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CallbackHandler implements BaseHandler {
    private TelegramLongPollingBot bot;

    @Override
    public void handle(Update update, TelegramLongPollingBot bot) {
        this.bot = bot;

        String chatId = update.getCallbackQuery().getFrom().getId().toString();
        CallbackQuery callbackQuery = update.getCallbackQuery();

        sendMenu(chatId,callbackQuery);
    }
    private String lang;


    private void sendMenu(String chatId, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
//        Integer messageId = callbackQuery.getMessage().getMessageId();

        if(data.equals("uz") || data.equals("en") || data.equals("ru")) {
            lang = data;
            sendFromMenu(chatId,lang);
        } else if (data.startsWith("from")) {
            String from = data.split("_")[1];
            Steps.set(chatId,"money" + "_" + from);
            sendToMenu(chatId,lang);
        } else if (data.startsWith("to")) {
            String to = data.split("_")[1];
            String step = Steps.get(chatId);
            Steps.set(chatId, step + "_" + to + "_" + lang);
            sendMoneyMenu(chatId);
        }

    }

    private void sendFromMenu(String chatId, String lang) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(TranslationProps.get(lang,"from"));

        sm.setReplyMarkup(getFromCurrs());

        try {
            bot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendToMenu(String chatId, String lang) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(TranslationProps.get(lang,"to"));

        sm.setReplyMarkup(getToCurrs());

        try {
            bot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMoneyMenu(String chatId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(TranslationProps.get(lang,"howMuch"));


        try {
            bot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboard getFromCurrs() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        try {
            Gson gson = new Gson();

            TypeToken type =TypeToken.getParameterized(List.class, Currency.class);
            List<Currency> currencies =gson.fromJson(Files.readString(Path.of("currencies.json")), type.getType());

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            int counter = 0, c = 0;

            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton b2 = new InlineKeyboardButton();
            b2.setText("UZS");
            b2.setCallbackData("from_UZS");
            row.add(b2);
            counter++;
            for (Currency currency : currencies) {
                if (c == 5) {
                    break;
                }
                if(counter == 3) {
                    keyboard.add(row);
                    row = new ArrayList<>();
                    counter = 0;
                    c++;
                }

                InlineKeyboardButton b1 = new InlineKeyboardButton();
                b1.setText(currency.getShortName());
                b1.setCallbackData("from_" + currency.getShortName());
                row.add(b1);
                counter++;
            }

            inlineKeyboardMarkup.setKeyboard(keyboard);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return inlineKeyboardMarkup;

    }
    private ReplyKeyboard getToCurrs() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        try {
            Gson gson = new Gson();

            TypeToken type =TypeToken.getParameterized(List.class, Currency.class);
            List<Currency> currencies =gson.fromJson(Files.readString(Path.of("currencies.json")), type.getType());

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            int counter = 0, c = 0;

            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton b2 = new InlineKeyboardButton();
            b2.setText("UZS");
            b2.setCallbackData("to_UZS");
            row.add(b2);
            counter++;
            for (Currency currency : currencies) {
                if (c == 5) {
                    break;
                }
                if(counter == 3) {
                    keyboard.add(row);
                    row = new ArrayList<>();
                    counter = 0;
                    c++;
                }

                InlineKeyboardButton b1 = new InlineKeyboardButton();
                b1.setText(currency.getShortName());
                b1.setCallbackData("to_" + currency.getShortName());
                row.add(b1);
                counter++;
            }



            inlineKeyboardMarkup.setKeyboard(keyboard);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return inlineKeyboardMarkup;

    }



}
