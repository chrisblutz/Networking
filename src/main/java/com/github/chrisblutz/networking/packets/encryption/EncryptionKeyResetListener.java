package com.github.chrisblutz.networking.packets.encryption;

/**
 * A listener interface for resetting an {@code EncryptionKey}'s key
 *
 * @author Christopher Lutz
 */
public interface EncryptionKeyResetListener {

    /**
     * Called when a {@code EncryptionKey} resets its key
     *
     * @return Whether or not the reset was successful
     */
    boolean resetKey();
}
