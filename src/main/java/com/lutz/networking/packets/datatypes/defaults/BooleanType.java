package com.lutz.networking.packets.datatypes.defaults;

import com.lutz.networking.packets.datatypes.DataType;

public class BooleanType extends DataType {

	@Override
	public Class<?> getTypeClass() {

		return Boolean.class;
	}

	@Override
	public String getAbbreviation() {

		return "bool";
	}

	@Override
	public Object readType(String toRead) {

		try {

			return Boolean.parseBoolean(toRead);

		} catch (Exception e) {

			return false;
		}
	}

	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
