package com.github.chrisblutz.networking.packets;

import com.github.chrisblutz.networking.packets.datatypes.DataType;
import com.github.chrisblutz.networking.packets.datatypes.DataTypes;
import com.github.chrisblutz.networking.sockets.Connection;

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
     * @param connection The {@code Connection} that sent the request
     * @param toParse    The {@code String} to turn into a {@code Packet}
     * @return The {@code Packet} form of the {@code String}
     */
    public Packet getPacketFromString(Connection connection, String toParse) {

        Packet p = new Packet();

        String[] lines = toParse.split("\\|");

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].replace(PacketUtils.VERTICAL_LINE_ESCAPE, "|");

            DataType type = DataTypes.readType(line);
            Packet.PacketData data = DataTypes.readValue(type, line);

            if(data != null){

                p.putData(data);
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
