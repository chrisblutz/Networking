package com.github.lutzblox.packets;

import java.util.ArrayList;
import java.util.List;

import com.github.lutzblox.exceptions.NetworkException;
import com.github.lutzblox.packets.datatypes.DataType;
import com.github.lutzblox.packets.datatypes.DataTypes;

public class PacketReader {

	private List<Throwable> errors = new ArrayList<Throwable>();
	private PacketHandlerConfiguration config;

	public PacketReader() {

		this(PacketHandlerConfiguration.getDefaultConfiguration());
	}

	public PacketReader(PacketHandlerConfiguration config) {

		this.config = config;
	}

	public Packet getPacketFromString(String toParse) {

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

						NullPointerException e = new NullPointerException(
								"The data type abbreviation '"
										+ declParts[0].replace("$(nl);", "\n")
												.replace("$(cr);", "\r")
												.replace("$(vl);", "|")
												.toUpperCase()
										+ "' does not have a DataType registered for it.");

						if (config.getIgnoreErrors()) {

							errors.add(e);

						} else {

							throw e;
						}
					}

				} else {

					NetworkException e = new NetworkException(
							"The packet information could not be read!");

					if (config.getIgnoreErrors()) {

						errors.add(e);

					} else {

						throw e;
					}
				}

			} else {

				NetworkException e = new NetworkException(
						"The packet information could not be read!");

				if (config.getIgnoreErrors()) {

					errors.add(e);

				} else {

					throw e;
				}
			}
		}

		return p;
	}

	public Throwable[] getErrors() {

		return errors.toArray(new Throwable[] {});
	}
}
