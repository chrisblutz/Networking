package com.github.lutzblox.packets.encryption;

import com.github.lutzblox.Listenable;
import com.github.lutzblox.exceptions.Errors;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.packets.PacketWriter;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;


public class EncryptedPacketWriter extends PacketWriter {

    private Listenable listenable = null;

    public void setListenable(Listenable listenable) {

        this.listenable = listenable;
    }

    public Listenable getListenable() {

        return listenable;
    }

    @Override
    public String getPacketAsWriteableString(Packet packet) {

        try {

            String writeable = super.getPacketAsWriteableString(packet).replace(":ENC:", "$(enc);");

            Key aesKey = new SecretKeySpec(EncryptionKey.getKey().getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(writeable.getBytes());

            return ":ENC:" + new String(new Base64().encode(encrypted));

        } catch (Exception e) {

            Errors.encryptionFailed(listenable, "AES", e);

            return super.getPacketAsWriteableString(new Packet());
        }
    }
}
