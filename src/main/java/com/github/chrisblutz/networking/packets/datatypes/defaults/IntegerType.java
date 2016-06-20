package com.github.chrisblutz.networking.packets.datatypes.defaults;

import com.github.chrisblutz.networking.packets.datatypes.DataType;


/**
 * A {@code DataType} representing a {@code Integer} object
 *
 * @author Christopher Lutz
 */
public class IntegerType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return Integer.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "int";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        try {

            return Integer.parseInt(toRead);

        } catch (Exception e) {

            return 0;
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
