package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;
import com.github.lutzblox.query.QueryRequest;
import com.github.lutzblox.query.QueryType;


/**
 * @author Christopher Lutz
 */
public class QueryRequestType extends DataType {
    
    @Override
    public Class<?> getTypeClass() {

        return QueryRequest.class;
    }

    @Override
    public String getAbbreviation() {

        return "qrq";
    }

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

    @Override
    public String writeType(Object toWrite) {

        QueryRequest q = (QueryRequest) toWrite;

        return q.getId().replace(",", "$(cma);")+","+q.getType().getId().replace(",", "$(cma);");
    }
}
