package com.github.chrisblutz.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A map class that encompasses a map within a map
 *
 * @author Christopher Lutz
 */
public class ExtendedMap {

    private Map<Class<?>, Map<String, Object>> data = new ConcurrentHashMap<Class<?>, Map<String, Object>>();

    /**
     * Puts a value into the map
     *
     * @param type   The type of the object being added
     * @param key    The key of the object being added
     * @param object The value of the object being added
     */
    public void put(Class<?> type, String key, Object object) {

        if (object == null) {

            return;
        }

        if (!data.containsKey(type)) {

            data.put(type, new ConcurrentHashMap<String, Object>());
        }

        checkDuplicateKey(key);

        data.get(type).put(key, object);
    }

    private void checkDuplicateKey(String key) {

        for (Class<?> type : typeSet()) {

            if (getType(type).containsKey(key)) {

                getType(type).remove(key);
            }
        }
    }

    /**
     * Puts an entire {@code ExtendedMap} into this one
     *
     * @param m The {@code ExtendedMap} to add
     */
    public void putAll(ExtendedMap m) {

        for (Class<?> type : m.typeSet()) {

            for (String key : m.keySet(type)) {

                put(type, key, m.get(type, key));
            }
        }
    }

    /**
     * Puts an entire {@code Map<Class<?>, Map<String, Object>>} object into
     * this one
     *
     * @param m The map to add
     */
    public void putAll(Map<Class<?>, Map<String, Object>> m) {

        for (Class<?> type : m.keySet()) {

            for (String key : m.get(type).keySet()) {

                put(type, key, m.get(type).get(key));
            }
        }
    }

    /**
     * Checks if this map contains the specified type
     *
     * @param type The type to check for
     * @return Whether or not this {@code ExtendedMap} contains the specified
     * type
     */
    public boolean containsType(Class<?> type) {

        return data.containsKey(type);
    }

    /**
     * Checks if this map contains the specified key in the specified type
     *
     * @param type The type to check in
     * @param key  The key to check for
     * @return Whether or not the type has the specified key
     */
    public boolean containsKey(Class<?> type, String key) {

        if (data.containsKey(type)) {

            return data.get(type).containsKey(key);
        }

        return false;
    }

    /**
     * Checks if this map contains the specified key over all types
     *
     * @param key The key to check for
     * @return Whether or not the key exists within the map
     */
    public boolean containsKey(String key) {

        for (Class<?> type : typeSet()) {

            if (getType(type).containsKey(key)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Removes a type from this map
     *
     * @param type The type to remove
     */
    public void removeType(Class<?> type) {

        if (data.containsKey(type)) {

            data.remove(type);
        }
    }

    /**
     * Removes a key/value combination from within the specified type
     *
     * @param type The type to remove the combination from
     * @param key  The key to remove
     */
    public void removeKeyInType(Class<?> type, String key) {

        if (data.containsKey(type)) {

            if (data.get(type).containsKey(key)) {

                data.get(type).remove(key);
            }
        }
    }

    /**
     * Removes a key/value combination from the map
     *
     * @param key The key to remove
     */
    public void removeKey(String key) {

        for (Class<?> type : typeSet()) {

            if (containsKey(type, key)) {

                removeKeyInType(type, key);
            }
        }
    }

    /**
     * Clears the map of all data
     */
    public void clear() {

        data.clear();
    }

    /**
     * Checks if the map is empty
     *
     * @return Whether or not the map is empty
     */
    public boolean isEmpty() {

        return data.isEmpty();
    }

    /**
     * Checks whether a certain type has any values mapped within it
     *
     * @param type The type to check in
     * @return Whether or not the specified type contains any mapped values
     */
    public boolean isEmpty(Class<?> type) {

        if (data.containsKey(type)) {

            return data.get(type).isEmpty();
        }

        return false;
    }

    /**
     * Gets the size of the map
     *
     * @return The size of the entire map
     */
    public int size() {

        return data.size();
    }

    /**
     * Gets the number of values mapped inside a certain type
     *
     * @param type The type to check
     * @return The number of values mapped inside the type
     */
    public int size(Class<?> type) {

        if (data.containsKey(type)) {

            return data.get(type).size();
        }

        return 0;
    }

    /**
     * Clears all mapped values from a certain type
     *
     * @param type The type to clear
     */
    public void clear(Class<?> type) {

        if (data.containsKey(type)) {

            data.get(type).clear();
        }
    }

    /**
     * Gets all mapped values within a certain type
     *
     * @param type The type to return
     * @return All mapped values within the type
     */
    public Map<String, Object> getType(Class<?> type) {

        if (data.containsKey(type)) {

            return data.get(type);
        }

        return null;
    }

    /**
     * Gets a value mapped to the specified key from the specified type
     *
     * @param type The type to check in
     * @param key  The key to check for
     * @return The value mapped to the key
     */
    public Object get(Class<?> type, String key) {

        if (data.containsKey(type)) {

            if (data.get(type).containsKey(key)) {

                Object o = data.get(type).get(key);

                return o;
            }
        }

        return null;
    }

    /**
     * Gets the value mapped to the specified key from anywhere in the map
     *
     * @param key The key to check for
     * @return The value mapped to the key
     */
    public Object get(String key) {

        for (Class<?> type : typeSet()) {

            if (getType(type).containsKey(key)) {

                Object o = getType(type).get(key);

                return o;
            }
        }

        return null;
    }

    /**
     * Gets all types used in this {@code ExtendedMap}
     *
     * @return A {@code Set} containing all types used
     */
    public Set<Class<?>> typeSet() {

        return data.keySet();
    }

    /**
     * Gets all keys used in this {@code ExtendedMap}
     *
     * @param type The type to get keys for
     * @return A {@code Set} containing all keys in the type
     */
    public Set<String> keySet(Class<?> type) {

        if (data.containsKey(type)) {

            return data.get(type).keySet();
        }

        return null;
    }

    /**
     * Gets all entries in this map
     *
     * @return A {@code Set} containing all entries from this map
     */
    public Set<Entry<Class<?>, Map<String, Object>>> entrySet() {

        return data.entrySet();
    }

    /**
     * Gets all entries from the specified type
     *
     * @param type The type to get entries for
     * @return A {@code Set} containing all entries from the specified type
     */
    public Set<Entry<String, Object>> entrySet(Class<?> type) {

        if (data.containsKey(type)) {

            return data.get(type).entrySet();
        }

        return null;
    }
}
