package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;
import com.github.lutzblox.query.QueryRequest;
import com.github.lutzblox.query.QueryType;


/**
 * A {@code DataType} representing a {@code QueryRequest} object
 *
 * @author Christopher Lutz
 */
public class QueryRequestType extends DataType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTypeClass() {

        return QueryRequest.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviation() {

        return "qrq";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readType(String toRead) {

        String[] parts = toRead.split(",", 2);

        if (parts.length == 2) {

            String id = parts[0].replace("$(cma);", ",");
            String reqType = parts[1].replace("$(cma);", ",");
            QueryType type = QueryType.getType(reqType);

            if(type != null){

                return new QueryRequest(id, type);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeType(Object toWrite) {

        QueryRequest q = (QueryRequest) toWrite;

        return q.getId().replace(",", "$(cma);")+","+q.getType().getId().replace(",", "$(cma);");
    }
}
