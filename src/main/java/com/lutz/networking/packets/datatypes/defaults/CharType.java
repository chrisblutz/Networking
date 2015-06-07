package com.lutz.networking.packets.datatypes.defaults;

import com.lutz.networking.packets.datatypes.DataType;

public class CharType extends DataType {

	@Override
	public Class<?> getTypeClass() {

		return Character.class;
	}

	@Override
	public String getAbbreviation() {

		return "char";
	}

	@Override
	public Object readType(String toRead) {

		if (toRead.length() >= 1) {

			return toRead.charAt(0);

		} else {

			return '\0';
		}
	}

	@Override
	public String writeType(Object toWrite) {

		return toWrite.toString();
	}
}
