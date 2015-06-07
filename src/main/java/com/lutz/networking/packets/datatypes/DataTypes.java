package com.lutz.networking.packets.datatypes;

import java.util.HashMap;
import java.util.Map;

import com.lutz.networking.packets.datatypes.defaults.BooleanType;
import com.lutz.networking.packets.datatypes.defaults.ByteType;
import com.lutz.networking.packets.datatypes.defaults.CharType;
import com.lutz.networking.packets.datatypes.defaults.DoubleType;
import com.lutz.networking.packets.datatypes.defaults.FloatType;
import com.lutz.networking.packets.datatypes.defaults.IntegerType;
import com.lutz.networking.packets.datatypes.defaults.LongType;
import com.lutz.networking.packets.datatypes.defaults.ShortType;
import com.lutz.networking.packets.datatypes.defaults.StringType;

public class DataTypes {
	
	private static Map<Class<?>, DataType> dataTypes = new HashMap<Class<?>, DataType>();

	/**
	 * Registers a data type to the DataType registry
	 * 
	 * @param type
	 *            The data type to register
	 */
	public static void registerDataType(DataType type) {

		dataTypes.put(type.getTypeClass(), type);
	}

	/**
	 * Gets all registered data types
	 * 
	 * @return An array containing all registered data types
	 */
	public static DataType[] getDataTypes() {

		return dataTypes.values().toArray(new DataType[] {});
	}

	/**
	 * Returns the data type associated with the given class (null if there
	 * isn't one)
	 * 
	 * @param c
	 *            The class to check against
	 * @return The DataType registered for the class (can be null if there is no
	 *         registered data type for the class)
	 */
	public static DataType getDataType(Class<?> c) {

		if (dataTypes.containsKey(c)) {

			return dataTypes.get(c);
		}

		return null;
	}

	/**
	 * Gets the data type for the given abbreviation (null if there isn't one)
	 * 
	 * @param abbrev
	 *            The abbreviation to check against
	 * @return The DataType registered for the abbreviation (can be null if
	 *         there is no registered data type for the abbreviation)
	 */
	public static DataType getDataType(String abbrev) {

		for (DataType type : dataTypes.values()) {

			if (type.getAbbreviation().equalsIgnoreCase(abbrev)) {

				return type;
			}
		}

		return null;
	}
	
	/** Register default data types */
	public static void registerDefaults(){

		DataTypes.registerDataType(new ShortType());
		DataTypes.registerDataType(new IntegerType());
		DataTypes.registerDataType(new LongType());
		DataTypes.registerDataType(new DoubleType());
		DataTypes.registerDataType(new FloatType());
		DataTypes.registerDataType(new StringType());
		DataTypes.registerDataType(new CharType());
		DataTypes.registerDataType(new BooleanType());
		DataTypes.registerDataType(new ByteType());
	}

	static {

		registerDefaults();
	}
}