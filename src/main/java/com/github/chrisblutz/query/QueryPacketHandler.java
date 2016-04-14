package com.github.chrisblutz.query;

import com.github.chrisblutz.exceptions.Errors;
import com.github.chrisblutz.exceptions.NetworkException;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.packets.datatypes.DataType;
import com.github.chrisblutz.packets.datatypes.DataTypes;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class QueryPacketHandler {

    public static String write(Query query) {

        String queryString = query.getId().replace(",", "$(cma);") + "," + query.getType().getId().replace(",", "$(cma);");

        Map<String, Object> params = query.getParameters();

        for (int i = 0; i < params.keySet().size(); i++) {

            String key = params.keySet().toArray(new String[params.keySet().size()])[i];
            Object value = params.get(key);

            DataType type = DataTypes.getDataType(value.getClass());

            if (type != null) {

                queryString += "," + DataTypes.writeType(type, key, value);

            } else {

                Errors.missingDataType("class", value.getClass().getName(), new NetworkException(""));
            }
        }

        return queryString;
    }

    public static Query read(String queryStr) {

        Map<String, Object> params = new HashMap<String, Object>();

        String[] lines = queryStr.split(",");

        if (lines.length >= 2) {

            String queryId = lines[0].replace("$(cma);", ",");
            String typeId = lines[1].replace("$(cma);", ",");
            QueryType type = QueryType.getType(typeId);

            if (!queryId.contentEquals("") && QueryType.getType(typeId) != null) {

                for (int i = 2; i < lines.length; i++) {

                    String line = lines[i].replace("$(cma);", ",");

                    DataType dataType = DataTypes.readType(line);
                    Packet.PacketData data = DataTypes.readValue(dataType, line);

                    if (data != null) {

                        params.put(data.getKey(), data.getValue());
                    }
                }

                return new Query(queryId, type, params);

            } else {

                Errors.malformedQuery(queryId, (type != null ? type.getId() : "null"), new NetworkException(""));
            }

        } else {

            Errors.malformedQuery("null", "null", new NetworkException(""));
        }

        return null;
    }
}
