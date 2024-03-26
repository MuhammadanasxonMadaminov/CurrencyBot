package org.currency.Properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BotProps {
    public static String get(String key){
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("resources/bot.properties"));

            return props.getProperty(key);
        } catch (IOException e) {
            return null;
        }
    }
}
