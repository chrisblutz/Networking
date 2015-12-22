package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;

import java.util.ArrayList;


/**
 * @author Christopher Lutz
 */
public class StringArrayType extends DataType {
    
    @Override
    public Class<?> getTypeClass() {

        return String[].class;
    }

    @Override
    public String getAbbreviation() {

        return "strarr";
    }

    @Override
    public Object readType(String toRead) {

        ArrayList<String> reading = new ArrayList<String>();

        for(String s : toRead.split(",")){

            reading.add(s.replace("$(cma);", ","));
        }

        return reading.toArray(new String[reading.size()]);
    }

    @Override
    public String writeType(Object toWrite) {

        String[] writing = ((String[]) toWrite);
        String writable = "";

        for (int i = 0; i < writing.length; i++) {

            String str = writing[i];

            writable += str.replace(",", "$(cma);");

            if (i < writing.length - 1) {

                writable += ",";
            }
        }

        return writable;
    }
}
