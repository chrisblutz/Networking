package com.github.chrisblutz.networking.packets.datatypes.defaults;

import com.github.chrisblutz.networking.packets.datatypes.DataType;


/**
 * A {@code DataType} representing a {@code Double} object
 *
 * @author Christopher Lutz
 */
public class DoubleType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return Double.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "dbl";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        try {

            return Double.parseDouble(toRead);

        } catch (Exception e) {

            return 0d;
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
