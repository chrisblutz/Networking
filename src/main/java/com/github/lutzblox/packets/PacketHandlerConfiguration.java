package com.github.lutzblox.packets;

public class PacketHandlerConfiguration {

	private boolean ignoreErrors;
	
	public PacketHandlerConfiguration(boolean ignoreErrors) {
		
		this.ignoreErrors = ignoreErrors;
	}
	
	public void setIgnoreErrors(boolean ignoreErrors){
		
		this.ignoreErrors = ignoreErrors;
	}
	
	public boolean getIgnoreErrors(){
		
		return ignoreErrors;
	}
	
	public static PacketHandlerConfiguration getDefaultConfiguration(){
		
		return new PacketHandlerConfiguration(true);
	}
}
