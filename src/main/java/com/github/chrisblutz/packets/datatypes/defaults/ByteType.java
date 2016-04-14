package com.github.chrisblutz.packets.datatypes.defaults;

import com.github.chrisblutz.packets.datatypes.DataType;


/**
 * A {@code DataType} representing a {@code Byte} object
 *
 * @author Christopher Lutz
 */
public class ByteType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return Byte.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "byte";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        try {

            return Byte.parseByte(toRead);

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
