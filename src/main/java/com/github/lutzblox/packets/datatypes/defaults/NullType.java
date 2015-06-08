package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;
import com.github.lutzblox.packets.datatypes.wrappers.Null;

public class NullType extends DataType {

	@Override
	public Class<?> getTypeClass() {

		return Null.class;
	}

	@Override
	public String getAbbreviation() {

		return "NUL";
	}

	@Override
	public Object readType(String toRead) {

		return null;
	}

	@Override
	public String writeType(Object toWrite) {

		return "null";
	}
}
