package com.github.lutzblox.packets;

import java.util.ArrayList;
import java.util.List;

import com.github.lutzblox.packets.datatypes.DataType;
import com.github.lutzblox.packets.datatypes.DataTypes;
import com.github.lutzblox.utils.ExtendedMap;

/**
 * A class that allows the transformation of {@code Packets} into
 * {@code Strings}. This can be configured using a
 * {@code PacketHandlerConfiguration}.
 * 
 * @author Christopher Lutz
 */
public class PacketWriter {

	private List<Throwable> errors = new ArrayList<Throwable>();
	private PacketHandlerConfiguration config;

	/**
	 * Creates a {@code PacketWriter} that uses the default
	 * {@code PacketHandlerConfiguration}
	 */
	public PacketWriter() {

		this(PacketHandlerConfiguration.getDefaultConfiguration());
	}

	/**
	 * Creates a {@code PacketWriter} that uses a custom
	 * {@code PacketHandlerConfiguration}
	 * 
	 * @param config
	 *            The {@code PacketHandlerConfiguration} that should be used
	 */
	public PacketWriter(PacketHandlerConfiguration config) {

		this.config = config;
	}

	/**
	 * Turns a {@code Packet} into a {@code String} following the
	 * {@code PacketHandlerConfiguration} used by this {@code PacketWriter}
	 * 
	 * @param packet
	 *            The {@code Packet} to turn into a {@code String}
	 * @return The {@code String} form of the {@code Packet}
	 */
	public String getPacketAsWriteableString(Packet packet) {

		errors.clear();

		ExtendedMap data = packet.getDataAsMap();

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

				NullPointerException e = new NullPointerException("The class '"
						+ type.getName()
						+ "' does not have a DataType registered for it!");

				if (config.getIgnoreErrors()) {

					errors.add(e);

				} else {

					throw e;
				}
			}
		}

		return toWrite;
	}

	/**
	 * Gets all of the errors thrown during the previous {@code Packet} to
	 * {@code String} transformation. This will be empty if {@code ignoreErrors}
	 * flag in the {@code PacketHandlerConfiguration} is {@code false}.
	 * 
	 * @return A {@code Throwable[]} containing all errors thrown during the
	 *         previous transformation
	 */
	public Throwable[] getErrors() {

		return errors.toArray(new Throwable[] {});
	}
}
