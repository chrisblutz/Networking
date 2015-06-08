package com.lutz.networking.databases.saving;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.lutz.networking.exceptions.NetworkException;
import com.lutz.networking.packets.datatypes.DataType;
import com.lutz.networking.packets.datatypes.DataTypes;
import com.lutz.networking.utils.ExtendedMap;

public class FileMethod implements SaveMethod {

	private File file;

	public FileMethod(File file) {

		this.file = file;
	}

	@Override
	public ExtendedMap load() {

		ExtendedMap map = new ExtendedMap();

		if (file.exists()) {

			try {

				Scanner scanner = new Scanner(file);

				List<String> lines = new ArrayList<String>();

				while (scanner.hasNextLine()) {

					lines.add(scanner.nextLine());
				}

				scanner.close();

				for (int i = 0; i < lines.size(); i++) {

					String line = lines.get(i);

					if (line.contains("=")) {

						String[] parts = line.split("=", 2);

						if (parts[0].contains(":")) {

							String value = parts[1];

							String[] declParts = parts[0].split(":", 2);

							DataType type = DataTypes.getDataType(declParts[0]
									.replace("$(nl);", "\n")
									.replace("$(cr);", "\r")
									.replace("$(vl);", "|"));

							if (type != null) {

								Object parsedValue = type.readType(value
										.replace("$(nl);", "\n")
										.replace("$(cr);", "\r")
										.replace("$(vl);", "|"));

								map.put(parsedValue.getClass(),
										declParts[1].replace("$(nl);", "\n")
												.replace("$(cr);", "\r")
												.replace("$(vl);", "|"),
										parsedValue);

							} else {

								throw new NullPointerException(
										"The data type abbreviation '"
												+ declParts[0]
														.replace("$(nl);", "\n")
														.replace("$(cr);", "\r")
														.replace("$(vl);", "|")
														.toUpperCase()
												+ "' does not have a DataType registered for it.");
							}

						} else {

							throw new NetworkException(
									"The persistent data file could not be read due to formatting errors!");
						}

					} else {

						throw new NetworkException(
								"The save file could not be read due to formatting errors!");
					}
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return map;
	}

	@Override
	public void save(ExtendedMap data) {

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

		try {

			PrintStream str = new PrintStream(file);

			str.println(toWrite);

			str.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
