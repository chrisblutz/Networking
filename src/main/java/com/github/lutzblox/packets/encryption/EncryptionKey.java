package com.github.lutzblox.packets.encryption;


public class EncryptionKey {

    private static String key = null;

    public static void setKey(String key){

        EncryptionKey.key = key;
    }

    public static String getKey(){

        return key;
    }
}
