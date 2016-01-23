package com.github.lutzblox.query;

import com.github.lutzblox.Client;
import com.github.lutzblox.Listenable;
import com.github.lutzblox.Server;
import com.github.lutzblox.sockets.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class QueryType {

    private static Map<String, QueryType> types = new HashMap<String, QueryType>();

    public static final QueryType CONNECTED_IPS = createQueryType("net-default:connected_ips", new QueryListener() {

        @Override
        public Object onQuery(Listenable listenable) {

            ArrayList<String> ips = new ArrayList<String>();

            if (listenable instanceof Server) {

                Server server = (Server) listenable;

                for (Connection c : server.getConnections()) {

                    if (c != null && c.getInitialized()) {

                        ips.add(c.getIp());
                    }
                }

            } else if (listenable instanceof Client) {

                Client client = (Client) listenable;

                Connection c = client.getConnection();

                if (c != null && c.getInitialized()) {

                    ips.add(c.getIp());
                }
            }

            return ips.toArray(new String[ips.size()]);
        }
    });
    public static final QueryType NUMBER_OF_CURRENT_CONNECTIONS = createQueryType("net-default:num_connected", new QueryListener() {

        @Override
        public Object onQuery(Listenable listenable) {

            int num = 0;

            if(listenable instanceof Client){

                num = ((Client) listenable).isOpen() ? 1 : 0;

            }else if(listenable instanceof Server){

                num = ((Server) listenable).getConnections().size();
            }

            return num;
        }
    });

    private String id;
    private QueryListener listener;

    private QueryType(String id, QueryListener listener) {

        this.id = id;
        this.listener = listener;
    }

    public String getId() {

        return id;
    }

    public Object query(Listenable listenable) {

        return listener == null ? null : listener.onQuery(listenable);
    }

    public static QueryType createQueryType(String id, QueryListener listener) {

        if (!types.
                containsKey(
                        id)) {

            types.put(id, new QueryType(id, listener));
        }

        return types.get(id);
    }

    public static Map<String, QueryType> getTypes() {

        return types;
    }

    public static QueryType getType(String name){

        if(getTypes().containsKey(name)){

            return getTypes().get(name);
        }

        return null;
    }
}
