package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;
import com.github.lutzblox.query.Query;
import com.github.lutzblox.query.QueryPacketHandler;


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
