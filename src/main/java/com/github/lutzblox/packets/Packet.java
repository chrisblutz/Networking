package com.github.lutzblox.packets;

import java.util.ArrayList;
import java.util.List;

import com.github.lutzblox.Listenable;
import com.github.lutzblox.exceptions.NetworkException;
import com.github.lutzblox.packets.datatypes.DataType;
import com.github.lutzblox.packets.datatypes.DataTypes;
import com.github.lutzblox.packets.datatypes.wrappers.Null;
import com.github.lutzblox.utils.ExtendedMap;

/**
 * A class representing a group of data pieces to be sent across a
 * {@code Connection}
 * 
 * @author Christopher Lutz
 */
public class Packet {

	/**
	 * A class representing a data piece inside of a {@code Packet}
	 * 
	 * @author Christopher Lutz
	 */
	public static class PacketData {

		private String key;
		private Object value;

		/**
		 * Creates a new {@code PacketData} object with the specified parameters
		 * 
		 * @param key
		 *            The key of the data piece
		 * @param value
		 *            The value of the data piece
		 */
		public PacketData(String key, Object value) {

			this.key = key;
			this.value = value;
		}

		/**
		 * Gets the key of the data piece
		 * 
		 * @return The key of the data piece
		 */
		public String getKey() {

			return key;
		}

		/**
		 * Gets the value of the data piece
		 * 
		 * @return The value of the data piece
		 */
		public Object getValue() {

			return value;
		}
	}

	/**
	 * A {@code PacketData} instance that tells the remote end that this
	 * {@code Packet} is empty and contains no data
	 */
	public static final PacketData EMPTY_PACKET = new PacketData(
			"packet-empty", true);

	private ExtendedMap data = new ExtendedMap();

	private boolean isVital = false;

	/**
	 * Creates an empty {@code Packet}
	 */
	public Packet() {
	}

	/**
	 * Creates a new {@code Packet} containing the specified data pieces
	 * 
	 * @param keys
	 *            The keys of the data pieces
	 * @param values
	 *            The values of the data pieces
	 */
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

	/**
	 * Creates a new {@code Packet} containing the specified data pieces
	 * 
	 * @param data
	 *            The data pieces to add
	 */
	public Packet(PacketData... data) {

		for (PacketData d : data) {

			putData(d.getKey(), d.getValue());
		}
	}

	/**
	 * Adds a data piece to this {@code Packet}
	 * 
	 * @param key
	 *            The key of the data piece
	 * @param value
	 *            The value of the data piece
	 */
	public void putData(String key, Object value) {

		data.put(value != null ? value.getClass() : Null.class, key, value);
	}

	/**
	 * Adds a data piece to this {@code Packet}
	 * 
	 * @param packetData
	 *            The data piece to add
	 */
	public void putData(PacketData packetData) {

		data.put(packetData.getValue().getClass(), packetData.getKey(),
				packetData.getValue());
	}

	/**
	 * Gets the value for the specified key
	 * 
	 * @param key
	 *            The key to check against
	 * @return The value attached to the key (can be {@code null} if the key
	 *         does not exist)
	 */
	public Object getData(String key) {

		return data.get(key);
	}

	/**
	 * Gets all data pieces attached to this {@code Packet}
	 * 
	 * @return A {@code PacketData[]} containing all data pieces attached to
	 *         this {@code Packet}
	 */
	public PacketData[] getData() {

		List<PacketData> packetData = new ArrayList<PacketData>();

		for (Class<?> type : data.typeSet()) {

			for (String key : data.keySet(type)) {

				packetData.add(new PacketData(key, data.get(key)));
			}
		}

		return packetData.toArray(new PacketData[] {});
	}

	/**
	 * Checks if the specified key exists as a data key
	 * 
	 * @param key
	 *            The key to check for
	 * @return Whether or not the key exists
	 */
	public boolean hasData(String key) {

		return data.containsKey(key);
	}

	/**
	 * Clears all data pieces from this {@code Packet}
	 */
	public void clearData() {

		data.clear();
	}

	/**
	 * Sets whether or not this {@code Packet} should be vital. If a vital
	 * packet is dropped by either side of a connection, it will be sent
	 * whenever there is an opening.
	 * 
	 * @param vital
	 *            Whether or not this {@code Packet} should be vital
	 */
	public void setVital(boolean vital) {

		this.isVital = vital;
	}

	/**
	 * Checks whether or not this {@code Packet} is vital. If a vital packet is
	 * dropped by either side of a connection, it will be sent whenever there is
	 * an opening.
	 * 
	 * @return Whether or not this {@code Packet} is vital
	 */
	public boolean isVital() {

		return isVital;
	}

	/**
	 * Checks whether or not this {@code Packet} contains no data pieces
	 * 
	 * @return Whether or not this {@code Packet} contains any data pieces
	 */
	public boolean isEmpty() {

		if (data.isEmpty()) {

			return true;

		} else if (data.size() == 1) {

			if (data.containsKey(Packet.EMPTY_PACKET.getKey())) {

				if (data.get(Packet.EMPTY_PACKET.getKey()) instanceof Boolean
						&& data.get(Packet.EMPTY_PACKET.getKey()).equals(true)) {

					return true;
				}
			}
		}

		return false;
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

	/**
	 * Parses a {@code Packet} from a {@code String}
	 * 
	 * @param toParse
	 *            The {@code String} to parse
	 * @param l
	 *            The {@code Listenable} that received this {@code Packet}
	 * @return The parsed {@code Packet}
	 */
	public static Packet getPacketFromString(String toParse, Listenable l) {

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

						p.putData(key, parsedValue);

					} else {

						NullPointerException ex = new NullPointerException(
								"The data type abbreviation '"
										+ declParts[0].replace("$(nl);", "\n")
												.replace("$(cr);", "\r")
												.replace("$(vl);", "|")
												.toUpperCase()
										+ "' does not have a DataType registered for it.");

						l.report(ex);
					}

				} else {

					NetworkException ex = new NetworkException(
							"The packet information could not be read!");

					l.report(ex);
				}

			} else {

				NetworkException ex = new NetworkException(
						"The packet information could not be read!");

				l.report(ex);
			}
		}

		return p;
	}
}
