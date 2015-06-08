package com.lutz.networking.packets.datatypes.defaults;

import com.lutz.networking.packets.datatypes.DataType;
import com.lutz.networking.packets.datatypes.wrappers.Null;

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
