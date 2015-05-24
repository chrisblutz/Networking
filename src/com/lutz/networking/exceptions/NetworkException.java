package com.lutz.networking.exceptions;

public class NetworkException extends RuntimeException {

	private static final long serialVersionUID = -2101644076998281656L;

	public NetworkException(String message){
		
		super(message);
	}
}
