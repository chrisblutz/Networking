package com.lutz.networking.packets;

import com.lutz.dataparsing.DataType;
import com.lutz.dataparsing.DataTypes;
import com.lutz.engine.util.ExtendedMap;
import com.lutz.networking.exceptions.NetworkException;

public class Packet {

	public static class PacketData {

		private String key;
		private Object value;

		public PacketData(String key, Object value) {

			this.key = key;
			this.value = value;
		}

		public String getKey() {

			return key;
		}

		public Object getValue() {

			return value;
		}
	}

	public static final PacketData SKIP_SENDING = new PacketData("pd-skse-n",
			true);

	private ExtendedMap<Class<?>, String, Object> data = new ExtendedMap<Class<?>, String, Object>();

	private boolean shouldSkipSend = false;

	public Packet() {
	}

	public Packet(String[] keys, Object[] values) {

		if (keys.length == values.length) {

			for (int i = 0; i < keys.length; i++) {

				putData(keys[i], values[i]);
			}

		} else {

			throw new NetworkException(
					"The constructor to Packet requires the String[] and Object[] to have the same lengths!");
		}
	}

	public Packet(PacketData... data) {

		for (PacketData d : data) {

			putData(d.getKey(), d.getValue());
		}
	}

	public void putData(String key, Object value) {

		data.put(value.getClass(), key, value);
	}

	public void putData(PacketData packetData) {

		data.put(packetData.getValue().getClass(), packetData.getKey(),
				packetData.getValue());
	}

	public Object getData(String key) {

		return data.get(key);
	}

	public boolean hasData(String key) {

		return data.containsKey(key);
	}
	
	private void setSkipSend(boolean skip){
		
		this.shouldSkipSend = skip;
	}
	
	public boolean shouldSkipSending(){
		
		return shouldSkipSend || data.containsKey(SKIP_SENDING.getKey());
	}

	@Override
	public String toString() {

		String toWrite = "";

		for (int typeInt = 0; typeInt < data.typeSet().size(); typeInt++) {

			Class<?> type = data.typeSet().toArray(new Class<?>[] {})[typeInt];

			DataType dataType = DataTypes.getDataType(type);

			if (dataType != null) {

				for (int i = 0; i < data.size(type); i++) {

					String key = data.keySet(type).toArray(new String[] {})[i];

					toWrite += dataType.getAbbreviation().toUpperCase()
							.replace("\n", "$(nl);").replace("\r", "$(cr);")
							.replace("|", "$(vl);")
							+ ":"
							+ key.replace("\n", "$(nl);")
									.replace("\r", "$(cr);")
									.replace("|", "$(vl);")
							+ "="
							+ dataType.writeType(data.get(type, key))
									.replace("\n", "$(nl);")
									.replace("\r", "$(cr);")
									.replace("|", "$(vl);");

					if (i < data.keySet(type).size() - 1
							|| (i == data.keySet(type).size() - 1 && typeInt < data
									.typeSet().size() - 1)) {

						toWrite += "|";
					}
				}

			} else {

				throw new NullPointerException("The class '" + type.getName()
						+ "' does not have a DataType registered for it!");
			}
		}

		return toWrite;
	}

	public static Packet getPacketFromString(String toParse) {

		Packet p = new Packet();

		String[] lines = toParse.split("\\|");

		for (int i = 0; i < lines.length; i++) {

			String line = lines[i];
			
			if (line.contains("=")) {

				String[] parts = line.split("=", 2);

				if (parts[0].contains(":")) {

					String value = parts[1];

					String[] declParts = parts[0].split(":", 2);

					DataType type = DataTypes.getDataType(declParts[0]
							.replace("$(nl);", "\n").replace("$(cr);", "\r")
							.replace("$(vl);", "|"));

					if (type != null) {

						Object parsedValue = type
								.readType(value.replace("$(nl);", "\n")
										.replace("$(cr);", "\r")
										.replace("$(vl);", "|"));

						String key = declParts[1].replace("$(nl);", "\n")
								.replace("$(cr);", "\r").replace("$(vl);", "|");

						if (key.equals(SKIP_SENDING.getKey())) {

							p.setSkipSend(true);
							
						} else {

							p.putData(key, parsedValue);
						}

					} else {

						throw new NullPointerException(
								"The data type abbreviation '"
										+ declParts[0].replace("$(nl);", "\n")
												.replace("$(cr);", "\r")
												.replace("$(vl);", "|")
												.toUpperCase()
										+ "' does not have a DataType registered for it.");
					}

				} else {

					throw new NetworkException(
							"The packet information could not be read!");
				}

			} else {

				throw new NetworkException(
						"The packet information could not be read!");
			}
		}

		return p;
	}
}
