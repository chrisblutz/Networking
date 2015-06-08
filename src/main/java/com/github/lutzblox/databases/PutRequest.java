package com.github.lutzblox.databases;

public class PutRequest {

	public static final String PUT_REQUEST_KEY_KEY = "net:putreqkey";
	public static final String PUT_REQUEST_VALUE_KEY = "net:putreqval";

	private String dataKey;
	private Object value;

	public PutRequest(String dataKey, Object value) {

		this.dataKey = dataKey;
		this.value = value;
	}

	public String getDataKey() {

		return dataKey;
	}

	public Object getValue() {

		return value;
	}
}
