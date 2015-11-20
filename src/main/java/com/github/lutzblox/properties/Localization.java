package com.github.lutzblox.properties;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;


public class Localization {

    public static final String THREAD_HAS_ERRORED = "thread_errored";
    public static final String SERVER_TIMEOUT = "server_timeout";
    public static final String PACKET_CONSTRUCTOR = "packet_constructor";
    public static final String UNREADABLE_PACKET = "unreadable_packet";
    public static final String ENCRYPTION_FAILED = "encryption_failed";
    public static final String DECRYPTION_FAILED = "decryption_failed";
    public static final String CAUSED_BY = "caused_by";

    private static boolean loaded = false;
    private static String locale = null;
    private static Properties properties = new Properties();

    public static String getMessage(String id, String... args) {

        if (!loaded) {

            load();
        }

        String message = properties.getProperty(id);

        if (message != null) {

            for (int i = 0; i < args.length; i++) {

                message = message.replace("{" + i + "}", args[i]);
            }

            return message;

        } else {

            return "NULL_MESSAGE (localization does not exist)";
        }
    }

    public static void load() {

        Locale l = Locale.getDefault();

        locale = l.getLanguage() + "_" + l.getCountry();

        InputStream propInput = Localization.class.getResourceAsStream("/locale/" + locale + ".properties");

        if (propInput != null) {

            try {

                properties.load(propInput);

                loaded = true;

            } catch (Exception e) {

                e.printStackTrace();
            }

        } else {

            propInput = Localization.class.getResourceAsStream("/locale/default.properties");

            if (propInput != null) {

                try {

                    properties.load(propInput);

                    loaded = true;

                } catch (Exception e) {

                    e.printStackTrace();
                }

            } else {

                new FileNotFoundException("Localization was not loaded correctly!  No localization for '" + getLocale() + "' and no default localizations could be found!").printStackTrace();
            }
        }
    }

    public static String getLocale() {

        if (!loaded || locale == null) {

            load();
        }

        return locale;
    }
}
