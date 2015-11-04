package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;


/**
 * A {@code DataType} representing a {@code Short} object
 *
 * @author Christopher Lutz
 */
public class ShortType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return Short.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "shrt";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        try {

            return Short.parseShort(toRead);

        } catch (Exception e) {

            return (short) 0;
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
