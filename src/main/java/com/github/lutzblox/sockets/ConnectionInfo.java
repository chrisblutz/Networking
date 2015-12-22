package com.github.lutzblox.sockets;

/**
 * @author Christopher Lutz
 */
public class ConnectionInfo {

    private String ip;
    private boolean encrypted, open, initialized;

    public ConnectionInfo(String ip, boolean encrypted, boolean open, boolean initialized) {

        this.ip = ip;
        this.encrypted = encrypted;
        this.open = open;
        this.initialized = initialized;
    }

    public String getIp(){

        return ip;
    }

    public boolean getEncrypted(){

        return encrypted;
    }

    public boolean getOpen(){

        return open;
    }

    public boolean getInitialized(){

        return initialized;
    }
}
