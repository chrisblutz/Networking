package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;

public class StringType extends DataType {

	@Override
	public Class<?> getTypeClass() {

		return String.class;
	}

	@Override
	public String getAbbreviation() {

		return "str";
	}

	@Override
	public Object readType(String toRead) {

		return toRead;
	}

	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
