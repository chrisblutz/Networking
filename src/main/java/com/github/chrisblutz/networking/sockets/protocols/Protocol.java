package com.github.chrisblutz.networking.sockets.protocols;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class Protocol {

    private Map<String, Class<?>> protocols = new HashMap<String, Class<?>>();

    public void add(String id, Class<?> type){

        if(id != null && type != null) {

            protocols.put(id, type);
        }
    }
}
