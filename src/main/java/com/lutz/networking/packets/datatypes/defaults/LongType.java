package com.lutz.networking.packets.datatypes.defaults;

import com.lutz.networking.packets.datatypes.DataType;

public class LongType extends DataType {

	@Override
	public Class<?> getTypeClass() {

		return Long.class;
	}

	@Override
	public String getAbbreviation() {

		return "long";
	}

	@Override
	public Object readType(String toRead) {

		try {

			return Long.parseLong(toRead);

		} catch (Exception e) {

			return 0l;
		}
	}

	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
