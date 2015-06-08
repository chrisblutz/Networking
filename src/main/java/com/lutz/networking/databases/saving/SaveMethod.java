package com.lutz.networking.databases.saving;

import com.lutz.networking.utils.ExtendedMap;

public interface SaveMethod {

	public ExtendedMap load();
	
	public void save(ExtendedMap data);
}
