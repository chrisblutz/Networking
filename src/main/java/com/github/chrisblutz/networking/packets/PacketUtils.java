package com.github.chrisblutz.networking.packets;

/**
 * @author Christopher Lutz
 */
public class PacketUtils {

    public static final String VERTICAL_LINE_ESCAPE = "$(vl);";
    public static final String NEW_LINE_ESCAPE = "$(nl);";
    public static final String CARRIAGE_RETURN_ESCAPE = "$(cr);";
    public static final String COMMA_ESCAPE = "$(cma);";
    public static final String ENCRYPTED_DATA_ESCAPE = "$(enc);";

    public static final String ENCRYPTED_DATA_PREFIX = ":ENC:";
}
