package com.github.lutzblox.query;

import com.github.lutzblox.Listenable;
import com.github.lutzblox.sockets.Connection;


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
     * @return The result value for the {@code Query}
     */
    Object onQuery(Connection connection, Listenable listenable);
}