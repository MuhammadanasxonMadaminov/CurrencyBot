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
        String nickname = update.getMessage().getFrom().getFirstName();

        sendMenu(chatId,message,nickname);
    }

    private void sendMenu(String chatId, Message message, String nickname) {
        String step = Steps.get(chatId);

        if(step.equals("main")) {
            sendMainMenu(chatId,nickname);
        }else if(step.startsWith("money")) {
            String[] curr =step.split("_");
            exchangeMenu(chatId,curr[1],curr[2], message,curr[3]);
        }
    }

    private void exchangeMenu(String chatId, String from, String to, Message message, String lang) {
        Double money = Double.parseDouble(message.getText());
        Double fromCurr = getRate(from);
        System.out.println("fromCurr = " + fromCurr);
        Double sum = money * fromCurr;

        Double toCurr = getRate(to);
        System.out.println("toCurr = " + toCurr);
        double result = sum / toCurr;
        System.out.println("result = " + result);
        String resultToString = Double.toString(result).split("\\.")[0] + "."
                + Double.toString(result).split("\\.")[1].substring(0,3);
        System.out.println("resultToString = " + resultToString);


        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(TranslationProps.get(lang,"exchangeMenu1") + money + " " + from + " "
                + TranslationProps.get(lang,"exchangeMenu2") + " " + resultToString + " " + to + "!");

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

    private void sendMainMenu(String chatId, String nickname) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(TranslationProps.get("en","greeting")+ " " + nickname + "! " + "\nWelcome to out Currency bot \nSelect a language:");

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
        b1.setText("UZ \uD83C\uDDFA\uD83C\uDDFF");
        b1.setCallbackData("uz");

        InlineKeyboardButton b2 = new InlineKeyboardButton();
        b2.setText("ENG \uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F");
        b2.setCallbackData("en");

        InlineKeyboardButton b3 = new InlineKeyboardButton();
        b3.setText("RUS \uD83C\uDDF7\uD83C\uDDFA");
        b3.setCallbackData("ru");

        keyboardMarkup.setKeyboard(List.of(List.of(b1,b2),List.of(b3)));
        return keyboardMarkup;
    }


}
