package com.github.chrisblutz.networking.query;

import com.github.chrisblutz.networking.Listenable;
import com.github.chrisblutz.networking.sockets.Connection;

import java.util.Map;


/**
 * A listener interface for executing a {@code Query}
 *
 * @author Christopher Lutz
 */
public interface QueryListener {

    /**
     * Executes a {@code Query} and returns the result
     *
     * @param connection The {@code Connection} that requested the {@code Query}
     * @param listenable The {@code Listenable} for the {@code Connection}
     * @param params     The parameters for the {@code Query}
     * @return The result value for the {@code Query}
     */
    Object onQuery(Connection connection, Listenable listenable, Map<String, Object> params);
}
