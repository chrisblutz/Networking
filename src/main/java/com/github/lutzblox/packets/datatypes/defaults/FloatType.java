package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;

/**
 * A {@code DataType} representing a {@code Float} object
 * 
 * @author Christopher Lutz
 */
public class FloatType extends DataType {

	/** {@inheritDoc} */
	@Override
	public Class<?> getTypeClass() {

		return Float.class;
	}

	/** {@inheritDoc} */
	@Override
	public String getAbbreviation() {

		return "flt";
	}

	/** {@inheritDoc} */
	@Override
	public Object readType(String toRead) {

		try {

			return Float.parseFloat(toRead);

		} catch (Exception e) {

			return 0f;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
