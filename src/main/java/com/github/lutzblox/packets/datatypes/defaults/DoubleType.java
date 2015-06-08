package com.github.lutzblox.packets.datatypes.defaults;

import com.github.lutzblox.packets.datatypes.DataType;

public class DoubleType extends DataType {

	@Override
	public Class<?> getTypeClass() {

		return Double.class;
	}

	@Override
	public String getAbbreviation() {

		return "dbl";
	}

	@Override
	public Object readType(String toRead) {

		try {

			return Double.parseDouble(toRead);

		} catch (Exception e) {

			return 0d;
		}
	}

	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
