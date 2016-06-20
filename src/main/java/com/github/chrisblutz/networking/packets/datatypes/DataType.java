package com.github.chrisblutz.networking.packets.datatypes;

/**
 * A class used to read and write objects from {@code Strings}
 *
 * @author Christopher Lutz
 */
public abstract class DataType {

    /**
     * Gets the type read/written by this {@code DataType}
     *
     * @return The class represented by this {@code DataType}
     */
    public abstract Class<?> getTypeClass();

    /**
     * Gets the abbreviation that will be used to identify the type in a
     * {@code String}
     *
     * @return The string abbreviation for this {@code DataType}
     */
    public abstract String getAbbreviation();

    /**
     * Gets the requested type object from a string
     *
     * @param toRead The {@code String} to turn into an object of this {@code DataType}
     * @return The {@code Object} parsed by this {@code DataType}
     */
    public abstract Object readType(String toRead);

    /**
     * Gets the string form of the object<br>
     * <br>
     * Notes:<br>
     * - Even though the method receives an {@code Object} as a parameter, you
     * can safely assume that its class is or is a subclass of the result of
     * {@code getTypeClass()}
     *
     * @param toWrite The {@code Object} to turn into a {@code String}
     * @return The {@code String} form of the given {@code Object}
     */
    public abstract String writeType(Object toWrite);

    static {

        DataTypes.registerDefaults();
    }
}
