package com.lutz.networking.packets.datatypes.defaults;

import com.lutz.networking.packets.datatypes.DataType;

public class FloatType extends DataType {

	@Override
	public Class<?> getTypeClass() {

		return Float.class;
	}

	@Override
	public String getAbbreviation() {

		return "flt";
	}

	@Override
	public Object readType(String toRead) {

		try {

			return Float.parseFloat(toRead);

		} catch (Exception e) {

			return 0f;
		}
	}

	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
