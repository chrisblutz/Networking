package com.github.lutzblox.packets.encryption;

import com.github.lutzblox.Listenable;
import com.github.lutzblox.exceptions.Errors;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.packets.PacketReader;
import com.github.lutzblox.properties.Localization;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;


public class EncryptedPacketReader extends PacketReader {

    private Listenable listenable = null;

    public void setListenable(Listenable listenable) {

        this.listenable = listenable;
    }

    public Listenable getListenable() {

        return listenable;
    }

    @Override
    public Packet getPacketFromString(String toParse) {

        if (toParse.startsWith(":ENC:")) {

            toParse = toParse.substring(":ENC:".length());

            try {

                if (EncryptionKey.getKey() == null) {

                    throw new NullPointerException(Localization.getMessage(Localization.ENCRYPTION_KEY_NULL));
                }

                Key aesKey = new SecretKeySpec(EncryptionKey.getKey().getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");


                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                String decrypted = new String(
                        cipher.doFinal(new Base64().decode(toParse.getBytes())))
                        .replace("$(enc);", ":ENC:");

                Packet p = super.getPacketFromString(decrypted);
                p.setEncrypted(true);

                return p;

            } catch (Exception e) {

                Errors.decryptionFailed(listenable, "AES", e);

                return new Packet();
            }

        } else {

            return super.getPacketFromString(toParse);
        }
    }
}
