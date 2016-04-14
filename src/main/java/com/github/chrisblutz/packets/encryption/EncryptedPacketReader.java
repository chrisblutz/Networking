package com.github.chrisblutz.packets.encryption;

import com.github.chrisblutz.Listenable;
import com.github.chrisblutz.packets.Packet;
import com.github.chrisblutz.packets.PacketHandlerConfiguration;
import com.github.chrisblutz.packets.PacketReader;
import com.github.chrisblutz.properties.Localization;
import com.github.chrisblutz.sockets.Connection;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;


/**
 * A class that allows the transformation of encrypted {@code Strings} into
 * {@code Packets}. This can be configured using a
 * {@code PacketHandlerConfiguration}.
 *
 * @author Christopher Lutz
 */
public class EncryptedPacketReader extends PacketReader {

    private Listenable listenable = null;

    /**
     * Creates an {@code EncryptedPacketReader} that uses the default
     * {@code PacketHandlerConfiguration}
     */
    public EncryptedPacketReader() {

        super();
    }

    /**
     * Creates an {@code EncryptedPacketReader} that uses a custom
     * {@code PacketHandlerConfiguration}
     *
     * @param config The {@code PacketHandlerConfiguration} that should be used
     */
    public EncryptedPacketReader(PacketHandlerConfiguration config) {

        super(config);
    }

    /**
     * Sets the {@code Listenable} used by this {@code EncryptedPacketReader}
     *
     * @param listenable The {@code Listenable} to be used by this {@code EncryptedPacketReader}
     */
    public void setListenable(Listenable listenable) {

        this.listenable = listenable;
    }

    /**
     * Gets the {@code Listenable} used by this {@code EncryptedPacketReader}
     *
     * @return The {@code Listenable} used by this {@code EncryptedPacketReader}
     */
    public Listenable getListenable() {

        return listenable;
    }

    /**
     * Turns a {@code String} into a {@code Packet} after decrypting it following the
     * {@code PacketHandlerConfiguration} used by this {@code EncryptedPacketReader}
     *
     * @param connection The {@code Connection} that sent the request
     * @param toParse    The encrypted {@code String} to turn into a {@code Packet}
     * @return The {@code Packet} form of the {@code String}
     */
    @Override
    public Packet getPacketFromString(Connection connection, String toParse) {

        if (toParse.startsWith(":ENC:")) {

            toParse = toParse.substring(":ENC:".length());

            try {

                EncryptionKey encryptionKey = connection.getEncryptionKey();

                if (encryptionKey == null || encryptionKey.getKey() == null) {

                    throw new NullPointerException(Localization.getMessage(Localization.ENCRYPTION_KEY_NULL));
                }

                Key aesKey = new SecretKeySpec(encryptionKey.getKey().getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");


                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                String decrypted = new String(
                        cipher.doFinal(new Base64().decode(toParse.getBytes())))
                        .replace("$(enc);", ":ENC:");

                Packet p = super.getPacketFromString(connection, decrypted);

                return p;

            } catch (Exception e) {

                //Errors.decryptionFailed(listenable, "AES", null);
                e.printStackTrace();

                return new Packet();
            }

        } else {

            return super.getPacketFromString(connection, toParse);
        }
    }
}
