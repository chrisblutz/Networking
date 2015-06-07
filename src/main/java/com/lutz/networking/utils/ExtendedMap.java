package com.lutz.networking.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ExtendedMap<T, K, O> {

	private Map<T, Map<K, O>> data = new HashMap<T, Map<K, O>>();

	public void put(T type, K key, O object) {

		if (!data.containsKey(type)) {

			data.put(type, new HashMap<K, O>());
		}

		checkDuplicateKey(key);

		data.get(type).put(key, object);
	}

	private void checkDuplicateKey(K key) {

		for (T type : typeSet()) {

			if (getType(type).containsKey(key)) {

				getType(type).remove(key);
			}
		}
	}

	public void putAll(ExtendedMap<T, K, O> m) {

		for (T type : m.typeSet()) {

			for (K key : m.keySet(type)) {

				put(type, key, m.get(type, key));
			}
		}
	}

	public void putAll(Map<T, Map<K, O>> m) {

		for (T type : m.keySet()) {

			for (K key : m.get(type).keySet()) {

				put(type, key, m.get(type).get(key));
			}
		}
	}

	public boolean containsType(T type) {

		return data.containsKey(type);
	}

	public boolean containsKey(T type, K key) {

		if (data.containsKey(type)) {

			return data.get(type).containsKey(key);
		}

		return false;
	}

	public boolean containsKey(K key) {

		for (T type : typeSet()) {

			if (getType(type).containsKey(key)) {

				return true;
			}
		}

		return false;
	}

	public void removeType(T type) {

		if (data.containsKey(type)) {

			data.remove(type);
		}
	}

	public void removeKeyInType(T type, K key) {

		if (data.containsKey(type)) {

			if (data.get(type).containsKey(key)) {

				data.get(type).remove(key);
			}
		}
	}

	public void clear() {

		data.clear();
	}

	public boolean isEmpty() {

		return data.isEmpty();
	}

	public boolean isEmpty(T type) {

		if (data.containsKey(type)) {

			return data.get(type).isEmpty();
		}

		return false;
	}

	public int size() {

		return data.size();
	}

	public int size(T type) {

		if (data.containsKey(type)) {

			return data.get(type).size();
		}

		return 0;
	}

	public void clear(T type) {

		if (data.containsKey(type)) {

			data.get(type).clear();
		}
	}

	public Map<K, O> getType(T type) {

		if (data.containsKey(type)) {

			return data.get(type);
		}

		return null;
	}

	public O get(T type, K key) {

		if (data.containsKey(type)) {

			if (data.get(type).containsKey(key)) {

				return data.get(type).get(key);
			}
		}

		return null;
	}

	public O get(K key) {

		for (T type : typeSet()) {

			if (getType(type).containsKey(key)) {

				return getType(type).get(key);
			}
		}

		return null;
	}

	public Set<T> typeSet() {

		return data.keySet();
	}

	public Set<K> keySet(T type) {

		if (data.containsKey(type)) {

			return data.get(type).keySet();
		}

		return null;
	}

	public Set<Entry<T, Map<K, O>>> entrySet() {

		return data.entrySet();
	}

	public Set<Entry<K, O>> entrySet(T type) {

		if (data.containsKey(type)) {

			return data.get(type).entrySet();
		}

		return null;
	}
}
