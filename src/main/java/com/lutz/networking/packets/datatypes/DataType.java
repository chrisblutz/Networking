package com.lutz.networking.packets.datatypes;

public abstract class DataType {

	/** Gets the type read/written by this {@code DataType} */
	public abstract Class<?> getTypeClass();

	/**
	 * Gets the abbreviation that will be used to identify the type in a {@code String}
	 */
	public abstract String getAbbreviation();

	/** Gets the requested type object from a string */
	public abstract Object readType(String toRead);

	/**
	 * Gets the string form of the object<br>
	 * <br>
	 * Notes:<br>
	 * - Even though the method receives an {@code Object} as a parameter,
	 * you can safely assume that it's class is or is a subclass of the result
	 * of {@code getTypeClass()}
	 */
	public abstract String writeType(Object toWrite);

	static {
		
		DataTypes.registerDefaults();
	}
}
