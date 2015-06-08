package com.github.lutzblox.databases.saving;

import com.github.lutzblox.utils.ExtendedMap;

public interface SaveMethod {

	public ExtendedMap load();
	
	public void save(ExtendedMap data);
}
