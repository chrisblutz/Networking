package com.github.chrisblutz.listeners.branching;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class BranchRegistry {

    private static Map<String, BranchingServerListener> branches = new HashMap<String, BranchingServerListener>();

    public static void registerBranchId(String id, BranchingServerListener listener){

        branches.put(id, listener);
    }

    public static BranchingServerListener getBranchingListener(String id){

        return branches.get(id);
    }

    public static BranchingServerListener[] getBranchingListeners(){

        return branches.values().toArray(new BranchingServerListener[branches.values().size()]);
    }

    public static String[] getBranchIds(){

        return branches.keySet().toArray(new String[branches.keySet().size()]);
    }

    public static Map<String, BranchingServerListener> getBranchIdsAsMap(){

        return branches;
    }

    public static void removeBranchId(String id){

        branches.remove(id);
    }
}
