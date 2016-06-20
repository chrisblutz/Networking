package com.github.chrisblutz.networking.packets.encryption;

import com.github.chrisblutz.networking.Listenable;
import com.github.chrisblutz.networking.exceptions.Errors;
import com.github.chrisblutz.networking.packets.Packet;
import com.github.chrisblutz.networking.packets.PacketHandlerConfiguration;
import com.github.chrisblutz.networking.packets.PacketUtils;
import com.github.chrisblutz.networking.packets.PacketWriter;
import com.github.chrisblutz.networking.properties.Localization;
import com.github.chrisblutz.networking.sockets.Connection;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;


/**
 * A class that allows the transformation of {@code Packets} into
 * encrypted {@code Strings}. This can be configured using a
 * {@code PacketHandlerConfiguration}.
 *
 * @author Christopher Lutz
 */
public class EncryptedPacketWriter extends PacketWriter {

    private Listenable listenable = null;

    /**
     * Creates an {@code EncryptedPacketWriter} that uses the default
     * {@code PacketHandlerConfiguration}
     */
    public EncryptedPacketWriter() {

        super();
    }

    /**
     * Creates an {@code EncryptedPacketWriter} that uses a custom
     * {@code PacketHandlerConfiguration}
     *
     * @param config The {@code PacketHandlerConfiguration} that should be used
     */
    public EncryptedPacketWriter(PacketHandlerConfiguration config) {

        super(config);
    }

    /**
     * Sets the {@code Listenable} used by this {@code EncryptedPacketWriter}
     *
     * @param listenable The {@code Listenable} to be used by this {@code EncryptedPacketWriter}
     */
    public void setListenable(Listenable listenable) {

        this.listenable = listenable;
    }

    /**
     * Gets the {@code Listenable} used by this {@code EncryptedPacketWriter}
     *
     * @return The {@code Listenable} used by this {@code EncryptedPacketWriter}
     */
    public Listenable getListenable() {

        return listenable;
    }

    /**
     * Turns a {@code Packet} into an encrypted {@code String} following the
     * {@code PacketHandlerConfiguration} used by this {@code EncryptedPacketWriter}
     *
     * @param connection The {@code Connection} that sent the request
     * @param packet     The {@code Packet} to turn into an encrypted {@code String}
     * @return The encrypted {@code String} form of the {@code Packet}
     */
    @Override
    public String getPacketAsWriteableString(Connection connection, Packet packet) {

        try {

            EncryptionKey encryptionKey = connection.getEncryptionKey();

            if (encryptionKey.getKey() == null) {

                throw new NullPointerException(Localization.getMessage(Localization.ENCRYPTION_KEY_NULL));
            }

            String writeable = super.getPacketAsWriteableString(connection, packet).replace(PacketUtils.ENCRYPTED_DATA_PREFIX, PacketUtils.ENCRYPTED_DATA_ESCAPE);

            Key aesKey = new SecretKeySpec(encryptionKey.getKey().getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(writeable.getBytes());

            return PacketUtils.ENCRYPTED_DATA_PREFIX + new String(new Base64().encode(encrypted));

        } catch (Exception e) {

            Errors.encryptionFailed(listenable, "AES", e);

            return super.getPacketAsWriteableString(connection, new Packet());
        }
    }
}
