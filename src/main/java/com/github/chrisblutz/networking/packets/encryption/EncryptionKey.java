package com.github.chrisblutz.networking.packets.encryption;


import com.github.chrisblutz.networking.query.Query;
import com.github.chrisblutz.networking.query.QueryStatus;
import com.github.chrisblutz.networking.query.QueryType;
import com.github.chrisblutz.networking.sockets.Connection;

import java.util.HashMap;


/**
 * A class representing an encryption key to use for encrypting a {@code Connection}
 */
public class EncryptionKey {

    private String key = null;
    private EncryptionKeyResetListener resetListener = null;

    /**
     * Creates a new {@code EncryptionKey} object using the specified key
     *
     * @param key           The encryption key to use
     * @param resetListener The {@code EncryptionKeyResetListener} to use when a {@code Connection} receives a query to reset the key
     */
    public EncryptionKey(String key, EncryptionKeyResetListener resetListener) {

        this.key = key;
        this.resetListener = resetListener;
    }

    /**
     * Sets the encryption key for this instance
     *
     * @param key           The key to use
     * @param resetListener The {@code EncryptionKeyResetListener} to use when a {@code Connection} receives a query to reset the key
     */
    public void setKey(String key, EncryptionKeyResetListener resetListener) {

        this.key = key;
        this.resetListener = resetListener;
    }

    /**
     * Gets the encryption key for this instance
     *
     * @return The key used by this {@code EncryptionKey}
     */
    public String getKey() {

        return key;
    }

    /**
     * Calls the {@code resetKey()} method of this {@code EncryptionKey}'s {@code EncryptionKeyResetListener}
     *
     * @return Whether or not the key was reset successfully ({@code false} can either mean that the listener's {@code resetKey()} returned false or that this {@code EncryptionKey} did not have a listener to use)
     */
    public boolean resetKey() {

        if (resetListener != null) {

            return resetListener.resetKey();
        }

        return false;
    }

    /**
     * Queries a {@code Connection} to reset its {@code EncryptionKey} (calls the {@code resetKey()} method of the remote {@code EncryptionKey})
     *
     * @param c The {@code Connection} to query
     * @return Whether or not the reset was successful
     */
    public static boolean resetEncryptionKey(Connection c) {

        Query query = c.query("reset-enc", QueryType.RESET_ENCRYPTION_KEY, new HashMap<String, Object>());

        while (query.isWorking()) ;

        if (query.getStatus() == QueryStatus.Status.SUCCESSFUL) {

            Object obj = query.getValue();

            if (obj instanceof Boolean) {

                boolean b = (Boolean) obj;

                if (b && c.getEncryptionKey() != null) {

                    c.getEncryptionKey().resetKey();

                    return true;

                } else {

                    return false;
                }

            } else {

                return false;
            }

        } else {

            return false;
        }
    }
}
