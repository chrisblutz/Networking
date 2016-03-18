package com.github.lutzblox.properties;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;


/**
 * This class holds all localization utilities
 *
 * @author Christopher Lutz
 */
public class Localization {

    public static final String THREAD_HAS_ERRORED = "thread_errored";
    public static final String SERVER_TIMEOUT = "server_timeout";
    public static final String PACKET_CONSTRUCTOR = "packet_constructor";
    public static final String UNREADABLE_PACKET = "unreadable_packet";
    public static final String ENCRYPTION_FAILED = "encryption_failed";
    public static final String DECRYPTION_FAILED = "decryption_failed";
    public static final String CAUSED_BY = "caused_by";
    public static final String DISALLOWED_FORCED_STATE_CHANGE = "disallowed_forced_state_change";
    public static final String ENCRYPTION_KEY_NULL = "encryption_key_null";
    public static final String MISSING_DATA_TYPE = "missing_data_type";
    public static final String NO_RESPONSE_EXPECTED = "no_response_expected";
    public static final String RESPONSE_TIMED_OUT = "response_timed_out";

    private static boolean loaded = false;
    private static String locale = null;
    private static Properties properties = new Properties();

    /**
     * Gets the message associated with the specified ID
     *
     * @param id The ID to retrieve
     * @param args The arguments to substitute into the message
     * @return The message associated with the ID
     */
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

    /**
     * Loads the current locale's properties file
     */
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

    /**
     * Retrieves the current locale of the system and loads the localization for it
     *
     * @return The {@code String} representation of the current locale
     */
    public static String getLocale() {

        if (!loaded || locale == null) {

            load();
        }

        return locale;
    }
}
