package com.github.chrisblutz.packets.datatypes.defaults;

import com.github.chrisblutz.packets.datatypes.DataType;
import com.github.chrisblutz.query.Query;
import com.github.chrisblutz.query.QueryPacketHandler;


/**
 * A {@code DataType} representing a {@code QueryRequest} object
 *
 * @author Christopher Lutz
 */
public class QueryDataType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return Query.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "qry";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        return QueryPacketHandler.read(toRead);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeType(Object toWrite) {

        Query q = (Query) toWrite;

        return QueryPacketHandler.write(q);
    }
}
