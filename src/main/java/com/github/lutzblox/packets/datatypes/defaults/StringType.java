package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;


/**
 * A {@code DataType} representing a {@code String} object
 *
 * @author Christopher Lutz
 */
public class StringType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "str";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        return toRead;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeType(Object toWrite) {

        return toWrite.toString();
    }
}
