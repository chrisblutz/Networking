package com.github.lutzblox.packets;

import com.github.lutzblox.exceptions.Errors;
import com.github.lutzblox.exceptions.NetworkException;
import com.github.lutzblox.packets.datatypes.DataType;
import com.github.lutzblox.packets.datatypes.DataTypes;

import java.util.ArrayList;
import java.util.List;


/**
 * A class that allows the transformation of {@code Strings} into
 * {@code Packets}. This can be configured using a
 * {@code PacketHandlerConfiguration}.
 *
 * @author Christopher Lutz
 */
public class PacketReader {

    private List<Throwable> errors = new ArrayList<Throwable>();
    private PacketHandlerConfiguration config;

    /**
     * Creates a {@code PacketReader} that uses the default
     * {@code PacketHandlerConfiguration}
     */
    public PacketReader() {

        this(PacketHandlerConfiguration.getDefaultConfiguration());
    }

    /**
     * Creates a {@code PacketReader} that uses a custom
     * {@code PacketHandlerConfiguration}
     *
     * @param config The {@code PacketHandlerConfiguration} that should be used
     */
    public PacketReader(PacketHandlerConfiguration config) {

        this.config = config;
    }

    /**
     * Turns a {@code String} into a {@code Packet} following the
     * {@code PacketHandlerConfiguration} used by this {@code PacketReader}
     *
     * @param toParse The {@code String} to turn into a {@code Packet}
     * @return The {@code Packet} form of the {@code String}
     */
    public Packet getPacketFromString(String toParse) {

        Packet p = new Packet();

        String[] lines = toParse.split("\\|");

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];

            if (line.contains("=")) {

                String[] parts = line.split("=", 2);

                if (parts[0].contains(":")) {

                    String value = parts[1];

                    String[] declParts = parts[0].split(":", 2);

                    DataType type = DataTypes.getDataType(declParts[0]
                            .replace("$(nl);", "\n").replace("$(cr);", "\r")
                            .replace("$(vl);", "|"));

                    if (type != null) {

                        Object parsedValue = type
                                .readType(value.replace("$(nl);", "\n")
                                        .replace("$(cr);", "\r")
                                        .replace("$(vl);", "|"));

                        String key = declParts[1].replace("$(nl);", "\n")
                                .replace("$(cr);", "\r").replace("$(vl);", "|");

                        p.putData(key, parsedValue);

                    } else {

                        NullPointerException e = Errors.getMissingDataType("data type abbreviation",
                                declParts[0].replace("$(nl);", "\n").replace("$(cr);", "\r")
                                        .replace("$(vl);", "|").toUpperCase(), new NetworkException(""));

                        if (config.getIgnoreErrors()) {

                            errors.add(e);

                        } else {

                            throw e;
                        }
                    }

                } else {

                    NetworkException e = Errors.getUnreadablePacket(new NetworkException(""));

                    if (config.getIgnoreErrors()) {

                        errors.add(e);

                    } else {

                        throw e;
                    }
                }

            } else {

                NetworkException e = Errors.getUnreadablePacket(new NetworkException(""));

                if (config.getIgnoreErrors()) {

                    errors.add(e);

                } else {

                    throw e;
                }
            }
        }

        return p;
    }

    /**
     * Gets all of the errors thrown during the previous {@code String} to
     * {@code Packet} transformation. This will be empty if {@code ignoreErrors}
     * flag in the {@code PacketHandlerConfiguration} is {@code false}.
     *
     * @return A {@code Throwable[]} containing all errors thrown during the
     * previous transformation
     */
    public Throwable[] getErrors() {

        return errors.toArray(new Throwable[]{});
    }
}
