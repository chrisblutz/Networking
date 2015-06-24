package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;

/**
 * A {@code DataType} representing a {@code Boolean} object
 * 
 * @author Christopher Lutz
 */
public class BooleanType extends DataType {

	/** {@inheritDoc} */
	@Override
	public Class<?> getTypeClass() {

		return Boolean.class;
	}

	/** {@inheritDoc} */
	@Override
	public String getAbbreviation() {

		return "bool";
	}

	/** {@inheritDoc} */
	@Override
	public Object readType(String toRead) {

		try {

			return Boolean.parseBoolean(toRead);

		} catch (Exception e) {

			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
