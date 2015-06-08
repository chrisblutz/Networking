package com.lutz.networking.databases;

public class Response {

	public static final String RESPONSE_KEY = "net:res";

	private Object value;

	public Response(Object value) {

		this.value = value;
	}

	public Object getValue() {

		return value;
	}
}
