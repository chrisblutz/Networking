package com.lutz.networking.packets.datatypes.defaults;

import com.lutz.networking.packets.datatypes.DataType;

public class IntegerType extends DataType {

	@Override
	public Class<?> getTypeClass() {

		return Integer.class;
	}

	@Override
	public String getAbbreviation() {

		return "int";
	}

	@Override
	public Object readType(String toRead) {

		try {

			return Integer.parseInt(toRead);

		} catch (Exception e) {

			return 0;
		}
	}

	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
