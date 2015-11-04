package com.github.lutzblox.databases;

/**
 * A class representing a command for a {@code DatabaseServer}
 *
 * @author Christopher Lutz
 */
public class Command {

    /**
     * The {@code Packet} data key representing a command
     */
    public static final String COMMAND_KEY = "net:com";

    private String command;

    /**
     * Creates a new database {@code Command} to send across a
     * {@code DatabaseClient}
     *
     * @param command The command to send
     */
    public Command(String command) {

        this.command = command;
    }

    /**
     * Gets the command to be sent
     *
     * @return The command that will be sent
     */
    public String getCommand() {

        return command;
    }
}
