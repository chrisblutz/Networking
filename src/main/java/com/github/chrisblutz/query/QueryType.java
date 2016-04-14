package com.github.chrisblutz.query;

import com.github.chrisblutz.Client;
import com.github.chrisblutz.Listenable;
import com.github.chrisblutz.Server;
import com.github.chrisblutz.sockets.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class QueryType {

    private static Map<String, QueryType> types = new HashMap<String, QueryType>();

    /**
     * This {@code QueryType} requests the IPs currently connected to the remote {@code Connection}<br>
     * <strong>*NOTE: This {@code QueryType} does not require any parameters</strong>
     */
    public static final QueryType CONNECTED_IPS = createQueryType("net-default:connected_ips", new QueryListener() {

        @Override
        public Object onQuery(Connection connection, Listenable listenable, Map<String, Object> params) {

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
    /**
     * This {@code QueryType} requests the number of current connections to the remote {@code Connection}<br>
     * <strong>*NOTE: This {@code QueryType} does not require any parameters</strong>
     */
    public static final QueryType NUMBER_OF_CURRENT_CONNECTIONS = createQueryType("net-default:num_connected", new QueryListener() {

        @Override
        public Object onQuery(Connection connection, Listenable listenable, Map<String, Object> params) {

            int num = 0;

            if (listenable instanceof Client) {

                num = ((Client) listenable).isOpen() ? 1 : 0;

            } else if (listenable instanceof Server) {

                num = ((Server) listenable).getConnections().size();
            }

            return num;
        }
    });
    /**
     * This {@code QueryType} requests that the encryption key for the remote {@code Connection} be changed<br>
     * <strong>*NOTE: This {@code QueryType} does not require any parameters</strong>
     */
    public static final QueryType RESET_ENCRYPTION_KEY = createQueryType("net-default:reset_encryption_key", new QueryListener() {

        @Override
        public Object onQuery(Connection connection, Listenable listenable, Map<String, Object> params) {

            if (connection.getEncryptionKey() != null) {

                return connection.getEncryptionKey().resetKey();

            } else {

                return false;
            }
        }
    });

    private String id;
    private QueryListener listener;

    private QueryType(String id, QueryListener listener) {

        this.id = id;
        this.listener = listener;
    }

    /**
     * Gets the ID of this {@code QueryType}
     *
     * @return The ID of this {@code QueryType}
     */
    public String getId() {

        return id;
    }

    /**
     * Executes a query on this {@code QueryType}
     *
     * @param connection The {@code Connection} requesting the query
     * @param listenable The {@code Listenable} for the {@code Connection}
     * @param params     The parameters for the {@code Query}
     * @return The result of the query on this {@code QueryType}
     */
    public Object query(Connection connection, Listenable listenable, Map<String, Object> params) {

        return listener == null ? null : listener.onQuery(connection, listenable, params);
    }

    /**
     * Creates a new {@code QueryType} with the specified ID and {@code QueryListener}
     *
     * @param id       The ID to use for this {@code QueryType}
     * @param listener The {@code QueryListener} to use when a {@code Connection} receives a query for this {@code QueryType}
     * @return The {@code QueryType} created with the specified ID and {@code QueryListener}
     */
    public static QueryType createQueryType(String id, QueryListener listener) {

        if (!types.
                containsKey(
                        id)) {

            types.put(id, new QueryType(id, listener));
        }

        return types.get(id);
    }

    /**
     * Gets all of the currently registered {@code QueryTypes}
     *
     * @return A {@code Map} containing IDs as keys for their respective {@code QueryTypes}
     */
    public static Map<String, QueryType> getTypes() {

        return types;
    }

    /**
     * Gets the {@code QueryType} for the specified ID
     *
     * @param id The ID to use to retrieve the {@code QueryType}
     * @return The {@code QueryType} with the specified ID, or {@code null} if no {@code QueryType} exists
     */
    public static QueryType getType(String id) {

        if (getTypes().containsKey(id)) {

            return getTypes().get(id);
        }

        return null;
    }
}
