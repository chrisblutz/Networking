package com.github.chrisblutz.networking.packets.datatypes.defaults;

import com.github.chrisblutz.networking.packets.datatypes.DataType;
import com.github.chrisblutz.networking.packets.datatypes.wrappers.Null;


/**
 * A {@code DataType} representing a {@code null} object
 *
 * @author Christopher Lutz
 */
public class NullType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return Null.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "NUL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeType(Object toWrite) {

        return "null";
    }
}
