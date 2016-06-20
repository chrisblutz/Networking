package com.github.chrisblutz.networking.packets.datatypes.defaults;

import com.github.chrisblutz.networking.packets.datatypes.DataType;


/**
 * A {@code DataType} representing a {@code Long} object
 *
 * @author Christopher Lutz
 */
public class LongType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return Long.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "long";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        try {

            return Long.parseLong(toRead);

        } catch (Exception e) {

            return 0l;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeType(Object toWrite) {

        return toWrite.toString();
    }
}
