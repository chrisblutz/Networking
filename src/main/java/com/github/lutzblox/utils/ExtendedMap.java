package com.github.lutzblox.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.lutzblox.packets.datatypes.wrappers.Null;

public class ExtendedMap {

	private Map<Class<?>, Map<String, Object>> data = new ConcurrentHashMap<Class<?>, Map<String, Object>>();

	public void put(Class<?> type, String key, Object object) {

		if (object == null) {

			object = Null.NULL;
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

	public void putAll(ExtendedMap m) {

		for (Class<?> type : m.typeSet()) {

			for (String key : m.keySet(type)) {

				put(type, key, m.get(type, key));
			}
		}
	}

	public void putAll(Map<Class<?>, Map<String, Object>> m) {

		for (Class<?> type : m.keySet()) {

			for (String key : m.get(type).keySet()) {

				put(type, key, m.get(type).get(key));
			}
		}
	}

	public boolean containsType(Class<?> type) {

		return data.containsKey(type);
	}

	public boolean containsKey(Class<?> type, String key) {

		if (data.containsKey(type)) {

			return data.get(type).containsKey(key);
		}

		return false;
	}

	public boolean containsKey(String key) {

		for (Class<?> type : typeSet()) {

			if (getType(type).containsKey(key)) {

				return true;
			}
		}

		return false;
	}

	public void removeType(Class<?> type) {

		if (data.containsKey(type)) {

			data.remove(type);
		}
	}

	public void removeKeyInType(Class<?> type, String key) {

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

	public boolean isEmpty(Class<?> type) {

		if (data.containsKey(type)) {

			return data.get(type).isEmpty();
		}

		return false;
	}

	public int size() {

		return data.size();
	}

	public int size(Class<?> type) {

		if (data.containsKey(type)) {

			return data.get(type).size();
		}

		return 0;
	}

	public void clear(Class<?> type) {

		if (data.containsKey(type)) {

			data.get(type).clear();
		}
	}

	public Map<String, Object> getType(Class<?> type) {

		if (data.containsKey(type)) {

			return data.get(type);
		}

		return null;
	}

	public Object get(Class<?> type, String key) {

		if (data.containsKey(type)) {

			if (data.get(type).containsKey(key)) {

				Object o = data.get(type).get(key);

				if (o == Null.NULL) {

					return null;

				} else {

					return o;
				}
			}
		}

		return null;
	}

	public Object get(String key) {

		for (Class<?> type : typeSet()) {

			if (getType(type).containsKey(key)) {

				Object o = getType(type).get(key);

				if (o == Null.NULL) {

					return null;

				} else {

					return o;
				}
			}
		}

		return null;
	}

	public Set<Class<?>> typeSet() {

		return data.keySet();
	}

	public Set<String> keySet(Class<?> type) {

		if (data.containsKey(type)) {

			return data.get(type).keySet();
		}

		return null;
	}

	public Set<Entry<Class<?>, Map<String, Object>>> entrySet() {

		return data.entrySet();
	}

	public Set<Entry<String, Object>> entrySet(Class<?> type) {

		if (data.containsKey(type)) {

			return data.get(type).entrySet();
		}

		return null;
	}
}
