package com.github.lutzblox.databases;

public class Command {

	public static final String COMMAND_KEY = "net:com";

	private String command;

	public Command(String command) {

		this.command = command;
	}

	public String getCommand() {

		return command;
	}
}
