package com.github.lutzblox.databases;

public class Request {

	public static final String REQUEST_KEY = "net:req";

	private String dataKey;

	public Request(String dataKey) {

		this.dataKey = dataKey;
	}

	public String getDataKey() {

		return dataKey;
	}
}
