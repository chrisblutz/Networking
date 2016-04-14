package com.github.chrisblutz.packets.datatypes.defaults;

import com.github.chrisblutz.packets.datatypes.DataType;


/**
 * A {@code DataType} representing a {@code Character} object
 *
 * @author Christopher Lutz
 */
public class CharType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return Character.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "char";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        if (toRead.length() >= 1) {

            return toRead.charAt(0);

        } else {

            return '\0';
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
