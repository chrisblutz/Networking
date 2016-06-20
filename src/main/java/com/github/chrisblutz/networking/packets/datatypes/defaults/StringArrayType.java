package com.github.chrisblutz.networking.packets.datatypes.defaults;

import com.github.chrisblutz.networking.packets.PacketUtils;
import com.github.chrisblutz.networking.packets.datatypes.DataType;

import java.util.ArrayList;


/**
 * A {@code DataType} representing a {@code String[]}
 *
 * @author Christopher Lutz
 */
public class StringArrayType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return String[].class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "strarr";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        ArrayList<String> reading = new ArrayList<String>();

        for (String s : toRead.split(",")) {

            reading.add(s.replace(PacketUtils.COMMA_ESCAPE, ","));
        }

        return reading.toArray(new String[reading.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeType(Object toWrite) {

        String[] writing = ((String[]) toWrite);
        String writable = "";

        for (int i = 0; i < writing.length; i++) {

            String str = writing[i];

            writable += str.replace(",", PacketUtils.COMMA_ESCAPE);

            if (i < writing.length - 1) {

                writable += ",";
            }
        }

        return writable;
    }
}
