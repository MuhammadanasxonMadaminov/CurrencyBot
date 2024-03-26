package org.currency.Properties;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationProps {
    public static String get(String lang,String key) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("words",new Locale(lang));

            return bundle.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
