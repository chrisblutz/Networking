package com.github.chrisblutz.networking.packets;

/**
 * A class used to configure {@code String} to and from {@code Packet}
 * transformation classes ({@code PacketReader} and {@code PacketWriter})
 *
 * @author Christopher Lutz
 */
public class PacketHandlerConfiguration {

    private boolean ignoreErrors;

    /**
     * Creates a new {@code PacketHandlerConfiguration}
     *
     * @param ignoreErrors Whether or not the reader/writer should ignore errors during
     *                     transformations. If errors are ignored, they will be added to
     *                     a list of {@code Throwables}. These can be retrieved by
     *                     calling the {@code getErrors()} method. If errors are not
     *                     ignored, they will be thrown normally.
     */
    public PacketHandlerConfiguration(boolean ignoreErrors) {

        this.ignoreErrors = ignoreErrors;
    }

    /**
     * Sets whether or not the reader/writer should ignore errors during
     * transformations. If errors are ignored, they will be added to a list of
     * {@code Throwables}. These can be retrieved by calling the
     * {@code getErrors()} method. If errors are not ignored, they will be
     * thrown normally.
     *
     * @param ignoreErrors Whether or not the reader/writer should ignore errors
     */
    public void setIgnoreErrors(boolean ignoreErrors) {

        this.ignoreErrors = ignoreErrors;
    }

    /**
     * Gets whether or not the reader/writer should ignore errors during
     * transformations. If errors are ignored, they will be added to a list of
     * {@code Throwables}. These can be retrieved by calling the
     * {@code getErrors()} method. If errors are not ignored, they will be
     * thrown normally.
     *
     * @return Whether or not the reader/writer will ignore errors
     */
    public boolean getIgnoreErrors() {

        return ignoreErrors;
    }

    /**
     * Gets the default {@code PacketHandlerConfiguration}<br>
     * Default Configurations:<br>
     * &nbsp;&nbsp;&nbsp;- Ignore Errors: {@code true}
     *
     * @return The default {@code PacketHandlerConfiguration}
     */
    public static PacketHandlerConfiguration getDefaultConfiguration() {

        return new PacketHandlerConfiguration(true);
    }
}
